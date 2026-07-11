package br.com.marhainvest.portfolio.domain;

import java.util.List;

public record Portfolio(
        List<PortfolioPosition> positions
) {

    public Portfolio withPurchase(
            String ticker,
            int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantidade deve ser maior que zero"
            );
        }

        var positionExists = positions.stream()
                .anyMatch(position ->
                        position.asset()
                                .ticker()
                                .equalsIgnoreCase(ticker)
                );

        if (!positionExists) {
            throw new IllegalArgumentException(
                    "Ativo não encontrado na carteira: " + ticker
            );
        }

        var updatedPositions = positions.stream()
                .map(position -> {

                    if (!position.asset()
                            .ticker()
                            .equalsIgnoreCase(ticker)) {

                        return position;
                    }

                    return new PortfolioPosition(
                            position.asset(),
                            position.quantity() + quantity,
                            position.averagePrice(),
                            position.targetQuantity()
                    );
                })
                .toList();

        return new Portfolio(updatedPositions);
    }
}