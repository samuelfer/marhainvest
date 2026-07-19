package br.com.marhainvest.recommendation.domain;
import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.score.domain.Score;
import java.math.BigDecimal;
import java.util.List;

public record Recommendation(
        Integer ranking,
        String ticker,
        AssetType assetType,
        RecommendationStatus status,
        BigDecimal currentPrice,
        int suggestedQuantity,
        BigDecimal estimatedCost,
        Score score,
        List<RecommendationAlert> alerts) {}
