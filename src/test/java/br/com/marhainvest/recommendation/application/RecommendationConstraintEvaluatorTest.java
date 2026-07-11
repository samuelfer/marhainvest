package br.com.marhainvest.recommendation.application;

import br.com.marhainvest.asset.domain.AssetCategory;
import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.portfolio.domain.Portfolio;
import br.com.marhainvest.portfolio.domain.PortfolioPosition;
import br.com.marhainvest.recommendation.domain.RecommendationPolicy;
import br.com.marhainvest.recommendation.domain.RecommendationStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendationConstraintEvaluatorTest {

    private final RecommendationConstraintEvaluator evaluator =
            new RecommendationConstraintEvaluator();

    private final RecommendationPolicy policy =
            RecommendationPolicy.defaultPolicy();

    @Test
    void shouldBlockFiiAlreadyAboveExposureLimit() {

        var position = createPosition(
                "XPLG11",
                200,
                new BigDecimal("100")
        );

        var otherFii = createPosition(
                "OTHER11",
                800,
                new BigDecimal("100")
        );

        var portfolio = new Portfolio(
                List.of(
                        position,
                        otherFii
                )
        );

        var result = evaluator.evaluate(
                portfolio,
                position,
                20,
                policy
        );

        assertThat(result.status())
                .isEqualTo(
                        RecommendationStatus.CONCENTRATION_LIMIT
                );

        assertThat(result.allowedQuantity())
                .isZero();

        assertThat(result.alerts())
                .hasSize(1);

        assertThat(result.alerts().getFirst().type())
                .isEqualTo("FII_EXPOSURE");
    }

    @Test
    void shouldReduceQuantityWhenProjectedExposureExceedsLimit() {

        var position = createPosition(
                "GAME11",
                90,
                new BigDecimal("100")
        );

        var otherFii = createPosition(
                "OTHER11",
                910,
                new BigDecimal("100")
        );

        var portfolio = new Portfolio(
                List.of(
                        position,
                        otherFii
                )
        );

        var result = evaluator.evaluate(
                portfolio,
                position,
                20,
                policy
        );

        assertThat(result.status())
                .isEqualTo(
                        RecommendationStatus.OPPORTUNITY
                );

        assertThat(result.allowedQuantity())
                .isEqualTo(11);

        assertThat(result.alerts())
                .hasSize(1);

        assertThat(result.alerts().getFirst().type())
                .isEqualTo("FII_EXPOSURE_ADJUSTED");
    }

    @Test
    void shouldKeepSuggestedQuantityWhenProjectedExposureIsBelowLimit() {

        var position = createPosition(
                "VGIR11",
                50,
                new BigDecimal("100")
        );

        var otherFii = createPosition(
                "OTHER11",
                950,
                new BigDecimal("100")
        );

        var portfolio = new Portfolio(
                List.of(
                        position,
                        otherFii
                )
        );

        var result = evaluator.evaluate(
                portfolio,
                position,
                20,
                policy
        );

        assertThat(result.status())
                .isEqualTo(
                        RecommendationStatus.OPPORTUNITY
                );

        assertThat(result.allowedQuantity())
                .isEqualTo(20);

        assertThat(result.alerts())
                .isEmpty();
    }

    private PortfolioPosition createPosition(
            String ticker,
            int quantity,
            BigDecimal currentPrice) {

        var asset = new AssetSnapshot(
                ticker,
                AssetType.FII,
                AssetCategory.MULTI_STRATEGY,
                currentPrice,
                null,
                new BigDecimal("12"),
                new BigDecimal("0.90"),
                null,
                null,
                new BigDecimal("12")
        );

        return new PortfolioPosition(
                asset,
                quantity,
                currentPrice,
                null
        );
    }
}