package br.com.marhainvest.opportunity.application;

import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.opportunity.domain.OpportunityRule;
import br.com.marhainvest.score.application.ScoreRatingCalculator;
import br.com.marhainvest.score.domain.Score;
import br.com.marhainvest.score.domain.ScoreItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpportunityScoreCalculator {

    private final List<OpportunityRule> rules;
    private final ScoreRatingCalculator ratingCalculator;

    public Score calculate(AssetSnapshot asset) {

        var items = rules.stream()
                .map(rule -> rule.evaluate(asset))
                .toList();

        int total = items.stream()
                .mapToInt(ScoreItem::points)
                .sum();

        return new Score(
                total,
                ratingCalculator.calculate(total),
                items
        );
    }
}