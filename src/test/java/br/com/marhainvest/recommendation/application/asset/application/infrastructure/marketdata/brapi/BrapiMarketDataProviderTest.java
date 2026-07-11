package br.com.marhainvest.recommendation.application.asset.application.infrastructure.marketdata.brapi;

import br.com.marhainvest.asset.application.dividend.DividendYieldCalculator;
import br.com.marhainvest.asset.application.targetprice.FiiTargetPriceCalculator;
import br.com.marhainvest.asset.domain.AssetCategory;
import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.asset.infrastructure.marketdata.brapi.BrapiClient;
import br.com.marhainvest.asset.infrastructure.marketdata.brapi.BrapiMarketDataProvider;
import br.com.marhainvest.asset.infrastructure.marketdata.brapi.dto.BrapiQuote;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BrapiMarketDataProviderTest {

    @Test
    void shouldEnrichFiiWithRealPriceAndCalculatedIndicators() {

        var brapiClient = mock(BrapiClient.class);

        var quote = new BrapiQuote(
                "XPLG11",
                "XPLG11",
                "XP Log Fundo Investimento Imobiliario FII",
                "BRL",
                new BigDecimal("91.55"),
                new BigDecimal("92.00"),
                new BigDecimal("91.25"),
                new BigDecimal("-0.21"),
                new BigDecimal("-0.23"),
                Instant.parse("2026-07-10T21:31:30Z"),
                89390L,
                new BigDecimal("91.78"),
                new BigDecimal("91.99"),
                new BigDecimal("89.05"),
                new BigDecimal("106.76"),
                "https://icons.brapi.dev/icons/XPLG11.svg"
        );

        when(brapiClient.getQuote("XPLG11"))
                .thenReturn(quote);

        var provider = new BrapiMarketDataProvider(
                brapiClient,
                new DividendYieldCalculator(),
                new FiiTargetPriceCalculator()
        );

        var asset = new AssetSnapshot(
                "XPLG11",
                AssetType.FII,
                AssetCategory.LOGISTICS,
                null,
                null,
                null,
                new BigDecimal("0.87"),
                null,
                null,
                new BigDecimal("9.80")
        );

        var result = provider.enrich(asset);

        assertThat(result.currentPrice())
                .isEqualByComparingTo("91.55");

        assertThat(result.dividendYield())
                .isEqualByComparingTo("10.70");

        assertThat(result.targetPrice())
                .isEqualByComparingTo("98.00");

        assertThat(result.dpa())
                .isEqualByComparingTo("9.80");

        assertThat(result.pvp())
                .isEqualByComparingTo("0.87");
    }
}