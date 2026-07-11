package br.com.marhainvest.allocation.domain.application;

import br.com.marhainvest.allocation.domain.AllocationDecision;
import br.com.marhainvest.allocation.domain.AllocationItem;
import br.com.marhainvest.allocation.domain.AllocationPlan;
import br.com.marhainvest.portfolio.domain.Portfolio;
import br.com.marhainvest.recommendation.application.RecommendationEngine;
import br.com.marhainvest.recommendation.domain.RecommendationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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

            var currentScore = recommendation.score().total();

            allocations.compute(
                    ticker,
                    (key, allocation) -> {

                        if (allocation == null) {
                            return new MutableAllocation(
                                    ticker,
                                    1,
                                    price,
                                    currentScore
                            );
                        }

                        allocation.increment(currentScore);

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
        private final List<AllocationDecision> decisions =
                new ArrayList<>();

        private MutableAllocation(
                String ticker,
                int quantity,
                BigDecimal unitPrice,
                int score) {

            this.ticker = ticker;
            this.quantity = quantity;
            this.unitPrice = unitPrice;

            addDecision(score);
        }

        private void increment(int score) {
            quantity++;
            addDecision(score);
        }

        private void addDecision(int score) {

            var lastDecision = decisions.isEmpty()
                    ? null
                    : decisions.getLast();

            if (lastDecision != null
                    && lastDecision.score() == score) {
                return;
            }

            decisions.add(
                    new AllocationDecision(
                            quantity,
                            score
                    )
            );
        }

        private AllocationItem toAllocationItem() {

            return new AllocationItem(
                    ticker,
                    quantity,
                    unitPrice,
                    unitPrice.multiply(
                            BigDecimal.valueOf(quantity)
                    ),
                    List.copyOf(decisions)
            );
        }
    }
}