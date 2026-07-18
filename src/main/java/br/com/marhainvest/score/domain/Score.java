package br.com.marhainvest.score.domain;

import java.util.List;

public record Score(
        int total,
        ScoreRating rating,
        List<ScoreItem> items
) {
    public Score {
        items = List.copyOf(items);
    }
}