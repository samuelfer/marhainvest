package br.com.marhainvest.score.rule;

import br.com.marhainvest.score.domain.context.RecommendationContext;
import br.com.marhainvest.score.domain.ScoreRule;
import br.com.marhainvest.score.domain.ScoreItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DividendYieldRule implements ScoreRule {

 private static final BigDecimal MINIMUM_DIVIDEND_YIELD =
         BigDecimal.valueOf(10);

 private static final int POINTS = 20;

 @Override
 public ScoreItem evaluate(
         RecommendationContext context) {

  var dividendYield =
          context.position()
                  .asset()
                  .dividendYield();

  if (dividendYield == null) {
   return new ScoreItem(
           "DIVIDEND_YIELD",
           0,
           "Dividend Yield não informado"
   );
  }

  if (dividendYield
          .compareTo(MINIMUM_DIVIDEND_YIELD) < 0) {

   return new ScoreItem(
           "DIVIDEND_YIELD",
           0,
           String.format(
                   "DY de %.2f%% abaixo do mínimo de 10%%",
                   dividendYield
           )
   );
  }

  return new ScoreItem(
          "DIVIDEND_YIELD",
          POINTS,
          String.format(
                  "DY de %.2f%% atende ao mínimo de 10%%",
                  dividendYield
          )
  );
 }
}