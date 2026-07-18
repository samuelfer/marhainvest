package br.com.marhainvest.score.rule;

import br.com.marhainvest.score.domain.context.RecommendationContext;
import br.com.marhainvest.score.domain.ScoreRule;
import br.com.marhainvest.score.domain.ScoreItem;
import org.springframework.stereotype.Component;

@Component
public class GoalRule implements ScoreRule {

 private static final int MAX_POINTS = 20;

 @Override
 public ScoreItem evaluate(RecommendationContext context) {

  var position = context.position();

  if (!position.hasGoal()) {
   return new ScoreItem(
           "GOAL",
           0,
           "Ativo sem meta configurada"
   );
  }

  if (position.goalCompleted()) {
   return new ScoreItem(
           "GOAL",
           0,
           "Meta concluída"
   );
  }

  double progress =
          (double) position.quantity()
                  / position.targetQuantity();

  double remaining = 1 - progress;

  int points = (int) Math.round(
          remaining * MAX_POINTS
  );

  return new ScoreItem(
          "GOAL",
          points,
          String.format(
                  "%.2f%% da meta concluída",
                  progress * 100
          )
  );
 }
}