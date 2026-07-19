package br.com.marhainvest.opportunity.domain;

import br.com.marhainvest.score.domain.Score;

public record OpportunityEvaluation(
        OpportunityStatus status,
        Score score,
        String reason
) {}