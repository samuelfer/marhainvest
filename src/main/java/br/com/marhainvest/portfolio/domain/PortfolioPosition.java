package br.com.marhainvest.portfolio.domain;
import br.com.marhainvest.asset.domain.AssetSnapshot;
import java.math.BigDecimal;
public record PortfolioPosition(AssetSnapshot asset, int quantity, BigDecimal averagePrice, Integer targetQuantity) {
  public boolean hasGoal() { return targetQuantity != null && targetQuantity > 0; }
  public boolean goalCompleted() { return hasGoal() && quantity >= targetQuantity; }
}
