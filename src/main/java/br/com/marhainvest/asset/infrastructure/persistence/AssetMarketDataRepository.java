package br.com.marhainvest.asset.infrastructure.persistence;

import br.com.marhainvest.asset.domain.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssetMarketDataRepository
        extends JpaRepository<AssetMarketDataEntity, Long> {

    Optional<AssetMarketDataEntity>
        findFirstByAssetIdOrderByReferenceDateDesc(Long assetId);

    @Query("""
        select amd
        from AssetMarketDataEntity amd
        where amd.referenceDate = (
            select max(m.referenceDate)
            from AssetMarketDataEntity m
            where m.asset = amd.asset
        )
        and (:type is null or amd.asset.assetType = :type)
        order by amd.asset.ticker
        """)
    List<AssetMarketDataEntity> findLatestByType(
            @Param("type") AssetType type);
}