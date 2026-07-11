package br.com.marhainvest.asset.infrastructure.marketdata.brapi;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "brapi")
public record BrapiProperties(
        String baseUrl,
        String token
) {
}