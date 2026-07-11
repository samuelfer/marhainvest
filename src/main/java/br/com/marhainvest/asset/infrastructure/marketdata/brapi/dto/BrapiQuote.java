package br.com.marhainvest.asset.infrastructure.marketdata.brapi.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record BrapiQuote(
        String symbol,
        String shortName,
        String longName,
        String currency,
        BigDecimal regularMarketPrice,
        BigDecimal regularMarketDayHigh,
        BigDecimal regularMarketDayLow,
        BigDecimal regularMarketChange,
        BigDecimal regularMarketChangePercent,
        Instant regularMarketTime,
        Long regularMarketVolume,
        BigDecimal regularMarketPreviousClose,
        BigDecimal regularMarketOpen,
        BigDecimal fiftyTwoWeekLow,
        BigDecimal fiftyTwoWeekHigh,
        String logourl
) {
}