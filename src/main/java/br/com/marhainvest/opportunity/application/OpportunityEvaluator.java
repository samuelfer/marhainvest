package br.com.marhainvest.opportunity.application;

import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.opportunity.domain.*;
import br.com.marhainvest.portfolio.domain.Portfolio;
import br.com.marhainvest.score.domain.Score;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpportunityEvaluator {

    private final List<OpportunityRule> rules;

    public OpportunityEvaluation evaluate(
            AssetSnapshot snapshot,
            Portfolio portfolio,
            Score score) {

        var context = new OpportunityContext(
                snapshot,
                portfolio,
                score
        );

        for (OpportunityRule rule : rules) {

            var result = rule.evaluate(context);

            if (!result.approved()) {
                return new OpportunityEvaluation(
                        result.status(),
                        score,
                        result.reason()
                );
            }
        }

        return new OpportunityEvaluation(
                OpportunityStatus.OPPORTUNITY,
                score,
                "Ativo aprovado em todas as regras."
        );
    }
}