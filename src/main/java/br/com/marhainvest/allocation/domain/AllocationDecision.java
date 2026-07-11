package br.com.marhainvest.allocation.domain;

public record AllocationDecision(
        int quantity,
        int score
) {
}