package br.com.marhainvest.recommendation.application;

import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.portfolio.domain.PortfolioPosition;
import br.com.marhainvest.recommendation.domain.RecommendationPolicy;
import org.springframework.stereotype.Component;

@Component
public class RecommendationEligibility {

    public boolean isEligible(
            PortfolioPosition position,
            RecommendationPolicy policy) {

        var asset = position.asset();

        if (asset.currentPrice() == null
                || asset.currentPrice().signum() <= 0) {
            return false;
        }

        if (asset.targetPrice() == null) {
            return false;
        }

        if (asset.currentPrice()
                .compareTo(asset.targetPrice()) > 0) {
            return false;
        }

        if (asset.type() == AssetType.FII) {

            if (asset.dividendYield() == null
                    || asset.dividendYield()
                    .compareTo(policy.minimumFiiDividendYield()) < 0) {
                return false;
            }

            if (asset.pvp() == null) {
                return false;
            }
        }

        return true;
    }
}