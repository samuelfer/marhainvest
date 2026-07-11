package br.com.marhainvest.recommendation.application;

import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.portfolio.domain.Portfolio;
import br.com.marhainvest.portfolio.domain.PortfolioPosition;
import br.com.marhainvest.recommendation.domain.RecommendationAlert;
import br.com.marhainvest.recommendation.domain.RecommendationConstraint;
import br.com.marhainvest.recommendation.domain.RecommendationPolicy;
import br.com.marhainvest.recommendation.domain.RecommendationStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class RecommendationConstraintEvaluator {

    public RecommendationConstraint evaluate(
            Portfolio portfolio,
            PortfolioPosition position,
            int suggestedQuantity,
            RecommendationPolicy policy) {

        if (position.asset().type() != AssetType.FII) {
            return opportunity(suggestedQuantity);
        }

        var maximumExposure = policy.maximumFiiExposure();

        var totalFiiValue = portfolio.positions()
                .stream()
                .filter(this::isFii)
                .map(this::calculatePositionValue)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        if (totalFiiValue.signum() <= 0) {
            return opportunity(suggestedQuantity);
        }

        var positionValue = calculatePositionValue(position);

        var currentExposure = positionValue.divide(
                totalFiiValue,
                6,
                RoundingMode.HALF_UP
        );

        if (currentExposure.compareTo(maximumExposure) >= 0) {
            return concentrationLimit(
                    position,
                    currentExposure,
                    maximumExposure
            );
        }

        var price = position.asset().currentPrice();

        var allowedQuantity = calculateMaximumQuantity(
                totalFiiValue,
                positionValue,
                price,
                maximumExposure
        );

        allowedQuantity = Math.min(
                allowedQuantity,
                suggestedQuantity
        );

        if (allowedQuantity <= 0) {
            return concentrationLimit(
                    position,
                    currentExposure,
                    maximumExposure
            );
        }

        if (allowedQuantity < suggestedQuantity) {

            var projectedExposure = calculateProjectedExposure(
                    totalFiiValue,
                    positionValue,
                    price,
                    allowedQuantity
            );

            return new RecommendationConstraint(
                    allowedQuantity,
                    RecommendationStatus.OPPORTUNITY,
                    List.of(
                            new RecommendationAlert(
                                    "FII_EXPOSURE_ADJUSTED",
                                    String.format(
                                            "Quantidade ajustada de %d para %d cota(s) "
                                                    + "para respeitar o limite de %.2f%%. "
                                                    + "Exposição projetada: %.2f%%.",
                                            suggestedQuantity,
                                            allowedQuantity,
                                            maximumExposure
                                                    .multiply(BigDecimal.valueOf(100))
                                                    .doubleValue(),
                                            projectedExposure
                                                    .multiply(BigDecimal.valueOf(100))
                                                    .doubleValue()
                                    )
                            )
                    )
            );
        }

        return opportunity(suggestedQuantity);
    }

    private int calculateMaximumQuantity(
            BigDecimal totalFiiValue,
            BigDecimal positionValue,
            BigDecimal price,
            BigDecimal maximumExposure) {

        /*
         * (posição + compra) / (total FIIs + compra) <= limite
         *
         * compra <=
         * (limite * total - posição) / (1 - limite)
         */

        var maximumPurchaseValue = maximumExposure
                .multiply(totalFiiValue)
                .subtract(positionValue)
                .divide(
                        BigDecimal.ONE.subtract(maximumExposure),
                        2,
                        RoundingMode.DOWN
                );

        if (maximumPurchaseValue.signum() <= 0) {
            return 0;
        }

        return maximumPurchaseValue
                .divide(
                        price,
                        0,
                        RoundingMode.DOWN
                )
                .intValue();
    }

    private BigDecimal calculateProjectedExposure(
            BigDecimal totalFiiValue,
            BigDecimal positionValue,
            BigDecimal price,
            int quantity) {

        var purchaseValue = price.multiply(
                BigDecimal.valueOf(quantity)
        );

        return positionValue
                .add(purchaseValue)
                .divide(
                        totalFiiValue.add(purchaseValue),
                        6,
                        RoundingMode.HALF_UP
                );
    }

    private RecommendationConstraint opportunity(
            int quantity) {

        return new RecommendationConstraint(
                quantity,
                RecommendationStatus.OPPORTUNITY,
                List.of()
        );
    }

    private RecommendationConstraint concentrationLimit(
            PortfolioPosition position,
            BigDecimal exposure,
            BigDecimal maximumExposure) {

        return new RecommendationConstraint(
                0,
                RecommendationStatus.CONCENTRATION_LIMIT,
                List.of(
                        new RecommendationAlert(
                                "FII_EXPOSURE",
                                String.format(
                                        "%s representa %.2f%% da carteira de FIIs. "
                                                + "O limite configurado é %.2f%%.",
                                        position.asset().ticker(),
                                        exposure
                                                .multiply(BigDecimal.valueOf(100))
                                                .doubleValue(),
                                        maximumExposure
                                                .multiply(BigDecimal.valueOf(100))
                                                .doubleValue()
                                )
                        )
                )
        );
    }

    private boolean isFii(
            PortfolioPosition position) {

        return position.asset().type() == AssetType.FII;
    }

    private BigDecimal calculatePositionValue(
            PortfolioPosition position) {

        var price = position.asset().currentPrice();

        if (price == null) {
            return BigDecimal.ZERO;
        }

        return price.multiply(
                BigDecimal.valueOf(position.quantity())
        );
    }
}