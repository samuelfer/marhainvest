package br.com.marhainvest.recommendation.application.allocation.application;

import br.com.marhainvest.allocation.domain.application.PortfolioAllocationSimulator;
import br.com.marhainvest.asset.domain.AssetCategory;
import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.portfolio.domain.Portfolio;
import br.com.marhainvest.portfolio.domain.PortfolioPosition;
import br.com.marhainvest.recommendation.application.RecommendationEngine;
import br.com.marhainvest.recommendation.domain.Recommendation;
import br.com.marhainvest.recommendation.domain.RecommendationStatus;
import br.com.marhainvest.score.domain.Score;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PortfolioAllocationSimulatorTest {

    @Test
    void shouldChangeAssetWhenBestOpportunityChangesDuringSimulation() {

        var recommendationEngine =
                mock(RecommendationEngine.class);

        var simulator = new PortfolioAllocationSimulator(
                recommendationEngine
        );

        var portfolio = createPortfolio();

        var trxfRecommendation = createRecommendation(
                "TRXF11",
                "100.00",
                80
        );

        var vgirRecommendation = createRecommendation(
                "VGIR11",
                "50.00",
                90
        );

        when(recommendationEngine.recommend(
                any(Portfolio.class),
                any(BigDecimal.class)
        ))
                .thenReturn(List.of(trxfRecommendation))
                .thenReturn(List.of(vgirRecommendation))
                .thenReturn(List.of());

        var result = simulator.simulate(
                portfolio,
                new BigDecimal("200.00")
        );

        assertThat(result.initialMoney())
                .isEqualByComparingTo("200.00");

        assertThat(result.investedAmount())
                .isEqualByComparingTo("150.00");

        assertThat(result.remainingMoney())
                .isEqualByComparingTo("50.00");

        assertThat(result.allocations())
                .hasSize(2);

        assertThat(result.allocations())
                .extracting(allocation -> allocation.ticker())
                .containsExactly(
                        "TRXF11",
                        "VGIR11"
                );

        assertThat(result.allocations().get(0).quantity())
                .isEqualTo(1);

        assertThat(result.allocations().get(1).quantity())
                .isEqualTo(1);
    }

    private Portfolio createPortfolio() {

        var trxf = createPosition(
                "TRXF11",
                AssetCategory.URBAN_INCOME,
                "100.00"
        );

        var vgir = createPosition(
                "VGIR11",
                AssetCategory.PAPER,
                "50.00"
        );

        return new Portfolio(
                List.of(
                        trxf,
                        vgir
                )
        );
    }

    private PortfolioPosition createPosition(
            String ticker,
            AssetCategory category,
            String currentPrice) {

        var asset = new AssetSnapshot(
                ticker,
                AssetType.FII,
                category,
                new BigDecimal(currentPrice),
                new BigDecimal("120.00"),
                new BigDecimal("12.00"),
                new BigDecimal("0.90"),
                null,
                null,
                new BigDecimal("12.00")
        );

        return new PortfolioPosition(
                asset,
                10,
                new BigDecimal(currentPrice),
                null
        );
    }

    private Recommendation createRecommendation(
            String ticker,
            String currentPrice,
            int score) {

        return new Recommendation(
                1,
                ticker,
                RecommendationStatus.OPPORTUNITY,
                new BigDecimal(currentPrice),
                1,
                new BigDecimal(currentPrice),
                new Score(
                        score,
                        List.of()
                ),
                List.of()
        );
    }
}