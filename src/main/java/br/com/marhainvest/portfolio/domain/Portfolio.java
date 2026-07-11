package br.com.marhainvest.portfolio.domain;
import java.util.List;
public record Portfolio(List<PortfolioPosition> positions) {
    public Portfolio { positions = List.copyOf(positions);
    }
}
