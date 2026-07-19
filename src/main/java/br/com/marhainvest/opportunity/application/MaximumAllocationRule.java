package br.com.marhainvest.opportunity.application;

import br.com.marhainvest.opportunity.domain.OpportunityContext;
import br.com.marhainvest.opportunity.domain.OpportunityRule;
import br.com.marhainvest.opportunity.domain.OpportunityRuleResult;
import org.springframework.stereotype.Service;

@Service
public class MaximumAllocationRule implements OpportunityRule {

    @Override
    public OpportunityRuleResult evaluate(OpportunityContext context) {

        if (context.portfolio() == null) {
            return OpportunityRuleResult.success();
        }

        // aplicar regra de alocação...

        return OpportunityRuleResult.success();
    }
}