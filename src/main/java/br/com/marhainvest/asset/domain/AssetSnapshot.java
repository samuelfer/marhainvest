package br.com.marhainvest.asset.domain;
import java.math.BigDecimal;
public record AssetSnapshot(
        String ticker,
        AssetType type,
        AssetCategory category,
        BigDecimal currentPrice,
        BigDecimal targetPrice,
        BigDecimal dividendYield,
        BigDecimal pvp,
        BigDecimal roe,
        BigDecimal payout,
        BigDecimal dpa) {}
