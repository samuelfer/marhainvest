package br.com.marhainvest.score.application;
import br.com.marhainvest.score.domain.*;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class ScoreCalculator {
 private final List<RecommendationRule> rules;
 public ScoreCalculator(List<RecommendationRule> rules){
  this.rules=rules;
 }
 public Score calculate(RecommendationContext c){
  var items = rules.stream().map(r->r.evaluate(c)).toList();
  return new Score(items.stream().mapToInt(ScoreItem::points).sum(),items);
 }
}
