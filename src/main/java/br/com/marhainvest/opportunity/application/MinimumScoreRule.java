package br.com.marhainvest.opportunity.application;

import br.com.marhainvest.opportunity.domain.OpportunityContext;
import br.com.marhainvest.opportunity.domain.OpportunityRule;
import br.com.marhainvest.opportunity.domain.OpportunityRuleResult;
import br.com.marhainvest.opportunity.domain.OpportunityStatus;
import org.springframework.stereotype.Service;

@Service
public class MinimumScoreRule implements OpportunityRule {

    private static final int MINIMUM_SCORE = 45;

    @Override
    public OpportunityRuleResult evaluate(OpportunityContext context) {

        if (context.score().total() < MINIMUM_SCORE) {
            return OpportunityRuleResult.failure(
                    OpportunityStatus.LOW_SCORE,
                    "Score abaixo do mínimo.",
                    "MinimumScoreRule"
            );
        }

        return OpportunityRuleResult.success();
    }
}