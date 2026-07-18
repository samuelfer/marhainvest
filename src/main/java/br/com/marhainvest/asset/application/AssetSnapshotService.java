package br.com.marhainvest.asset.application;

import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.asset.infrastructure.mapper.AssetSnapshotMapper;
import br.com.marhainvest.asset.infrastructure.persistence.AssetMarketDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetSnapshotService {

    private final AssetMarketDataRepository repository;
    private final AssetSnapshotMapper mapper;
    private final AssetSnapshotEnricher enricher;

    public List<AssetSnapshot> findByType(
            AssetType type) {

        return repository.findLatestByType(type)
                .stream()
                .map(mapper::toSnapshot)
                .map(enricher::enrich)
                .toList();
    }
}