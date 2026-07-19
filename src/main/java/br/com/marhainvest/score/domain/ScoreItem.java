package br.com.marhainvest.score.domain;

public record ScoreItem(
        String rule,
        int points,
        String reason
) {

    public static ScoreItem zero(
            String rule,
            String reason) {

        return new ScoreItem(rule, 0, reason);
    }

    public static ScoreItem scored(
            String rule,
            int points,
            String reason) {

        return new ScoreItem(rule, points, reason);
    }
}