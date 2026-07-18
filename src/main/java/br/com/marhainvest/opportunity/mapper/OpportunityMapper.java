package br.com.marhainvest.opportunity.mapper;

import br.com.marhainvest.asset.domain.AssetSnapshot;
import br.com.marhainvest.opportunity.domain.OpportunityResponse;
import br.com.marhainvest.score.domain.Score;
import org.springframework.stereotype.Component;

@Component
public class OpportunityMapper {

    public OpportunityResponse toResponse(
            AssetSnapshot snapshot,
            Score score) {

        return new OpportunityResponse(

                snapshot.ticker(),

                snapshot.type(),

                snapshot.currentPrice(),

                snapshot.targetPrice(),

                snapshot.dividendYield(),

                snapshot.pvp(),

                score

        );
    }

}