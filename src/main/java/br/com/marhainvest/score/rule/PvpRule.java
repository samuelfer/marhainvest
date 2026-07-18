package br.com.marhainvest.score.rule;

import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.score.domain.context.RecommendationContext;
import br.com.marhainvest.score.domain.ScoreRule;
import br.com.marhainvest.score.domain.ScoreItem;
import br.com.marhainvest.score.domain.ScoreMath;
import org.springframework.stereotype.Component;

@Component
public class PvpRule implements ScoreRule {

 private static final int MAX_POINTS = 15;

 @Override
 public ScoreItem evaluate(RecommendationContext context) {

  var asset = context.position().asset();

  if (asset.type() != AssetType.FII) {
   return new ScoreItem(
           "PVP",
           0,
           "Regra de P/VP não aplicada ao ativo"
   );
  }

  if (asset.pvp() == null) {
   return new ScoreItem(
           "PVP",
           0,
           "P/VP não informado"
   );
  }

  int points = ScoreMath.inverseProportional(
          asset.pvp().doubleValue(),
          0.80,
          1.00,
          MAX_POINTS
  );

  return new ScoreItem(
          "PVP",
          points,
          String.format(
                  "P/VP de %.2f",
                  asset.pvp().doubleValue()
          )
  );
 }
}