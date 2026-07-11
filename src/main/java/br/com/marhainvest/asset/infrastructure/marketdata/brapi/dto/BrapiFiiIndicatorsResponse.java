package br.com.marhainvest.asset.infrastructure.marketdata.brapi.dto;

import java.util.List;

public record BrapiFiiIndicatorsResponse(
        List<BrapiFiiIndicators> fiis
) {
}