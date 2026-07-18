package br.com.marhainvest.score.rule;

import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.score.domain.context.RecommendationContext;
import br.com.marhainvest.score.domain.ScoreRule;
import br.com.marhainvest.score.domain.ScoreItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Essa class deverah evoluir para ver
 * ✓ estabilidade dos dividendos em 12/24 meses
 * ✓ queda recente dos dividendos
 * ✓ dividendos extraordinários
 * ✓ vacância
 * ✓ concentração por imóvel
 * ✓ concentração por inquilino
 * ✓ inadimplência
 * ✓ alavancagem
 * ✓ recorrência do resultado
 */
@Component
public class FiiMarketRiskRule implements ScoreRule {

    @Override
    public ScoreItem evaluate(
            RecommendationContext context) {

        var asset = context.position().asset();

        if (asset.type() != AssetType.FII) {
            return new ScoreItem(
                    "FII_RISK",
                    0,
                    "Regra de risco não aplicável ao ativo"
            );
        }

        var dy = asset.dividendYield();
        var pvp = asset.pvp();

        if (dy == null || pvp == null) {
            return new ScoreItem(
                    "FII_RISK",
                    0,
                    "Dados insuficientes para avaliar risco"
            );
        }

        if (dy.compareTo(
                BigDecimal.valueOf(20)
        ) >= 0) {

            return risk(
                    -20,
                    dy,
                    pvp,
                    "DY extremamente elevado"
            );
        }

        if (dy.compareTo(BigDecimal.valueOf(16)) >= 0
                && pvp.compareTo(BigDecimal.valueOf(0.70)) <= 0) {

            return risk(
                    -15,
                    dy,
                    pvp,
                    "DY elevado combinado com forte desconto patrimonial"
            );
        }

        if (dy.compareTo(BigDecimal.valueOf(14)) >= 0
                && pvp.compareTo(BigDecimal.valueOf(0.80)) <= 0) {

            return risk(
                    -10,
                    dy,
                    pvp,
                    "DY elevado combinado com desconto patrimonial"
            );
        }

        if (dy.compareTo(BigDecimal.valueOf(12)) >= 0
                && pvp.compareTo(BigDecimal.valueOf(0.70)) <= 0) {

            return risk(
                    -5,
                    dy,
                    pvp,
                    "Desconto patrimonial relevante exige atenção"
            );
        }

        return new ScoreItem(
                "FII_RISK",
                0,
                "Nenhum sinal relevante de risco identificado"
        );
    }

    private ScoreItem risk(
            int points,
            BigDecimal dy,
            BigDecimal pvp,
            String reason) {

        return new ScoreItem(
                "FII_RISK",
                points,
                "%s. DY de %s%% e P/VP de %s"
                        .formatted(
                                reason,
                                dy,
                                pvp
                        )
        );
    }
}