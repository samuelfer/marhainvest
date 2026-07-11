package br.com.marhainvest.portfolio.infrastructure.persistence;

import br.com.marhainvest.asset.infrastructure.persistence.AssetEntity;
import br.com.marhainvest.goal.infrastructure.persistence.GoalEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "portfolio_asset")
@Getter
@NoArgsConstructor
public class PortfolioAssetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortfolioEntity portfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private AssetEntity asset;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "average_price")
    private BigDecimal averagePrice;

    @Column(name = "target_price")
    private BigDecimal targetPrice;

    @OneToOne(
            mappedBy = "portfolioAsset",
            fetch = FetchType.LAZY
    )
    private GoalEntity goal;
}