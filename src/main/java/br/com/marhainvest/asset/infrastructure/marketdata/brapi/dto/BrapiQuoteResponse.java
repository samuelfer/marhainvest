package br.com.marhainvest.asset.infrastructure.marketdata.brapi.dto;

import java.time.Instant;
import java.util.List;

public record BrapiQuoteResponse(
        List<BrapiQuote> results,
        Instant requestedAt,
        Long took
) {
}