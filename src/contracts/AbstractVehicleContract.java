package contracts;

import company.InsuranceCompany;
import objects.Person;
import payment.ContractPaymentData;

public abstract class AbstractVehicleContract extends AbstractContract {
    protected Person beneficiary;

    public AbstractVehicleContract(String contractNumber, InsuranceCompany insurer, Person beneficiary, Person policyHolder, ContractPaymentData contractPaymentData, int coverageAmount) {
        super(contractNumber, insurer, policyHolder, contractPaymentData, coverageAmount);
        if (beneficiary != null && beneficiary.equals(policyHolder)) throw new IllegalArgumentException("Beneficiary and policy holder cannot be the same person!");
        this.beneficiary = beneficiary;
    }

    public void setBeneficiary(Person beneficiary) {
        if (beneficiary.equals(policyHolder)) throw new IllegalArgumentException("Beneficiary and policy holder cannot be the same person!");
        this.beneficiary = beneficiary;
    }

    public Person getBeneficiary() {
        return beneficiary;
    }
}
