package br.com.marhainvest.allocation.domain;

import java.math.BigDecimal;

public record AllocationItem(
        String ticker,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalCost
) {
}