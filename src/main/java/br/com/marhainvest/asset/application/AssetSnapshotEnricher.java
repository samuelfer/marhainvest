package br.com.marhainvest.asset.application;

import br.com.marhainvest.asset.application.targetprice.FiiTargetPriceCalculator;
import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.recommendation.domain.RecommendationPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssetSnapshotEnricher {

    private final FiiTargetPriceCalculator targetPriceCalculator;

    public AssetSnapshot enrich(
            AssetSnapshot snapshot) {

        if (snapshot.type() != AssetType.FII) {
            return snapshot;
        }

        var targetPrice =
                targetPriceCalculator.calculate(
                        snapshot,
                        RecommendationPolicy.defaultPolicy());

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