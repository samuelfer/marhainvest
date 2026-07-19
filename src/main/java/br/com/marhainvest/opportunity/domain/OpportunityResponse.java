package br.com.marhainvest.opportunity.domain;

import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.score.domain.Score;

import java.math.BigDecimal;

public record OpportunityResponse(
        String ticker,
        String category,
        AssetType assetType,
        BigDecimal currentPrice,
        BigDecimal targetPrice,
        BigDecimal dividendYield,
        BigDecimal pvp,
        OpportunityStatus status,
        String reason,
        Score score

) {
}