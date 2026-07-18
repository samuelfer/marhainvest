package br.com.marhainvest.opportunity.domain;

import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.score.domain.ScoreItem;

public interface OpportunityRule {
    ScoreItem evaluate(AssetSnapshot asset);
}