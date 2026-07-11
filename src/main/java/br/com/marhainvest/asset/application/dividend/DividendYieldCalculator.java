package br.com.marhainvest.asset.application.dividend;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DividendYieldCalculator {

    private static final BigDecimal ONE_HUNDRED =
            BigDecimal.valueOf(100);

    public BigDecimal calculate(
            BigDecimal dpa,
            BigDecimal currentPrice) {

        if (dpa == null || dpa.signum() <= 0) {
            return null;
        }

        if (currentPrice == null
                || currentPrice.signum() <= 0) {
            return null;
        }

        return dpa
                .divide(
                        currentPrice,
                        6,
                        RoundingMode.HALF_UP
                )
                .multiply(ONE_HUNDRED)
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }
}