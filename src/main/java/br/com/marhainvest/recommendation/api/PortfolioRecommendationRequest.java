package br.com.marhainvest.recommendation.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PortfolioRecommendationRequest(

        @NotNull
        Long portfolioId,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal availableMoney

) {
}