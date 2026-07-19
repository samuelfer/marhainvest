package br.com.marhainvest.opportunity.domain;

public interface OpportunityRule {
    OpportunityRuleResult evaluate(OpportunityContext context);
}