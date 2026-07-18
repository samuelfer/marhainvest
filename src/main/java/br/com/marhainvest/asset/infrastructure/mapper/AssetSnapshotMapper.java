package br.com.marhainvest.asset.infrastructure.mapper;

import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.asset.infrastructure.persistence.AssetMarketDataEntity;
import org.springframework.stereotype.Component;

@Component
public class AssetSnapshotMapper {

    public AssetSnapshot toSnapshot(
            AssetMarketDataEntity entity) {

        return new AssetSnapshot(
                entity.getAsset().getTicker(),
                entity.getAsset().getAssetType(),
                entity.getAsset().getAssetCategory(),
                entity.getCurrentPrice(),
                null,
                entity.getDividendYield(),
                entity.getPvp(),
                entity.getRoe(),
                entity.getPayout(),
                entity.getDpa()
        );
    }
}