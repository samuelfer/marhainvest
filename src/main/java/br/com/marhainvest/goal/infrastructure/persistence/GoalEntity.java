package br.com.marhainvest.goal.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import br.com.marhainvest.portfolio.infrastructure.persistence.PortfolioAssetEntity;

@Entity
@Table(name = "goal")
@Getter
@NoArgsConstructor
public class GoalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "portfolio_asset_id",
            nullable = false,
            unique = true
    )
    private PortfolioAssetEntity portfolioAsset;

    @Column(name = "target_quantity", nullable = false)
    private Integer targetQuantity;
}