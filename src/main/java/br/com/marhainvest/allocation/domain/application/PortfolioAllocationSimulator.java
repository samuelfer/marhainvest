package br.com.marhainvest.allocation.domain.application;

import br.com.marhainvest.allocation.domain.AllocationItem;
import br.com.marhainvest.allocation.domain.AllocationPlan;
import br.com.marhainvest.portfolio.domain.Portfolio;
import br.com.marhainvest.recommendation.application.RecommendationEngine;
import br.com.marhainvest.recommendation.domain.RecommendationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
public class PortfolioAllocationSimulator {

    private final RecommendationEngine recommendationEngine;

    public AllocationPlan simulate(
            Portfolio portfolio,
            BigDecimal money) {

        var initialMoney = money;
        var remainingMoney = money;
        var simulatedPortfolio = portfolio;

        var allocations = new LinkedHashMap<
                String,
                MutableAllocation
                >();

        while (remainingMoney.signum() > 0) {

            var recommendations =
                    recommendationEngine.recommend(
                            simulatedPortfolio,
                            remainingMoney
                    );

            var availableMoney = remainingMoney;


            var bestOpportunity = recommendations.stream()
                    .filter(recommendation ->
                            recommendation.status()
                                    == RecommendationStatus.OPPORTUNITY
                    )
                    .filter(recommendation ->
                            recommendation.currentPrice()
                                    .compareTo(availableMoney) <= 0
                    )
                    .findFirst();

            if (bestOpportunity.isEmpty()) {
                break;
            }

            var recommendation = bestOpportunity.get();

            var ticker = recommendation.ticker();
            var price = recommendation.currentPrice();

            simulatedPortfolio =
                    simulatedPortfolio.withPurchase(
                            ticker,
                            1
                    );

            remainingMoney =
                    remainingMoney.subtract(price);

            allocations.compute(
                    ticker,
                    (key, allocation) -> {

                        if (allocation == null) {
                            return new MutableAllocation(
                                    ticker,
                                    1,
                                    price
                            );
                        }

                        allocation.increment();

                        return allocation;
                    }
            );
        }

        var items = allocations.values()
                .stream()
                .map(MutableAllocation::toAllocationItem)
                .toList();

        var investedAmount =
                initialMoney.subtract(remainingMoney);

        return new AllocationPlan(
                initialMoney,
                investedAmount,
                remainingMoney,
                items
        );
    }

    private static class MutableAllocation {

        private final String ticker;
        private int quantity;
        private final BigDecimal unitPrice;

        private MutableAllocation(
                String ticker,
                int quantity,
                BigDecimal unitPrice) {

            this.ticker = ticker;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        private void increment() {
            quantity++;
        }

        private AllocationItem toAllocationItem() {

            return new AllocationItem(
                    ticker,
                    quantity,
                    unitPrice,
                    unitPrice.multiply(
                            BigDecimal.valueOf(quantity)
                    )
            );
        }
    }
}