package br.com.marhainvest.recommendation.domain;

public record RecommendationAlert(
        String type,
        String message
) {
}