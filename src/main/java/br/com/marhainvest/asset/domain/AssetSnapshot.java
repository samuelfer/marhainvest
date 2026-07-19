package br.com.marhainvest.asset.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record AssetSnapshot(

        String ticker,
        AssetType type,
        AssetCategory category,

        BigDecimal currentPrice,
        BigDecimal targetPrice,

        // Temporariamente persistido até conseguirmos calcular automaticamente
        BigDecimal dividendYield,

        BigDecimal patrimonialValuePerShare,
        BigDecimal roe,
        BigDecimal payout,
        BigDecimal dpa

) {

    private static final int SCALE = 2;
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    /**
     * Preço / Valor Patrimonial por Cota.
     */
    public BigDecimal pvp() {

        return divide(
                currentPrice,
                patrimonialValuePerShare
        );
    }

    /**
     * Margem de segurança em percentual.
     *
     * Exemplo:
     * Cotação:     R$ 90,00
     * Preço-teto:  R$ 100,00
     *
     * Resultado: 10%
     */
    public BigDecimal safetyMargin() {

        if (!isPositive(currentPrice) || !isPositive(targetPrice)) {
            return null;
        }

        return divide(
                targetPrice
                        .subtract(currentPrice)
                        .multiply(ONE_HUNDRED),
                targetPrice
        );
    }

    /**
     * Futuramente poderá substituir o campo dividendYield quando
     * o DPA anual passar a ser atualizado automaticamente.
     */
    public BigDecimal calculatedDividendYield() {

        if (!isPositive(currentPrice) || !isPositive(dpa)) {
            return null;
        }

        return divide(
                dpa.multiply(ONE_HUNDRED),
                currentPrice
        );
    }

    private BigDecimal divide(
            BigDecimal numerator,
            BigDecimal denominator) {

        if (numerator == null || !isPositive(denominator)) {
            return null;
        }

        return numerator.divide(
                denominator,
                SCALE,
                RoundingMode.HALF_UP
        );
    }

    private boolean isPositive(BigDecimal value) {
        return value != null && value.signum() > 0;
    }
}