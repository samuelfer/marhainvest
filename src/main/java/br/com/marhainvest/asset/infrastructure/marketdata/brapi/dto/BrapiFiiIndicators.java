package br.com.marhainvest.asset.infrastructure.marketdata.brapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BrapiFiiIndicators(
        String symbol,
        LocalDate asOfDate,
        BigDecimal price,
        BigDecimal priceToNav,
        BigDecimal dividendYield12m
) {
}