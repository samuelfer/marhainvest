# MarhaInvest API

Motor de decisão de aportes do **MarhaInvest**.

O objetivo da API é analisar uma carteira de investimentos e identificar as melhores oportunidades de aporte considerando:

- composição atual da carteira;
- diversificação;
- dividend yield;
- preço atual;
- preço-teto;
- margem de segurança;
- P/VP;
- metas de quantidade;
- concentração por ativo;
- sinais de risco em FIIs.

O MarhaInvest não busca simplesmente o ativo com maior Dividend Yield ou maior desconto.

O objetivo é responder:

> Dado o estado atual da minha carteira e o valor disponível para aporte, qual ativo representa a melhor oportunidade neste momento?

---

# Stack

- Java 24
- Spring Boot 3.5.4
- Maven
- PostgreSQL 16
- Spring Data JPA
- Flyway
- Springdoc / OpenAPI
- JUnit 5
- AssertJ

---

# Executando o projeto

Suba a infraestrutura:

```bash
docker compose up -d
```

Execute a aplicação:

```bash
mvn spring-boot:run
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

---

# Motor de recomendação

Endpoint principal:

```http
POST /api/v1/recommendations
```

O endpoint recebe o valor disponível para aporte e analisa os ativos existentes na carteira.

Exemplo:

```http
POST /api/v1/recommendations?portfolioId=1&money=2000
```

O motor retorna os ativos ordenados pelas melhores oportunidades de aporte.

Exemplo simplificado:

```json
[
  {
    "ranking": 1,
    "ticker": "TRXF11",
    "status": "OPPORTUNITY",
    "currentPrice": 91.10,
    "suggestedQuantity": 21,
    "estimatedCost": 1913.10,
    "score": {
      "total": 78
    },
    "alerts": []
  }
]
```

---

# Arquitetura do motor

O `RecommendationEngine` não conhece critérios específicos de investimento.

Ele é responsável por:

1. selecionar ativos elegíveis;
2. criar o contexto da recomendação;
3. calcular o score;
4. avaliar restrições da carteira;
5. calcular a quantidade sugerida;
6. ordenar as oportunidades;
7. gerar o ranking.

Os critérios de pontuação são implementados através da interface:

```java
RecommendationRule
```

O `ScoreCalculator` recebe automaticamente todas as implementações de `RecommendationRule` registradas no Spring.

```text
RecommendationEngine
        |
        v
ScoreCalculator
        |
        +-- DiversificationRule
        +-- DividendYieldRule
        +-- FiiMarketRiskRule
        +-- GoalRule
        +-- PvpRule
        +-- TargetPriceRule
```

Isso permite adicionar novas regras sem alterar o motor de recomendação.

Exemplo:

```java
@Component
public class NewRule implements RecommendationRule {

    @Override
    public ScoreItem evaluate(
            RecommendationContext context) {

        // regra

        return new ScoreItem(
                "NEW_RULE",
                10,
                "Motivo da pontuação"
        );
    }
}
```

Após registrar a classe como `@Component`, a regra passa a participar automaticamente do cálculo do score.

---

# Contexto da recomendação

Cada regra recebe um:

```java
RecommendationContext
```

O contexto contém:

- carteira;
- posição analisada;
- valor disponível para aporte.

Isso permite que uma regra analise não apenas o ativo isoladamente, mas também sua relação com a carteira.

Exemplo:

```text
Ativo: XPLG11
Segmento: LOGISTICS
Quantidade atual: 80
Valor atual da posição
Participação entre os FIIs
Quantidade de ativos do mesmo segmento
Valor disponível para aporte
```

---

# Score

O score de uma recomendação é formado pela soma dos pontos retornados pelas regras.

```text
Score =
    Diversificação
  + Dividend Yield
  + Risco
  + Meta
  + P/VP
  + Preço-teto
```

Exemplo:

```text
DIVERSIFICATION    +15
DIVIDEND_YIELD     +20
FII_RISK             0
GOAL               +16
PVP                 +5
TARGET_PRICE        +22
-----------------------
TOTAL                78
```

O score não representa uma garantia de retorno.

Ele representa o grau de aderência do ativo às políticas de aporte configuradas no MarhaInvest.

---

# Regras de pontuação

## DiversificationRule

Avalia a diversificação da carteira.

A regra considera:

- classe do ativo;
- segmento;
- percentual do segmento dentro da classe;
- quantidade de ativos existentes no segmento.

Exemplo:

```text
Segmento LOGISTICS representa 18,22% da classe FII
e possui 1 ativo(s)
```

Segmentos com menor representação podem receber maior pontuação.

O objetivo é evitar que novos aportes aumentem excessivamente a concentração em segmentos já predominantes.

---

## DividendYieldRule

Avalia o Dividend Yield anual do ativo.

Para FIIs, a política atual considera como referência mínima:

```text
10% ao ano
```

Exemplo:

```text
DY de 12,96% atende ao mínimo de 10%
```

Ativos que atendem ao DY mínimo recebem pontuação positiva.

O Dividend Yield não é analisado isoladamente.

Um DY muito elevado pode ser acompanhado por sinais de risco e sofrer penalização através da `FiiMarketRiskRule`.

---

## FiiMarketRiskRule

Avalia sinais simples de risco em Fundos Imobiliários.

A primeira versão da regra busca identificar a combinação de:

```text
Dividend Yield muito elevado
+
P/VP fortemente descontado
```

Essa combinação pode representar uma oportunidade, mas também pode indicar que o mercado está precificando algum risco.

Exemplo:

```text
DY: 17,65%
P/VP: 0,60
```

Resultado:

```text
FII_RISK: -15 pontos
```

Motivo:

```text
DY elevado combinado com forte desconto patrimonial
```

A regra não elimina o ativo das recomendações.

Ela reduz sua pontuação.

Isso permite que o ativo continue sendo analisado pelo motor sem transformar automaticamente um DY elevado e um P/VP baixo em uma excelente oportunidade.

A regra deverá evoluir futuramente considerando indicadores adicionais de risco.

---

## GoalRule

Avalia o progresso de uma meta de quantidade configurada para o ativo.

Exemplo:

```text
Meta: 100 cotas
Quantidade atual: 22 cotas
```

Progresso:

```text
22% da meta concluída
```

Ativos mais distantes da meta podem receber maior pontuação.

Exemplo:

```text
22% da meta concluída → 16 pontos
91% da meta concluída → 2 pontos
```

Ativos sem meta configurada recebem:

```text
0 pontos
```

A ausência de meta não impede que o ativo seja recomendado.

---

## PvpRule

Avalia o indicador Preço sobre Valor Patrimonial.

A regra é aplicada principalmente aos Fundos Imobiliários.

Exemplo:

```text
P/VP 0,87
```

significa que o ativo está sendo negociado abaixo do seu valor patrimonial.

Quanto maior o desconto patrimonial, maior pode ser a pontuação.

Entretanto, um P/VP extremamente baixo combinado com DY muito elevado pode gerar penalização através da `FiiMarketRiskRule`.

Isso evita analisar o P/VP isoladamente.

---

## TargetPriceRule

Avalia a margem de segurança entre a cotação atual e o preço-teto.

A margem de segurança é calculada através da fórmula:

```text
Margem de segurança =
    (Preço-teto - Cotação atual)
    / Preço-teto
    * 100
```

Exemplo:

```text
Cotação atual: R$ 91,10
Preço-teto: R$ 118,10
```

Resultado:

```text
Margem de segurança: 22,86%
```

Quanto maior a margem de segurança positiva, maior a pontuação.

A pontuação máxima atual da regra é:

```text
25 pontos
```

Caso a cotação esteja acima do preço-teto:

```text
TARGET_PRICE = 0 pontos
```

---

# Cálculo de preço-teto para FIIs

O preço-teto dos FIIs pode ser calculado pelo `FiiTargetPriceCalculator`.

A fórmula utilizada é:

```text
Preço-teto =
    DPA anual
    / Dividend Yield mínimo desejado
```

Onde:

```text
DPA = Dividendos por cota acumulados em 12 meses
```

Exemplo:

```text
DPA anual: R$ 11,81
DY mínimo desejado: 10%
```

Cálculo:

```text
11,81 / 0,10 = 118,10
```

Preço-teto:

```text
R$ 118,10
```

Isso significa que, considerando um DY mínimo desejado de 10%, o preço máximo calculado para o ativo seria R$ 118,10.

O preço-teto calculado é utilizado pela `TargetPriceRule` para calcular a margem de segurança.

---

# Dados de mercado

Os dados de mercado são representados pelo:

```java
AssetSnapshot
```

O snapshot pode conter:

- ticker;
- tipo do ativo;
- categoria;
- preço atual;
- preço-teto;
- Dividend Yield;
- P/VP;
- ROE;
- payout;
- DPA.

O `PortfolioLoader` cria o snapshot utilizando os dados persistidos mais recentes.

Em seguida, o snapshot pode ser enriquecido através de:

```java
MarketDataProvider
```

---

# MarketDataProvider

O `MarketDataProvider` abstrai a origem dos dados externos de mercado.

Implementação atual:

```text
BrapiMarketDataProvider
```

O provider consulta dados da BRAPI e atualiza informações de mercado disponíveis.

A arquitetura permite substituir a origem dos dados sem alterar o domínio ou o motor de recomendação.

```text
MarketDataProvider
        |
        +-- BrapiMarketDataProvider
        |
        +-- FutureProvider
```

O domínio não depende diretamente da BRAPI.

---

# BRAPI

A integração com a BRAPI é realizada através do:

```java
BrapiClient
```

Exemplo de consulta:

```http
GET /api/quote/XPLG11
```

Os dados disponíveis incluem:

- preço atual;
- máxima do dia;
- mínima do dia;
- variação;
- volume;
- abertura;
- fechamento anterior;
- mínima de 52 semanas;
- máxima de 52 semanas.

O plano gratuito utilizado atualmente não fornece todos os indicadores fundamentalistas necessários.

Por esse motivo, indicadores como:

- Dividend Yield;
- P/VP;
- DPA;

podem continuar sendo obtidos ou persistidos por outras fontes.

---

# Limite de concentração em FIIs

O MarhaInvest possui uma política de concentração por ativo dentro da carteira de FIIs.

Limite atual:

```text
10%
```

O percentual considera apenas ativos da classe:

```text
FII
```

Ações e FIAGROs não participam desse cálculo.

A exposição é calculada através de:

```text
Valor da posição do FII
/
Valor total da carteira de FIIs
```

---

# RecommendationConstraintEvaluator

O `RecommendationConstraintEvaluator` avalia restrições que não fazem parte do score.

Atualmente ele verifica a concentração de FIIs.

Existem dois cenários.

## Ativo já acima do limite

Exemplo:

```text
XPLG11 representa 18,23% da carteira de FIIs
Limite configurado: 10%
```

Resultado:

```text
status = CONCENTRATION_LIMIT
ranking = null
suggestedQuantity = 0
```

O ativo continua sendo retornado.

Isso é importante porque o usuário ainda pode visualizar:

- score;
- fundamentos;
- margem de segurança;
- motivo da restrição.

Exemplo de alerta:

```text
XPLG11 representa 18,23% da carteira de FIIs.
O limite configurado é 10,00%.
```

---

## Aporte ultrapassaria o limite

Caso o ativo esteja abaixo de 10%, mas a quantidade inicialmente calculada ultrapasse o limite, o motor reduz automaticamente a quantidade sugerida.

Exemplo:

```text
Quantidade inicial: 229 cotas
Quantidade ajustada: 175 cotas
```

Resultado projetado:

```text
Exposição: 9,99%
```

Alerta:

```text
FII_EXPOSURE_ADJUSTED
```

O ativo continua com:

```text
status = OPPORTUNITY
```

A quantidade sugerida passa a respeitar a política de concentração.

---

# Status da recomendação

## OPPORTUNITY

O ativo está elegível para aporte.

```json
{
  "status": "OPPORTUNITY"
}
```

Pode receber ranking e quantidade sugerida.

---

## CONCENTRATION_LIMIT

O ativo ultrapassa o limite de concentração configurado.

```json
{
  "status": "CONCENTRATION_LIMIT"
}
```

Nesse cenário:

```text
ranking = null
suggestedQuantity = 0
estimatedCost = 0
```

O ativo continua sendo retornado para fins de análise.

---

# RecommendationPolicy

As políticas de investimento são centralizadas no:

```java
RecommendationPolicy
```

Exemplos de políticas:

```text
Dividend Yield mínimo para FIIs
Exposição máxima por FII
```

A configuração padrão é obtida através de:

```java
RecommendationPolicy.defaultPolicy()
```

Centralizar essas decisões evita espalhar números mágicos pelas regras.

---

# Princípio do motor

O MarhaInvest separa três conceitos.

## Elegibilidade

Responde:

> O ativo pode participar da análise?

Responsabilidade:

```text
RecommendationEligibility
```

---

## Score

Responde:

> Quão interessante é o ativo neste momento?

Responsabilidade:

```text
ScoreCalculator
RecommendationRule
```

---

## Restrições

Responde:

> Mesmo sendo interessante, existe alguma limitação para realizar o aporte?

Responsabilidade:

```text
RecommendationConstraintEvaluator
```

Essa separação é importante.

Um ativo pode possuir:

```text
Score alto
```

e ainda assim apresentar:

```text
CONCENTRATION_LIMIT
```

Exemplo:

```text
XPLG11

Score: 64
Margem de segurança positiva
DY acima de 10%
P/VP atrativo

Porém:

18,23% da carteira de FIIs
```

O ativo continua interessante do ponto de vista de score, mas não é recomendado para novo aporte devido à concentração atual.

---

# Testes

As principais regras possuem testes unitários.

Exemplos:

```text
RecommendationEngineTest
FiiTargetPriceCalculatorTest
TargetPriceRuleTest
FiiMarketRiskRuleTest
RecommendationConstraintEvaluatorTest
BrapiMarketDataProviderTest
```

Os testes validam:

- cálculo de score;
- ordenação do ranking;
- cálculo de preço-teto;
- margem de segurança;
- penalização de risco;
- limite de concentração;
- ajuste automático da quantidade;
- integração do provider de dados de mercado.

Execute os testes com:

```bash
mvn test
```

---

# Evolução futura

Possíveis evoluções do motor:

- análise da estabilidade dos dividendos;
- histórico de Dividend Yield;
- vacância de FIIs;
- concentração por locatário;
- concentração por imóvel;
- qualidade da gestão;
- liquidez;
- análise de emissões;
- crescimento do DPA;
- score específico por tipo de FII;
- regras específicas para ações;
- regras específicas para FIAGRO;
- simulação iterativa de aportes;
- distribuição automática de um aporte entre vários ativos.

Uma evolução planejada é o:

```text
PortfolioAllocationSimulator
```

O objetivo será responder:

> Como distribuir um valor disponível entre diferentes ativos buscando melhorar a carteira após cada compra simulada?

---

# Objetivo do projeto

O MarhaInvest não pretende prever o mercado.

O objetivo é transformar uma estratégia pessoal de investimento em regras explícitas, testáveis e evolutivas.

Em vez de decidir um aporte apenas por percepção:

```text
"Esse ativo parece barato"
```

o motor busca responder:

```text
"Este ativo possui boa margem de segurança,
atende ao retorno mínimo desejado,
contribui para a carteira
e respeita os limites de concentração definidos."
```