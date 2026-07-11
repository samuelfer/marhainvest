package br.com.marhainvest.recommendation.domain;

import java.util.List;

public record RecommendationConstraint(
        int allowedQuantity,
        RecommendationStatus status,
        List<RecommendationAlert> alerts
) {
}