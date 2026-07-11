package br.com.marhainvest.asset.infrastructure.marketdata.brapi.dto;

public record BrapiQuoteResult(
        String requestedSymbol,
        String symbol,
        boolean changed,
        BrapiQuote data
) {
}