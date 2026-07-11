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
import br.com.marhainvest.score.domain.RecommendationRule;
import br.com.marhainvest.score.rule.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PortfolioAllocationRealScenarioTest {

    @Test
    void shouldAllocateMoneyUsingRealRecommendationRules() {

        List<RecommendationRule> rules = List.of(
                new DiversificationRule(),
                new DividendYieldRule(),
                new FiiMarketRiskRule(),
                new GoalRule(),
                new PvpRule(),
                new TargetPriceRule()
        );

        var engine = new RecommendationEngine(
                new ScoreCalculator(rules),
                new RecommendationEligibility(),
                new RecommendationConstraintEvaluator()
        );

        var simulator = new PortfolioAllocationSimulator(
                engine
        );

        var portfolio = new Portfolio(
                List.of(
                        createPosition(
                                "TRXF11",
                                AssetCategory.URBAN_INCOME,
                                "91.10",
                                "118.10",
                                "12.96",
                                "0.93",
                                "11.81",
                                22,
                                100
                        ),
                        createPosition(
                                "GAME11",
                                AssetCategory.MULTI_STRATEGY,
                                "8.70",
                                "10.50",
                                "13.45",
                                "0.91",
                                "1.17",
                                100,
                                null
                        ),
                        createPosition(
                                "VGIR11",
                                AssetCategory.PAPER,
                                "9.83",
                                "11.00",
                                "15.56",
                                "1.00",
                                "1.53",
                                20,
                                null
                        ),
                        createPosition(
                                "HGLG11",
                                AssetCategory.LOGISTICS,
                                "150.00",
                                "170.00",
                                "9.00",
                                "0.95",
                                "13.50",
                                100,
                                null
                        ),
                        createPosition(
                                "XPML11",
                                AssetCategory.SHOPPING,
                                "106.00",
                                "115.00",
                                "9.00",
                                "0.97",
                                "9.54",
                                100,
                                null
                        ),
                        createPosition(
                                "KNRI11",
                                AssetCategory.MULTI_STRATEGY,
                                "160.00",
                                "180.00",
                                "9.00",
                                "0.90",
                                "14.40",
                                100,
                                null
                        )
                )
        );

        var result = simulator.simulate(
                portfolio,
                new BigDecimal("2000.00")
        );

        assertThat(result.initialMoney())
                .isEqualByComparingTo("2000.00");

        assertThat(result.investedAmount())
                .isPositive();

        assertThat(result.investedAmount())
                .isLessThanOrEqualTo(
                        new BigDecimal("2000.00")
                );

        assertThat(result.remainingMoney())
                .isGreaterThanOrEqualTo(BigDecimal.ZERO);

        assertThat(
                result.investedAmount()
                        .add(result.remainingMoney())
        ).isEqualByComparingTo("2000.00");

        assertThat(result.allocations())
                .isNotEmpty();

        assertThat(result.allocations())
                .allSatisfy(allocation -> {

                    assertThat(allocation.quantity())
                            .isPositive();

                    assertThat(allocation.totalCost())
                            .isEqualByComparingTo(
                                    allocation.unitPrice()
                                            .multiply(
                                                    BigDecimal.valueOf(
                                                            allocation.quantity()
                                                    )
                                            )
                            );
                });

        System.out.println("Valor inicial: " + result.initialMoney());
        System.out.println("Valor investido: " + result.investedAmount());
        System.out.println("Valor restante: " + result.remainingMoney());

        result.allocations().forEach(allocation ->
                System.out.printf(
                        "%s -> %d cotas x %s = %s%n",
                        allocation.ticker(),
                        allocation.quantity(),
                        allocation.unitPrice(),
                        allocation.totalCost()
                )
        );
    }

    private PortfolioPosition createPosition(
            String ticker,
            AssetCategory category,
            String currentPrice,
            String targetPrice,
            String dividendYield,
            String pvp,
            String dpa,
            int quantity,
            Integer targetQuantity) {

        var asset = new AssetSnapshot(
                ticker,
                AssetType.FII,
                category,
                new BigDecimal(currentPrice),
                new BigDecimal(targetPrice),
                new BigDecimal(dividendYield),
                new BigDecimal(pvp),
                null,
                null,
                new BigDecimal(dpa)
        );

        return new PortfolioPosition(
                asset,
                quantity,
                new BigDecimal(currentPrice),
                targetQuantity
        );
    }
}