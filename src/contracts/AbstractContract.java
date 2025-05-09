package contracts;

import company.InsuranceCompany;
import objects.Person;
import payment.ContractPaymentData;

import java.util.Objects;

public abstract class AbstractContract {
    private final String contractNumber;
    protected final InsuranceCompany insurer;
    protected final Person policyHolder;
    protected final ContractPaymentData contractPaymentData;
    protected int coverageAmount;
    protected boolean isActive;

    public AbstractContract(String contractNumber, InsuranceCompany insurer,
                            Person policyHolder, ContractPaymentData contractPaymentData,
                            int coverageAmount){
        if (contractNumber == null || contractNumber.isEmpty()) throw new IllegalArgumentException("Contract number cannot be null or empty string");
        if (insurer == null || policyHolder == null) throw  new IllegalArgumentException("Insurer or policy holder is null!");
        if (coverageAmount < 0) throw new IllegalArgumentException("Coverage amount must be non-negative!");

        this.insurer = insurer;

        if (isContractNumberTaken(contractNumber)) throw new IllegalArgumentException("Contract number has been already taken!");

        this.contractNumber = contractNumber;
        this.policyHolder = policyHolder;
        this.contractPaymentData = contractPaymentData;
        this.coverageAmount = coverageAmount;
        this.isActive = true;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public Person getPolicyHolder() {
        return policyHolder;
    }

    public InsuranceCompany getInsurer() {
        return insurer;
    }

    public int getCoverageAmount() {
        return coverageAmount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setInactive() {
        this.isActive = false;
    }

    public void setCoverageAmount(int coverageAmount) {
        if (coverageAmount < 0) throw new IllegalArgumentException("Coverage amount must be non-negative!");
        this.coverageAmount = coverageAmount;
    }

    public ContractPaymentData getContractPaymentData() {
        return contractPaymentData;
    }

    public void pay(int amount) {
        insurer.getHandler().pay(this, amount);
    }

    public void updateBalance() {
        insurer.chargePremiumOnContract(this);
    }

    private boolean isContractNumberTaken(String contractNumber) {
        for (AbstractContract contract : insurer.getContracts()) {
            if (contract.getContractNumber().equals(contractNumber)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractContract)) return false;
        AbstractContract that = (AbstractContract) o;
        return this.getContractNumber().equals(that.getContractNumber()) &&
                this.insurer.equals(that.insurer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContractNumber(), insurer);
    }
}
