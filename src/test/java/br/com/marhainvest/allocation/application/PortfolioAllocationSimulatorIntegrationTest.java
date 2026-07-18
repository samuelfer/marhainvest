package br.com.marhainvest.allocation.application;

import br.com.marhainvest.allocation.domain.application.PortfolioAllocationSimulator;
import br.com.marhainvest.asset.domain.AssetCategory;
import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.portfolio.domain.Portfolio;
import br.com.marhainvest.portfolio.domain.PortfolioPosition;
import br.com.marhainvest.recommendation.application.RecommendationConstraintEvaluator;
import br.com.marhainvest.recommendation.application.RecommendationEligibility;
import br.com.marhainvest.recommendation.application.RecommendationEngine;
import br.com.marhainvest.score.application.ScoreCalculator;
import br.com.marhainvest.score.domain.ScoreRule;
import br.com.marhainvest.score.rule.GoalRule;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PortfolioAllocationSimulatorIntegrationTest {

    @Test
    void shouldRecalculateRankingAfterEachVirtualPurchase() {

        List<ScoreRule> rules = List.of(
                new GoalRule()
        );

        var scoreCalculator = new ScoreCalculator(rules);

        var recommendationEngine = new RecommendationEngine(
                scoreCalculator,
                new RecommendationEligibility(),
                new RecommendationConstraintEvaluator()
        );

        var simulator = new PortfolioAllocationSimulator(
                recommendationEngine
        );

        var firstAsset = createPosition(
                "AAAA3",
                1,
                10
        );

        var secondAsset = createPosition(
                "BBBB3",
                5,
                10
        );

        var portfolio = new Portfolio(
                List.of(
                        firstAsset,
                        secondAsset
                )
        );

        var result = simulator.simulate(
                portfolio,
                new BigDecimal("700.00")
        );

        assertThat(result.initialMoney())
                .isEqualByComparingTo("700.00");

        assertThat(result.investedAmount())
                .isEqualByComparingTo("700.00");

        assertThat(result.remainingMoney())
                .isZero();

        assertThat(result.allocations())
                .extracting(allocation -> allocation.ticker())
                .contains(
                        "AAAA3",
                        "BBBB3"
                );
    }

    private PortfolioPosition createPosition(
            String ticker,
            int quantity,
            int targetQuantity) {

        var asset = new AssetSnapshot(
                ticker,
                AssetType.STOCK,
                AssetCategory.INSURANCE,
                new BigDecimal("100.00"),
                new BigDecimal("120.00"),
                new BigDecimal("12.00"),
                null,
                null,
                null,
                null
        );

        return new PortfolioPosition(
                asset,
                quantity,
                new BigDecimal("100.00"),
                targetQuantity
        );
    }
}