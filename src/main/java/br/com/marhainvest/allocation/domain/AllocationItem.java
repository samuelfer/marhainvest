package br.com.marhainvest.allocation.domain;

import java.math.BigDecimal;
import java.util.List;

public record AllocationItem(
        String ticker,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalCost,
        List<AllocationDecision> decisions
) {
}