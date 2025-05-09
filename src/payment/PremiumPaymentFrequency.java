package payment;

public enum PremiumPaymentFrequency {
    ANNUAL(12),
    SEMI_ANNUAL(6),
    QUARTERLY(3),
    MONTHLY(1);

    private final int valueInMonths;

    PremiumPaymentFrequency(int valueInMonths) {
        this.valueInMonths = valueInMonths;
    }

    public int getValueInMonths() {
        return valueInMonths;
    }
}
