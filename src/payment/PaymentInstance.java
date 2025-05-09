package payment;

import java.time.LocalDateTime;

public class PaymentInstance implements Comparable<PaymentInstance> {
    private final LocalDateTime paymentTime;
    private final int paymentAmount;

    public PaymentInstance(LocalDateTime paymentTime, int paymentAmount) {
        if (paymentTime == null || paymentAmount <= 0) throw new IllegalArgumentException("Payment time is null or payment amount is negative!");

        this.paymentTime = paymentTime;
        this.paymentAmount = paymentAmount;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public int getPaymentAmount() {
        return paymentAmount;
    }

    @Override
    public int compareTo(PaymentInstance o) {
        return this.paymentTime.compareTo(o.paymentTime);
    }
}
