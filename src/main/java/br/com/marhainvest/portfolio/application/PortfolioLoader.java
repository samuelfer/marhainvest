package br.com.marhainvest.portfolio.application;

import br.com.marhainvest.asset.application.MarketDataProvider;
import br.com.marhainvest.asset.application.targetprice.FiiTargetPriceCalculator;
import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.asset.infrastructure.persistence.AssetMarketDataRepository;
import br.com.marhainvest.portfolio.domain.Portfolio;
import br.com.marhainvest.portfolio.domain.PortfolioPosition;
import br.com.marhainvest.portfolio.infrastructure.persistence.PortfolioRepository;
import br.com.marhainvest.recommendation.domain.RecommendationPolicy;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PortfolioLoader {

    private final PortfolioRepository portfolioRepository;
    private final AssetMarketDataRepository marketDataRepository;
    private final MarketDataProvider marketDataProvider;
    private final FiiTargetPriceCalculator fiiTargetPriceCalculator;

    @Transactional(readOnly = true)
    public Portfolio load(Long portfolioId) {

        var portfolio = portfolioRepository
                .findById(portfolioId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Carteira não encontrada"
                        )
                );

        var policy = RecommendationPolicy.defaultPolicy();

        var positions = portfolio.getAssets()
                .stream()
                .map(position -> {

                    var asset = position.getAsset();

                    var marketData = marketDataRepository
                            .findFirstByAssetIdOrderByReferenceDateDesc(
                                    asset.getId()
                            )
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Dados de mercado não encontrados para "
                                                    + asset.getTicker()
                                    )
                            );

                    var localSnapshot = new AssetSnapshot(
                            asset.getTicker(),
                            asset.getAssetType(),
                            asset.getAssetCategory(),
                            marketData.getCurrentPrice(),
                            position.getTargetPrice(),
                            marketData.getDividendYield(),
                            marketData.getPvp(),
                            marketData.getRoe(),
                            marketData.getPayout(),
                            marketData.getDpa()
                    );

                    var snapshot = marketDataProvider.enrich(
                            localSnapshot
                    );

                    snapshot = calculateTargetPrice(
                            snapshot,
                            policy
                    );

                    Integer targetQuantity =
                            position.getGoal() != null
                                    ? position.getGoal()
                                    .getTargetQuantity()
                                    : null;

                    return new PortfolioPosition(
                            snapshot,
                            position.getQuantity(),
                            position.getAveragePrice(),
                            targetQuantity
                    );
                })
                .toList();

        return new Portfolio(positions);
    }

    private AssetSnapshot calculateTargetPrice(
            AssetSnapshot snapshot,
            RecommendationPolicy policy) {

        if (snapshot.type() != AssetType.FII) {
            return snapshot;
        }

        var targetPrice = fiiTargetPriceCalculator.calculate(
                snapshot,
                policy
        );

        return new AssetSnapshot(
                snapshot.ticker(),
                snapshot.type(),
                snapshot.category(),
                snapshot.currentPrice(),
                targetPrice,
                snapshot.dividendYield(),
                snapshot.pvp(),
                snapshot.roe(),
                snapshot.payout(),
                snapshot.dpa()
        );
    }
}