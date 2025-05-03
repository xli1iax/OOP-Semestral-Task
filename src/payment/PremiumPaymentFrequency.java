package payment;

public enum PremiumPaymentFrequency {
    ANNUAL,
    SEMI_ANNUAL,
    QUARTERLY,
    MONTHLY;

    public int getValueInMonths() {
        return 0;
    }
}
