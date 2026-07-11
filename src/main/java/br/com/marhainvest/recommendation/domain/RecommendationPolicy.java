package br.com.marhainvest.recommendation.domain;

import java.math.BigDecimal;

public record RecommendationPolicy(
        BigDecimal minimumFiiDividendYield,
        BigDecimal maximumFiiExposure
) {

    public static RecommendationPolicy defaultPolicy() {
        return new RecommendationPolicy(
                new BigDecimal("10"),
                new BigDecimal("0.10")
        );
    }
}