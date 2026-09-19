public class PercentCommission implements CommissionPolicy {

    private final double percent; // процент комиссии, например 1.0 для 1%

    public PercentCommission(double percent) {
        if (percent < 0) {
            throw new IllegalArgumentException("Процент комиссии не может быть отрицательным");
        }
        this.percent = percent;
    }

    @Override
    public double calculate(double amount) {
        return amount * (percent / 100.0);
    }
}