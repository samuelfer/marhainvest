package br.com.marhainvest.score.domain;

public final class ScoreMath {

    private ScoreMath() {
    }

    public static int proportional(
            double value,
            double minimum,
            double maximum,
            int maxPoints) {

        if (value <= minimum) {
            return 0;
        }

        if (value >= maximum) {
            return maxPoints;
        }

        double ratio = (value - minimum)
                / (maximum - minimum);

        return (int) Math.round(ratio * maxPoints);
    }

    public static int inverseProportional(
            double value,
            double minimum,
            double maximum,
            int maxPoints) {

        if (value <= minimum) {
            return maxPoints;
        }

        if (value >= maximum) {
            return 0;
        }

        double ratio = (maximum - value)
                / (maximum - minimum);

        return (int) Math.round(ratio * maxPoints);
    }
}