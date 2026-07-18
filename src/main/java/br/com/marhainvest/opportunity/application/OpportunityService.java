package br.com.marhainvest.opportunity.application;

import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.opportunity.domain.OpportunityResponse;
import br.com.marhainvest.opportunity.domain.OpportunityRule;
import br.com.marhainvest.recommendation.application.RecommendationEngine;
import br.com.marhainvest.score.domain.Score;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpportunityService {

    private final OpportunityEngine engine;

    public List<OpportunityResponse> findAll(AssetType assetType) {
        return engine.findAll(assetType);
    }
}