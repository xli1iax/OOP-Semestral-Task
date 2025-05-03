package contracts;

import company.InsuranceCompany;
import objects.Person;
import payment.ContractPaymentData;

public class AbstractContract {
    private final String contractNumber;
    protected final InsuranceCompany insurer;
    protected final Person policyHolder;
    protected final ContractPaymentData contractPaymentData;
    protected int coverageAmount;
    protected boolean isActive;

    public AbstractContract(String contractNumber, InsuranceCompany insurer,
                            Person policyHolder, ContractPaymentData contractPaymentData,
                            int coverageAmount){
        if (contractNumber == null || contractNumber.trim().isEmpty()
                || insurer == null || policyHolder == null) throw  new IllegalArgumentException("Please, check that you parameters are not null or empty string!");
        if (coverageAmount < 0) throw new IllegalArgumentException("Coverage amount must be non-negative!");

        this.contractNumber = contractNumber;
        this.insurer = insurer;
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

    }

    public void updateBalance() {

    }
}
