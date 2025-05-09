package payment;

import company.InsuranceCompany;
import contracts.AbstractContract;
import contracts.InvalidContractException;
import contracts.MasterVehicleContract;
import contracts.SingleVehicleContract;

import java.util.*;


public class PaymentHandler {
    private final Map<AbstractContract, Set<PaymentInstance>> paymentHistory;
    private final InsuranceCompany insurer;

    public PaymentHandler(InsuranceCompany insurer) {
        if (insurer == null) throw new IllegalArgumentException("Insurer cannot be null!");
        this.insurer = insurer;
        this.paymentHistory = new HashMap<>();
    }

    public Map<AbstractContract, Set<PaymentInstance>> getPaymentHistory() {
        return paymentHistory;
    }

    public void pay(MasterVehicleContract contract, int amount) {
        if (contract == null || amount <= 0) throw new IllegalArgumentException("Contract is null or amount is negative!");
        if (!contract.isActive() || !contract.getInsurer().equals(this.insurer)) throw new InvalidContractException("The contract is inactive or insurers didn't match!");
        if (contract.getChildContracts().isEmpty()) throw  new IllegalArgumentException("Child Contracts iis empty!");

        Set<SingleVehicleContract> childContracts = contract.getChildContracts();
        int amountToSaved = amount;
        for (SingleVehicleContract child : childContracts) {
            if (child.isActive() && child.getContractPaymentData().getOutstandingBalance() > 0) {
                int currentBalance = child.getContractPaymentData().getOutstandingBalance();
                if (amount >= currentBalance){
                    amount -= currentBalance;
                    child.getContractPaymentData().setOutstandingBalance(0);
                } else {
                    child.getContractPaymentData().setOutstandingBalance(currentBalance - amount);
                }
            }
        }

        while (amount > 0) {
            for (SingleVehicleContract child : contract.getChildContracts()) {
                if (child.isActive()) {
                    int premium = child.getContractPaymentData().getPremium();
                    int currentBalance = child.getContractPaymentData().getOutstandingBalance();
                    if (amount >= premium) {
                        child.getContractPaymentData().setOutstandingBalance(currentBalance - premium);
                        amount -= premium;
                    } else {
                        child.getContractPaymentData().setOutstandingBalance(currentBalance - amount);
                        amount = 0;
                    }
                }
            }
        }

        PaymentInstance payment = new PaymentInstance(insurer.getCurrentTime(), amountToSaved);

        if (!paymentHistory.containsKey(contract)) {
            paymentHistory.put(contract, new TreeSet<>());
        }

        paymentHistory.get(contract).add(payment);
    }

    public void pay(AbstractContract contract, int amount) {
        if (contract == null || amount <= 0) throw new IllegalArgumentException("Contract is null or amount is negative!");
        if (!contract.isActive() || !contract.getInsurer().equals(this.insurer)) throw new InvalidContractException("The contract is inactive or insurers didn't match!");
        contract.getContractPaymentData().setOutstandingBalance( contract.getContractPaymentData().getOutstandingBalance() - amount);

        PaymentInstance payment = new PaymentInstance(insurer.getCurrentTime(), amount);

        if (!paymentHistory.containsKey(contract)) {
            paymentHistory.put(contract, new TreeSet<>());
        }
        paymentHistory.get(contract).add(payment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentHandler)) return false;
        PaymentHandler that = (PaymentHandler) o;
        return this.paymentHistory.equals(that.paymentHistory) &&
                this.insurer.equals(that.insurer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentHistory, insurer);
    }
}
