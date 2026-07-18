package br.com.marhainvest.score.application;

import br.com.marhainvest.score.domain.ScoreRating;
import org.springframework.stereotype.Service;

@Service
public class ScoreRatingCalculator {

    public ScoreRating calculate(int total) {

        if (total >= 90) {
            return ScoreRating.EXCELLENT;
        }

        if (total >= 75) {
            return ScoreRating.GOOD;
        }

        if (total >= 60) {
            return ScoreRating.FAIR;
        }

        return ScoreRating.POOR;
    }
}