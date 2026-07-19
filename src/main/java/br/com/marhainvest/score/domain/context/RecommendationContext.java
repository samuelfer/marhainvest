package br.com.marhainvest.score.domain.context;

import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.portfolio.domain.Portfolio;
import br.com.marhainvest.portfolio.domain.PortfolioPosition;

import java.math.BigDecimal;

public record RecommendationContext(
        Portfolio portfolio,
        PortfolioPosition position,
        BigDecimal money
) implements ScoreContext {

    @Override
    public AssetSnapshot asset() {
        return position.asset();
    }
}