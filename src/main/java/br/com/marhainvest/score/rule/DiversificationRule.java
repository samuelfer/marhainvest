package br.com.marhainvest.score.rule;

import br.com.marhainvest.portfolio.domain.PortfolioPosition;
import br.com.marhainvest.score.domain.context.RecommendationContext;
import br.com.marhainvest.score.domain.ScoreRule;
import br.com.marhainvest.score.domain.ScoreItem;
import br.com.marhainvest.score.domain.context.ScoreContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Component
public class DiversificationRule implements ScoreRule {

    private static final int MAX_EXPOSURE_POINTS = 20;

    @Override
    public ScoreItem evaluate(
            ScoreContext context) {

        if (!(context instanceof RecommendationContext recommendation)) {
            return ScoreItem.zero(
                    "DIVERSIFICATION",
                    "Regra disponível apenas para recomendações."
            );
        }

        var asset = context.asset();

        var assetType = asset.type();
        var category = asset.category();

        if (category == null) {
            return new ScoreItem(
                    "DIVERSIFICATION",
                    0,
                    "Categoria do ativo não informada"
            );
        }

        var comparablePositions = ((RecommendationContext) context).portfolio()
                .positions()
                .stream()
                .filter(position ->
                        position.asset().type() == assetType
                )
                .toList();

        var totalAssetTypeValue = comparablePositions
                .stream()
                .map(this::calculatePositionValue)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        if (totalAssetTypeValue.signum() == 0) {
            return new ScoreItem(
                    "DIVERSIFICATION",
                    0,
                    "Patrimônio da classe não calculável"
            );
        }

        var categoryPositions = comparablePositions
                .stream()
                .filter(position ->
                        Objects.equals(
                                position.asset().category(),
                                category
                        )
                )
                .filter(position -> position.quantity() > 0)
                .toList();

        var categoryValue = categoryPositions
                .stream()
                .map(this::calculatePositionValue)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        var exposure = categoryValue.divide(
                totalAssetTypeValue,
                6,
                RoundingMode.HALF_UP
        );

        int exposurePoints = (int) Math.round(
                (1 - exposure.doubleValue())
                        * MAX_EXPOSURE_POINTS
        );

        int assetCount = categoryPositions.size();


        return new ScoreItem(
                "DIVERSIFICATION",
                exposurePoints,
                String.format("Segmento %s representa %.2f%% da classe %s e possui %d ativo(s)",
                        category,
                        exposure.doubleValue() * 100,
                        assetType,
                        assetCount
                )
        );
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