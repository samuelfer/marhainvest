package br.com.marhainvest.asset.infrastructure.marketdata.brapi;

import br.com.marhainvest.asset.infrastructure.marketdata.brapi.dto.BrapiFiiIndicators;
import br.com.marhainvest.asset.infrastructure.marketdata.brapi.dto.BrapiQuote;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/market-data")
@RequiredArgsConstructor
public class MarketDataController {

    private final BrapiClient brapiClient;

    @GetMapping("/{ticker}")
    public BrapiQuote getQuote(
            @PathVariable String ticker) {
        return brapiClient.getQuote(ticker);
    }

    @GetMapping("/{ticker}/indicators")
    public BrapiFiiIndicators getIndicators(
            @PathVariable String ticker) {
        return brapiClient.getFiiIndicators(ticker);
    }
}