# ADR-001 - Motor baseado em regras

## Decisão
O Score MarhaInvest será composto por implementações independentes de `RecommendationRule`.

## Motivos
- regras isoladas e testáveis;
- recomendação explicável;
- inclusão de novos critérios sem alterar o motor;
- preparação para estratégias configuráveis Bazin, Barsi e personalizadas.
