package br.com.marhainvest.recommendation.application;

import br.com.marhainvest.asset.domain.AssetCategory;
import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.portfolio.domain.Portfolio;
import br.com.marhainvest.portfolio.domain.PortfolioPosition;
import br.com.marhainvest.recommendation.domain.RecommendationStatus;
import br.com.marhainvest.score.application.ScoreCalculator;
import br.com.marhainvest.score.domain.ScoreRule;
import br.com.marhainvest.score.rule.DiversificationRule;
import br.com.marhainvest.score.rule.DividendYieldRule;
import br.com.marhainvest.score.rule.GoalRule;
import br.com.marhainvest.score.rule.PvpRule;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendationEngineTest {

 @Test
 void shouldRecommendTrxfWhenXplgExceedsFiiExposureLimit() {

  List<ScoreRule> rules = List.of(
          new DiversificationRule(),
          new DividendYieldRule(),
          new PvpRule(),
          new GoalRule()
  );

  var engine = new RecommendationEngine(
          new ScoreCalculator(rules),
          new RecommendationEligibility(),
          new RecommendationConstraintEvaluator()
  );

  var xplg = new PortfolioPosition(
          new AssetSnapshot(
                  "XPLG11",
                  AssetType.FII,
                  AssetCategory.LOGISTICS,
                  new BigDecimal("91.61"),
                  new BigDecimal("100"),
                  new BigDecimal("10.74"),
                  new BigDecimal("0.87"),
                  null,
                  null,
                  null
          ),
          80,
          new BigDecimal("95"),
          200
  );

  var trxf = new PortfolioPosition(
          new AssetSnapshot(
                  "TRXF11",
                  AssetType.FII,
                  AssetCategory.URBAN_INCOME,
                  new BigDecimal("91"),
                  new BigDecimal("95"),
                  new BigDecimal("12.93"),
                  new BigDecimal("0.93"),
                  null,
                  null,
                  null
          ),
          22,
          new BigDecimal("90"),
          100
  );

  /*
   * Representa o restante da carteira de FIIs.
   *
   * DY abaixo de 10%:
   * não deve ser elegível como oportunidade.
   *
   * Porém continua participando do cálculo
   * de exposição da carteira.
   */
  var otherFii = new PortfolioPosition(
          new AssetSnapshot(
                  "OTHER11",
                  AssetType.FII,
                  AssetCategory.CORPORATE_OFFICE,
                  new BigDecimal("100"),
                  new BigDecimal("100"),
                  new BigDecimal("5"),
                  BigDecimal.ONE,
                  null,
                  null,
                  null
          ),
          400,
          new BigDecimal("100"),
          null
  );

  var portfolio = new Portfolio(
          List.of(
                  xplg,
                  trxf,
                  otherFii
          )
  );

  var result = engine.recommend(
          portfolio,
          new BigDecimal("2000")
  );

  var trxfRecommendation = result.stream()
          .filter(recommendation ->
                  recommendation.ticker().equals("TRXF11")
          )
          .findFirst()
          .orElseThrow();

  var xplgRecommendation = result.stream()
          .filter(recommendation ->
                  recommendation.ticker().equals("XPLG11")
          )
          .findFirst()
          .orElseThrow();

  assertThat(trxfRecommendation.status())
          .isEqualTo(RecommendationStatus.OPPORTUNITY);

  assertThat(trxfRecommendation.ranking())
          .isEqualTo(1);

  assertThat(xplgRecommendation.status())
          .isEqualTo(RecommendationStatus.CONCENTRATION_LIMIT);

  assertThat(xplgRecommendation.ranking())
          .isNull();

  assertThat(xplgRecommendation.suggestedQuantity())
          .isZero();

  assertThat(xplgRecommendation.alerts())
          .hasSize(1);

  assertThat(xplgRecommendation.alerts().getFirst().type())
          .isEqualTo("FII_EXPOSURE");
 }
}