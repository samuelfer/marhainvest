package br.com.marhainvest.opportunity.application;

import br.com.marhainvest.asset.application.AssetSnapshotService;
import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.opportunity.domain.OpportunityResponse;
import br.com.marhainvest.opportunity.domain.OpportunityStatus;
import br.com.marhainvest.opportunity.mapper.OpportunityMapper;
import br.com.marhainvest.portfolio.domain.Portfolio;
import br.com.marhainvest.score.application.ScoreCalculator;
import br.com.marhainvest.score.domain.context.OpportunityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpportunityEngine {

    private final AssetSnapshotService snapshotService;
    private final ScoreCalculator scoreCalculator;
    private final OpportunityEvaluator evaluator;
    private final OpportunityMapper mapper;

    public List<OpportunityResponse> findAll(AssetType type) {
        return findAll(null, type);
    }

    public List<OpportunityResponse> findAll(
            Portfolio portfolio,
            AssetType type) {

        return snapshotService.findByType(type)
                .stream()
                .map(snapshot -> buildOpportunity(snapshot, portfolio))
                .filter(opportunity ->
                        opportunity.status() == OpportunityStatus.OPPORTUNITY
                )
                .sorted(
                        Comparator.comparingInt(
                                (OpportunityResponse o) -> o.score().total()
                        ).reversed()
                )
                .toList();
    }

    private OpportunityResponse buildOpportunity(
            AssetSnapshot snapshot,
            Portfolio portfolio) {

        var score = scoreCalculator.calculate(new OpportunityContext(snapshot));

        var evaluation = evaluator.evaluate(
                snapshot,
                portfolio,
                score
        );

        return mapper.toResponse(snapshot, evaluation);
    }
}