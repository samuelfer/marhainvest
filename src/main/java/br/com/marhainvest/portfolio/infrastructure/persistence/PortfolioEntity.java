package br.com.marhainvest.portfolio.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "portfolio")
@Getter
@NoArgsConstructor
public class PortfolioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(
            mappedBy = "portfolio",
            fetch = FetchType.LAZY
    )
    private List<PortfolioAssetEntity> assets = new ArrayList<>();
}