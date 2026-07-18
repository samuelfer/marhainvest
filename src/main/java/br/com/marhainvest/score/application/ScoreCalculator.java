package br.com.marhainvest.score.application;

import br.com.marhainvest.score.domain.ScoreRule;
import br.com.marhainvest.score.domain.Score;
import br.com.marhainvest.score.domain.context.RecommendationContext;
import br.com.marhainvest.score.domain.ScoreItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreCalculator {

 private final List<ScoreRule> rules;
 private final ScoreRatingCalculator ratingCalculator;

 public ScoreCalculator(
         List<ScoreRule> rules,
         ScoreRatingCalculator ratingCalculator) {

  this.rules = rules;
  this.ratingCalculator = ratingCalculator;
 }

 public Score calculate(RecommendationContext context) {

  List<ScoreItem> items = rules.stream()
          .map(rule -> rule.evaluate(context))
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