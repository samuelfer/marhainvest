package br.com.marhainvest.allocation.domain.application;

import br.com.marhainvest.allocation.domain.AllocationDecision;
import br.com.marhainvest.allocation.domain.AllocationExplanation;
import br.com.marhainvest.allocation.domain.AllocationItem;
import br.com.marhainvest.allocation.domain.AllocationPlan;
import br.com.marhainvest.portfolio.domain.Portfolio;
import br.com.marhainvest.recommendation.application.RecommendationEngine;
import br.com.marhainvest.recommendation.domain.RecommendationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

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
            var currentScore = recommendation.score().total();

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
                                    price,
                                    currentScore,
                                    availableMoney
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
        private final BigDecimal initialAvailableMoney;

        private final List<AllocationDecision> decisions =
                new ArrayList<>();

        private MutableAllocation(
                String ticker,
                int quantity,
                BigDecimal unitPrice,
                int score,
                BigDecimal initialAvailableMoney) {

            this.ticker = ticker;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.initialAvailableMoney = initialAvailableMoney;

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

            var initialScore = decisions
                    .getFirst()
                    .score();

            var finalScore = decisions
                    .getLast()
                    .score();

            var explanation = new AllocationExplanation(
                    initialScore,
                    finalScore,
                    initialAvailableMoney,
                    buildReason(
                            initialScore,
                            finalScore
                    )
            );

            return new AllocationItem(
                    ticker,
                    quantity,
                    unitPrice,
                    unitPrice.multiply(
                            BigDecimal.valueOf(quantity)
                    ),
                    List.copyOf(decisions),
                    explanation
            );
        }

        private String buildReason(
                int initialScore,
                int finalScore) {

            if (quantity == 1) {
                return """
                        Ativo selecionado como melhor oportunidade compatível \
                        com o saldo disponível de %s.
                        """
                        .formatted(
                                formatCurrency(initialAvailableMoney)
                        )
                        .trim();
            }

            if (initialScore > finalScore) {
                return """
                        Ativo selecionado com %s disponíveis e permaneceu \
                        como melhor oportunidade durante a simulação, mesmo \
                        com a redução do score de %d para %d.
                        """
                        .formatted(
                                formatCurrency(initialAvailableMoney),
                                initialScore,
                                finalScore
                        )
                        .trim();
            }

            return """
                    Ativo selecionado com %s disponíveis e permaneceu \
                    como melhor oportunidade durante %d compras virtuais, \
                    mantendo score %d.
                    """
                    .formatted(
                            formatCurrency(initialAvailableMoney),
                            quantity,
                            initialScore
                    )
                    .trim();
        }
        private static String formatCurrency( BigDecimal value) {
            var formatter = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR") );
            return formatter.format(value);
        }
    }
}