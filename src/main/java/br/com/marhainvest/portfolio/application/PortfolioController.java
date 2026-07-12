package br.com.marhainvest.portfolio.application;

import br.com.marhainvest.portfolio.domain.Portfolio;
import br.com.marhainvest.portfolio.infrastructure.persistence.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioLoader portfolioLoader;

    @GetMapping
    public List<PortfolioSummary> findAll() {
        return portfolioRepository.findAll()
                .stream()
                .map(portfolio -> new PortfolioSummary(
                        portfolio.getId(),
                        portfolio.getName()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public Portfolio findById(
            @PathVariable Long id) {

        return portfolioLoader.load(id);
    }
}