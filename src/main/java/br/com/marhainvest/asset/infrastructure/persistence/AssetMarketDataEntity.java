package br.com.marhainvest.asset.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_market_data")
@Getter
@NoArgsConstructor
public class AssetMarketDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private AssetEntity asset;

    @Column(name = "current_price", nullable = false)
    private BigDecimal currentPrice;

    @Column(name = "dividend_yield")
    private BigDecimal dividendYield;

    private BigDecimal pvp;

    private BigDecimal roe;

    private BigDecimal payout;

    @Column(name = "dpa")
    private BigDecimal dpa;

    @Column(name = "reference_date", nullable = false)
    private LocalDateTime referenceDate;
}