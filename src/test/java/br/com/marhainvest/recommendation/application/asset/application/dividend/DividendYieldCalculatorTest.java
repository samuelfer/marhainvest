package br.com.marhainvest.recommendation.application.asset.application.dividend;

import br.com.marhainvest.asset.application.dividend.DividendYieldCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class DividendYieldCalculatorTest {

    private final DividendYieldCalculator calculator =
            new DividendYieldCalculator();

    @Test
    void shouldCalculateDividendYield() {

        var dividendYield = calculator.calculate(
                new BigDecimal("9.80"),
                new BigDecimal("91.55")
        );

        assertThat(dividendYield)
                .isEqualByComparingTo("10.70");
    }

    @Test
    void shouldReturnNullWhenDpaIsNull() {

        var dividendYield = calculator.calculate(
                null,
                new BigDecimal("91.55")
        );

        assertThat(dividendYield).isNull();
    }

    @Test
    void shouldReturnNullWhenDpaIsZero() {

        var dividendYield = calculator.calculate(
                BigDecimal.ZERO,
                new BigDecimal("91.55")
        );

        assertThat(dividendYield).isNull();
    }

    @Test
    void shouldReturnNullWhenCurrentPriceIsNull() {

        var dividendYield = calculator.calculate(
                new BigDecimal("9.80"),
                null
        );

        assertThat(dividendYield).isNull();
    }

    @Test
    void shouldReturnNullWhenCurrentPriceIsZero() {

        var dividendYield = calculator.calculate(
                new BigDecimal("9.80"),
                BigDecimal.ZERO
        );

        assertThat(dividendYield).isNull();
    }
}