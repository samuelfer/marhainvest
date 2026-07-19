package br.com.marhainvest.opportunity.domain;

public record OpportunityRuleResult(

        boolean approved,

        OpportunityStatus status,

        String reason

) {

    public static OpportunityRuleResult success() {
        return new OpportunityRuleResult(
                true,
                null,
                null
        );
    }

    public static OpportunityRuleResult failure(
            OpportunityStatus status,
            String reason) {

        return new OpportunityRuleResult(
                false,
                status,
                reason
        );
    }
}