package br.com.marhainvest.asset.infrastructure.marketdata.brapi;

import br.com.marhainvest.asset.infrastructure.marketdata.brapi.dto.BrapiFiiIndicators;
import br.com.marhainvest.asset.infrastructure.marketdata.brapi.dto.BrapiFiiIndicatorsResponse;
import br.com.marhainvest.asset.infrastructure.marketdata.brapi.dto.BrapiQuote;
import br.com.marhainvest.asset.infrastructure.marketdata.brapi.dto.BrapiQuoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class BrapiClient {

    private final RestClient restClient;
    private final BrapiProperties properties;

    public BrapiQuote getQuote(String ticker) {

        var response = restClient
                .get()
                .uri(
                        properties.baseUrl()
                                + "/api/quote/{ticker}",
                        ticker
                )
                .headers(headers ->
                        headers.setBearerAuth(
                                properties.token().trim()
                        )
                )
                .retrieve()
                .body(BrapiQuoteResponse.class);

        if (response == null
                || response.results() == null
                || response.results().isEmpty()) {

            throw new IllegalStateException(
                    "Cotação não encontrada para " + ticker
            );
        }

        return response.results().getFirst();
    }

    public BrapiFiiIndicators getFiiIndicators(String ticker) {

        var response = restClient
                .get()
                .uri(
                        properties.baseUrl()
                                + "/api/v2/fii/indicators?symbols={ticker}",
                        ticker
                )
                .headers(headers ->
                        headers.setBearerAuth(
                                properties.token().trim()
                        )
                )
                .retrieve()
                .body(BrapiFiiIndicatorsResponse.class);

        if (response == null
                || response.fiis() == null
                || response.fiis().isEmpty()) {

            throw new IllegalStateException(
                    "Indicadores não encontrados para " + ticker
            );
        }

        return response.fiis().getFirst();
    }
}