package br.com.marhainvest.opportunity.domain;

public record OpportunityRuleResult(
        boolean approved,
        OpportunityStatus status,
        String reason,
        String rule
) {

    public static OpportunityRuleResult success() {
        return new OpportunityRuleResult(
                true,
                OpportunityStatus.APPROVED,
                null,
                null
        );
    }

    public static OpportunityRuleResult failure(
            OpportunityStatus status,
            String reason,
            String rule) {

        return new OpportunityRuleResult(
                false,
                status,
                reason,
                rule
        );
    }
}