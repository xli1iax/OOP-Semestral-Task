package contracts;

import company.InsuranceCompany;
import objects.Person;
import payment.ContractPaymentData;

import java.util.Set;

public class TravelContract extends AbstractContract{
    private final Set<Person> insuredPersons;

    public TravelContract(String contractNumber, InsuranceCompany insurer,
                          Person policyHolder, ContractPaymentData contractPaymentData,
                          int coverageAmount, Set<Person> personsToInsure) {
        super(contractNumber, insurer, policyHolder, contractPaymentData, coverageAmount);
        if (contractPaymentData == null) throw new IllegalArgumentException("Contract payment data can not be null!");
        if (personsToInsure == null || personsToInsure.isEmpty()) throw new IllegalArgumentException("Person to insure can not be null or empty!");
        this.insuredPersons = personsToInsure;
    }

    public Set<Person> getIsuredPersons() {
        return insuredPersons;
    }
}
