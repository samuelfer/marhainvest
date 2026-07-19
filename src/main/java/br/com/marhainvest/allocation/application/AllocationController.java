package br.com.marhainvest.allocation.application;

import br.com.marhainvest.allocation.domain.AllocationPlan;
import br.com.marhainvest.portfolio.application.PortfolioLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/allocations")
@RequiredArgsConstructor
public class AllocationController {

    private final PortfolioLoader portfolioLoader;
    private final br.com.marhainvest.allocation.domain.application.PortfolioAllocationSimulator simulator;

    @PostMapping("/simulate")
    public AllocationPlan simulate(@RequestParam Long portfolioId, @RequestParam BigDecimal money) {
        var portfolio = portfolioLoader.load(portfolioId);
        return simulator.simulate(portfolio, money);
    }
}