package br.com.marhainvest.asset.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetMarketDataRepository
        extends JpaRepository<AssetMarketDataEntity, Long> {

    Optional<AssetMarketDataEntity>
        findFirstByAssetIdOrderByReferenceDateDesc(Long assetId);
}