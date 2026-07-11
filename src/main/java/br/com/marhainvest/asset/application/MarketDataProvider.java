package br.com.marhainvest.asset.application;

import br.com.marhainvest.asset.domain.AssetSnapshot;

public interface MarketDataProvider {

    AssetSnapshot enrich(AssetSnapshot asset);
}