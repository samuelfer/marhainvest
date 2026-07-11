package br.com.marhainvest.asset.application.targetprice;

import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.recommendation.domain.RecommendationPolicy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FiiTargetPriceCalculator {

    private static final BigDecimal ONE_HUNDRED =
            BigDecimal.valueOf(100);

    public BigDecimal calculate(
            AssetSnapshot asset,
            RecommendationPolicy policy) {

        if (asset.type() != AssetType.FII) {
            throw new IllegalArgumentException(
                    "Cálculo de preço-teto aplicável apenas a FIIs"
            );
        }

        if (asset.dpa() == null
                || asset.dpa().signum() <= 0) {
            return null;
        }

        var minimumDividendYield =
                policy.minimumFiiDividendYield();

        if (minimumDividendYield == null
                || minimumDividendYield.signum() <= 0) {
            return null;
        }

        var requiredYield = minimumDividendYield.divide(
                ONE_HUNDRED,
                6,
                RoundingMode.HALF_UP
        );

        return asset.dpa().divide(
                requiredYield,
                2,
                RoundingMode.DOWN
        );
    }
}