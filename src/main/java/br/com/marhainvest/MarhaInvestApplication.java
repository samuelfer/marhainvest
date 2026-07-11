package br.com.marhainvest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import br.com.marhainvest.asset.infrastructure.marketdata.brapi.BrapiProperties;

@SpringBootApplication
@EnableConfigurationProperties(BrapiProperties.class)
public class MarhaInvestApplication { public static void main(String[] args) { SpringApplication.run(MarhaInvestApplication.class, args); } }
