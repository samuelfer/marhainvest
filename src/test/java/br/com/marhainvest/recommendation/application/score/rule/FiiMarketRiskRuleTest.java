package br.com.marhainvest.recommendation.application.score.rule;

import br.com.marhainvest.asset.domain.AssetCategory;
import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.portfolio.domain.Portfolio;
import br.com.marhainvest.portfolio.domain.PortfolioPosition;
import br.com.marhainvest.score.domain.RecommendationContext;
import br.com.marhainvest.score.rule.FiiMarketRiskRule;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FiiMarketRiskRuleTest {

    private final FiiMarketRiskRule rule =
            new FiiMarketRiskRule();

    @Test
    void shouldPenalizeHighDividendYieldAndStrongDiscount() {

        var context = createContext(
                AssetType.FII,
                new BigDecimal("17.65"),
                new BigDecimal("0.60")
        );

        var result = rule.evaluate(context);

        assertThat(result.rule())
                .isEqualTo("FII_RISK");

        assertThat(result.points())
                .isEqualTo(-15);

        assertThat(result.reason())
                .contains("DY elevado combinado com forte desconto patrimonial");
    }

    @Test
    void shouldNotPenalizeHighDividendYieldWithoutPatrimonialDiscount() {

        var context = createContext(
                AssetType.FII,
                new BigDecimal("15.56"),
                new BigDecimal("1.00")
        );

        var result = rule.evaluate(context);

        assertThat(result.points())
                .isZero();
    }

    @Test
    void shouldPenalizeElevatedDividendYieldAndPatrimonialDiscount() {

        var context = createContext(
                AssetType.FII,
                new BigDecimal("14.50"),
                new BigDecimal("0.75")
        );

        var result = rule.evaluate(context);

        assertThat(result.points())
                .isEqualTo(-10);
    }

    @Test
    void shouldNotApplyRuleToStock() {

        var context = createContext(
                AssetType.STOCK,
                new BigDecimal("17.65"),
                new BigDecimal("0.60")
        );

        var result = rule.evaluate(context);

        assertThat(result.points())
                .isZero();

        assertThat(result.reason())
                .contains("não aplicável");
    }

    @Test
    void shouldPenalizeAtExactRiskBoundary() {

        var context = createContext(
                AssetType.FII,
                new BigDecimal("16.00"),
                new BigDecimal("0.70")
        );

        var result = rule.evaluate(context);

        assertThat(result.points())
                .isEqualTo(-15);
    }

    private RecommendationContext createContext(
            AssetType type,
            BigDecimal dividendYield,
            BigDecimal pvp) {

        var asset = new AssetSnapshot(
                "TEST11",
                type,
                AssetCategory.MULTI_STRATEGY,
                new BigDecimal("10.00"),
                new BigDecimal("15.00"),
                dividendYield,
                pvp,
                null,
                null,
                new BigDecimal("1.50")
        );

        var position = new PortfolioPosition(
                asset,
                100,
                new BigDecimal("10.00"),
                null
        );

        var portfolio = new Portfolio(
                List.of(position)
        );

        return new RecommendationContext(
                portfolio,
                position,
                new BigDecimal("2000")
        );
    }
}