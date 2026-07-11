package br.com.marhainvest.recommendation.application;

import br.com.marhainvest.portfolio.domain.Portfolio;
import br.com.marhainvest.recommendation.domain.Recommendation;
import br.com.marhainvest.recommendation.domain.RecommendationPolicy;
import br.com.marhainvest.recommendation.domain.RecommendationStatus;
import br.com.marhainvest.score.application.ScoreCalculator;
import br.com.marhainvest.score.domain.RecommendationContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@AllArgsConstructor
public class RecommendationEngine {

    private final ScoreCalculator scoreCalculator;
    private final RecommendationEligibility eligibility;
    private final RecommendationConstraintEvaluator constraintEvaluator;

    public List<Recommendation> recommend(
            Portfolio portfolio,
            BigDecimal money) {

        var policy = RecommendationPolicy.defaultPolicy();

        var recommendations = portfolio.positions()
                .stream()
                .filter(position ->
                        position.asset().currentPrice() != null
                                && position.asset().currentPrice().signum() > 0
                )
                .filter(position ->
                        position.asset()
                                .currentPrice()
                                .compareTo(money) <= 0
                )
                .filter(position -> !position.goalCompleted())
                .filter(position ->
                        eligibility.isEligible(
                                position,
                                policy
                        )
                )
                .map(position -> {

                    var context = new RecommendationContext(
                            portfolio,
                            position,
                            money
                    );

                    var score = scoreCalculator.calculate(context);

                    int suggestedQuantity = money
                            .divide(
                                    position.asset().currentPrice(),
                                    0,
                                    RoundingMode.DOWN
                            )
                            .intValue();

                    if (position.hasGoal()) {
                        suggestedQuantity = Math.min(
                                suggestedQuantity,
                                position.targetQuantity()
                                        - position.quantity()
                        );
                    }

                    var constraint = constraintEvaluator.evaluate(
                            portfolio,
                            position,
                            suggestedQuantity,
                            policy
                    );

                    int allowedQuantity =
                            constraint.allowedQuantity();

                    var estimatedCost = position.asset()
                            .currentPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            allowedQuantity
                                    )
                            );

                    return new Recommendation(
                            null,
                            position.asset().ticker(),
                            constraint.status(),
                            position.asset().currentPrice(),
                            allowedQuantity,
                            estimatedCost,
                            score,
                            constraint.alerts()
                    );
                })
                .sorted(
                        Comparator
                                .comparing(
                                        (Recommendation recommendation) ->
                                                recommendation.status()
                                                        == RecommendationStatus.OPPORTUNITY
                                )
                                .reversed()
                                .thenComparing(
                                        Comparator.comparingInt(
                                                (Recommendation recommendation) ->
                                                        recommendation.score().total()
                                        ).reversed()
                                )
                )
                .toList();

        var rank = new AtomicInteger(1);

        return recommendations.stream()
                .map(recommendation -> {

                    Integer ranking =
                            recommendation.status()
                                    == RecommendationStatus.OPPORTUNITY
                                    ? rank.getAndIncrement()
                                    : null;

                    return new Recommendation(
                            ranking,
                            recommendation.ticker(),
                            recommendation.status(),
                            recommendation.currentPrice(),
                            recommendation.suggestedQuantity(),
                            recommendation.estimatedCost(),
                            recommendation.score(),
                            recommendation.alerts()
                    );
                })
                .toList();
    }
}