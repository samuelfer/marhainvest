package br.com.marhainvest.recommendation.application.asset.application;

import br.com.marhainvest.asset.application.targetprice.FiiTargetPriceCalculator;
import br.com.marhainvest.asset.domain.AssetCategory;
import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.recommendation.domain.RecommendationPolicy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FiiTargetPriceCalculatorTest {

    private final FiiTargetPriceCalculator calculator =
            new FiiTargetPriceCalculator();

    private final RecommendationPolicy policy =
            RecommendationPolicy.defaultPolicy();

    @Test
    void shouldCalculateFiiTargetPriceUsingDpaAndMinimumDividendYield() {

        var asset = createFii(new BigDecimal("9.80"));

        var targetPrice = calculator.calculate(
                asset,
                policy
        );

        assertThat(targetPrice)
                .isEqualByComparingTo("98.00");
    }

    @Test
    void shouldReturnNullWhenDpaIsNull() {

        var asset = createFii(null);

        var targetPrice = calculator.calculate(
                asset,
                policy
        );

        assertThat(targetPrice).isNull();
    }

    @Test
    void shouldReturnNullWhenDpaIsZero() {

        var asset = createFii(BigDecimal.ZERO);

        var targetPrice = calculator.calculate(
                asset,
                policy
        );

        assertThat(targetPrice).isNull();
    }

    @Test
    void shouldRejectAssetWhenItIsNotFii() {

        var asset = new AssetSnapshot(
                "BBAS3",
                AssetType.STOCK,
                AssetCategory.BANKING,
                new BigDecimal("32.00"),
                null,
                new BigDecimal("8.00"),
                null,
                new BigDecimal("20.00"),
                new BigDecimal("40.00"),
                new BigDecimal("3.20")
        );

        assertThatThrownBy(() ->
                calculator.calculate(asset, policy)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Cálculo de preço-teto aplicável apenas a FIIs"
                );
    }

    @Test
    void shouldUseMinimumDividendYieldFromPolicy() {

        var asset = createFii(
                new BigDecimal("9.80")
        );

        var customPolicy = new RecommendationPolicy(
                new BigDecimal("8"),
                new BigDecimal("0.10")
        );

        var targetPrice = calculator.calculate(
                asset,
                customPolicy
        );

        assertThat(targetPrice)
                .isEqualByComparingTo("122.50");
    }

    private AssetSnapshot createFii(
            BigDecimal dpa) {

        return new AssetSnapshot(
                "XPLG11",
                AssetType.FII,
                AssetCategory.LOGISTICS,
                new BigDecimal("91.55"),
                null,
                new BigDecimal("10.71"),
                new BigDecimal("0.87"),
                null,
                null,
                dpa
        );
    }
}