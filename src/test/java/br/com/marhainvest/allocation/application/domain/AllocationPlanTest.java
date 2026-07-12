package br.com.marhainvest.allocation.application.domain;

import br.com.marhainvest.allocation.domain.AllocationDecision;
import br.com.marhainvest.allocation.domain.AllocationExplanation;
import br.com.marhainvest.allocation.domain.AllocationItem;
import br.com.marhainvest.allocation.domain.AllocationPlan;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AllocationPlanTest {

    @Test
    void shouldRepresentPortfolioAllocationPlan() {

        var allocations = List.of(
                new AllocationItem(
                        "TRXF11",
                        10,
                        new BigDecimal("91.10"),
                        new BigDecimal("911.00"),
                        List.of(
                                new AllocationDecision(
                                        1,
                                        80
                                )
                        ),
                        new AllocationExplanation(
                                80,
                                80,
                                new BigDecimal("2000.00"),
                                "Ativo permaneceu como melhor oportunidade."
                        )
                ),
                new AllocationItem(
                        "VGIR11",
                        60,
                        new BigDecimal("9.83"),
                        new BigDecimal("589.80"),
                        List.of(
                                new AllocationDecision(
                                        1,
                                        70
                                )
                        ),
                        new AllocationExplanation(
                                70,
                                70,
                                new BigDecimal("1089.00"),
                                "Ativo permaneceu como melhor oportunidade."
                        )
                )
        );

        var plan = new AllocationPlan(
                new BigDecimal("2000.00"),
                new BigDecimal("1500.80"),
                new BigDecimal("499.20"),
                allocations
        );

        assertThat(plan.initialMoney())
                .isEqualByComparingTo("2000.00");

        assertThat(plan.investedAmount())
                .isEqualByComparingTo("1500.80");

        assertThat(plan.remainingMoney())
                .isEqualByComparingTo("499.20");

        assertThat(plan.allocations())
                .hasSize(2);

        var firstAllocation = plan.allocations()
                .getFirst();

        assertThat(firstAllocation.ticker())
                .isEqualTo("TRXF11");

        assertThat(firstAllocation.decisions())
                .hasSize(1);

        assertThat(
                firstAllocation
                        .decisions()
                        .getFirst()
                        .score()
        ).isEqualTo(80);

        assertThat(firstAllocation.explanation())
                .isNotNull();

        assertThat(firstAllocation.explanation().initialScore())
                .isEqualTo(80);

        assertThat(firstAllocation.explanation().finalScore())
                .isEqualTo(80);

        assertThat(
                firstAllocation
                        .explanation()
                        .initialAvailableMoney()
        ).isEqualByComparingTo("2000.00");

        assertThat(firstAllocation.explanation().reason())
                .isNotBlank();
    }
}