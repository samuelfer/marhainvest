package br.com.marhainvest.allocation.domain;

import java.math.BigDecimal;
import java.util.List;

public record AllocationPlan(
        BigDecimal initialMoney,
        BigDecimal investedAmount,
        BigDecimal remainingMoney,
        List<AllocationItem> allocations
) {
}