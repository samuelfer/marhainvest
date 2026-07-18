package br.com.marhainvest.asset.infrastructure.marketdata.brapi;

import br.com.marhainvest.asset.application.AssetSnapshotEnricher;
import br.com.marhainvest.asset.application.MarketDataProvider;
import br.com.marhainvest.asset.application.dividend.DividendYieldCalculator;
import br.com.marhainvest.asset.domain.AssetSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrapiMarketDataProvider implements MarketDataProvider {

    private final BrapiClient brapiClient;
    private final DividendYieldCalculator dividendYieldCalculator;
    private final AssetSnapshotEnricher assetSnapshotEnricher;

    @Override
    public AssetSnapshot enrich(AssetSnapshot asset) {

        var quote = brapiClient.getQuote(asset.ticker());

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
                null,
                dividendYield,
                asset.pvp(),
                asset.roe(),
                asset.payout(),
                asset.dpa()
        );

        return assetSnapshotEnricher.enrich(snapshot);
    }
}