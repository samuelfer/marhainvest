package br.com.marhainvest.opportunity.domain;

import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.portfolio.domain.Portfolio;
import br.com.marhainvest.score.domain.Score;

public record OpportunityContext(
        AssetSnapshot snapshot,
        Portfolio portfolio,
        Score score
) {
}