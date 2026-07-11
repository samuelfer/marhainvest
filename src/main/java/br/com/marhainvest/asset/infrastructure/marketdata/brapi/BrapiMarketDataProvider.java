package br.com.marhainvest.asset.infrastructure.marketdata.brapi;

import br.com.marhainvest.asset.application.MarketDataProvider;
import br.com.marhainvest.asset.application.dividend.DividendYieldCalculator;
import br.com.marhainvest.asset.application.targetprice.FiiTargetPriceCalculator;
import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.recommendation.domain.RecommendationPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrapiMarketDataProvider implements MarketDataProvider {

    private final BrapiClient brapiClient;
    private final DividendYieldCalculator dividendYieldCalculator;
    private final FiiTargetPriceCalculator fiiTargetPriceCalculator;

    @Override
    public AssetSnapshot enrich(AssetSnapshot asset) {

        var quote = brapiClient.getQuote(
                asset.ticker()
        );

        var currentPrice = quote.regularMarketPrice();

        var dividendYield = dividendYieldCalculator.calculate(
                asset.dpa(),
                currentPrice
        );

        var snapshot = new AssetSnapshot(
                asset.ticker(),
                asset.type(),
                asset.category(),
                currentPrice,
                asset.targetPrice(),
                dividendYield,
                asset.pvp(),
                asset.roe(),
                asset.payout(),
                asset.dpa()
        );

        if (asset.type() != AssetType.FII) {
            return snapshot;
        }

        var targetPrice = fiiTargetPriceCalculator.calculate(
                snapshot,
                RecommendationPolicy.defaultPolicy()
        );

        return new AssetSnapshot(
                snapshot.ticker(),
                snapshot.type(),
                snapshot.category(),
                snapshot.currentPrice(),
                targetPrice,
                snapshot.dividendYield(),
                snapshot.pvp(),
                snapshot.roe(),
                snapshot.payout(),
                snapshot.dpa()
        );
    }
}