package br.com.marhainvest.recommendation.application.portfolio.domain;

import br.com.marhainvest.asset.domain.AssetCategory;
import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.portfolio.domain.Portfolio;
import br.com.marhainvest.portfolio.domain.PortfolioPosition;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class PortfolioTest {

    @Test
    void shouldSimulatePurchaseWithoutChangingOriginalPortfolio() {

        var asset = new AssetSnapshot(
                "TRXF11",
                AssetType.FII,
                AssetCategory.URBAN_INCOME,
                new BigDecimal("91.10"),
                new BigDecimal("118.00"),
                new BigDecimal("12.96"),
                new BigDecimal("0.93"),
                null,
                null,
                new BigDecimal("11.81")
        );

        var position = new PortfolioPosition(
                asset,
                22,
                new BigDecimal("90.00"),
                100
        );

        var portfolio = new Portfolio(
                List.of(position)
        );

        var simulatedPortfolio = portfolio.withPurchase(
                "TRXF11",
                1
        );

        assertThat(
                portfolio.positions()
                        .getFirst()
                        .quantity()
        ).isEqualTo(22);

        assertThat(
                simulatedPortfolio.positions()
                        .getFirst()
                        .quantity()
        ).isEqualTo(23);
    }

    @Test
    void shouldAddMultipleUnitsToPosition() {

        var asset = new AssetSnapshot(
                "TRXF11",
                AssetType.FII,
                AssetCategory.URBAN_INCOME,
                new BigDecimal("91.10"),
                new BigDecimal("118.00"),
                new BigDecimal("12.96"),
                new BigDecimal("0.93"),
                null,
                null,
                new BigDecimal("11.81")
        );

        var position = new PortfolioPosition(
                asset,
                22,
                new BigDecimal("90.00"),
                100
        );

        var portfolio = new Portfolio(
                List.of(position)
        );

        var simulatedPortfolio = portfolio.withPurchase(
                "TRXF11",
                10
        );

        assertThat(
                simulatedPortfolio.positions()
                        .getFirst()
                        .quantity()
        ).isEqualTo(32);
    }

    @Test
    void shouldRejectInvalidPurchaseQuantity() {

        var portfolio = new Portfolio(
                List.of()
        );

        assertThat(
                org.assertj.core.api.Assertions
                        .catchThrowable(() ->
                                portfolio.withPurchase(
                                        "TRXF11",
                                        0
                                )
                        )
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectPurchaseWhenAssetDoesNotExist() {

        var portfolio = new Portfolio(
                List.of()
        );

        assertThatThrownBy(() ->
                portfolio.withPurchase(
                        "TRXF11",
                        1
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Ativo não encontrado na carteira: TRXF11"
                );
    }
}