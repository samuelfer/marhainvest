package br.com.marhainvest.opportunity.application;

import br.com.marhainvest.opportunity.domain.OpportunityContext;
import br.com.marhainvest.opportunity.domain.OpportunityRule;
import br.com.marhainvest.opportunity.domain.OpportunityRuleResult;
import br.com.marhainvest.opportunity.domain.OpportunityStatus;
import org.springframework.stereotype.Service;

@Service
public class MinimumScoreRule implements OpportunityRule {

    @Override
    public OpportunityRuleResult evaluate(OpportunityContext context) {

        if (context.score().total() < 80) {
            return OpportunityRuleResult.failure(
                    OpportunityStatus.LOW_SCORE,
                    "Score abaixo do mínimo."
            );
        }

        return OpportunityRuleResult.success();
    }
}