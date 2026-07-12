package br.com.marhainvest.allocation.domain;

import java.math.BigDecimal;

public record AllocationExplanation(
        int initialScore,
        int finalScore,
        BigDecimal initialAvailableMoney,
        String reason
) {
}