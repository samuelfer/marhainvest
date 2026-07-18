package br.com.marhainvest.opportunity.application;

import br.com.marhainvest.asset.application.AssetSnapshotService;
import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.opportunity.domain.OpportunityResponse;
import br.com.marhainvest.opportunity.mapper.OpportunityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpportunityEngine {

    private final AssetSnapshotService snapshotService;
    private final OpportunityScoreCalculator scoreCalculator;
    private final OpportunityMapper mapper;

    public List<OpportunityResponse> findAll(AssetType type) {

        return snapshotService.findByType(type)
                .stream()
                .map(this::calculate)
                .sorted(
                        Comparator.comparingInt(
                                (OpportunityResponse o) -> o.score().total()
                        ).reversed()
                )
                .toList();
    }

    private OpportunityResponse calculate(AssetSnapshot snapshot) {

        var score = scoreCalculator.calculate(snapshot);

        return mapper.toResponse(snapshot, score);
    }
}