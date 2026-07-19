package br.com.marhainvest.score.rule;

import br.com.marhainvest.score.domain.ScoreItem;
import br.com.marhainvest.score.domain.ScoreRule;
import br.com.marhainvest.score.domain.context.ScoreContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class TargetPriceRule implements ScoreRule {

    private static final int MAX_POINTS = 25;

    private static final BigDecimal ONE_HUNDRED =
            BigDecimal.valueOf(100);

    @Override
    public ScoreItem evaluate(ScoreContext context) {

        var asset = context.asset();

        var currentPrice = asset.currentPrice();
        var targetPrice = asset.targetPrice();

        if (currentPrice == null
                || currentPrice.signum() <= 0
                || targetPrice == null
                || targetPrice.signum() <= 0) {

            return new ScoreItem(
                    "TARGET_PRICE",
                    0,
                    "Preço-teto não disponível"
            );
        }

        var safetyMargin = targetPrice
                .subtract(currentPrice)
                .divide(
                        targetPrice,
                        6,
                        RoundingMode.HALF_UP
                )
                .multiply(ONE_HUNDRED);

        if (safetyMargin.signum() <= 0) {

            return new ScoreItem(
                    "TARGET_PRICE",
                    0,
                    "Cotação de R$ %s acima do preço-teto de R$ %s"
                            .formatted(
                                    currentPrice.setScale(
                                            2,
                                            RoundingMode.HALF_UP
                                    ),
                                    targetPrice.setScale(
                                            2,
                                            RoundingMode.HALF_UP
                                    )
                            )
            );
        }

        int points = Math.min(
                safetyMargin
                        .setScale(0, RoundingMode.DOWN)
                        .intValue(),
                MAX_POINTS
        );

        return new ScoreItem(
                "TARGET_PRICE",
                points,
                """
                Margem de segurança de %s%%.
                Cotação R$ %s e preço-teto R$ %s
                """.formatted(
                        safetyMargin.setScale(
                                2,
                                RoundingMode.HALF_UP
                        ),
                        currentPrice.setScale(
                                2,
                                RoundingMode.HALF_UP
                        ),
                        targetPrice.setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
                ).replace("\n", " ")
        );
    }
}