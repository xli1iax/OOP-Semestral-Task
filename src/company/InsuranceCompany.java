package company;

import contracts.*;
import objects.Person;
import objects.Vehicle;
import payment.ContractPaymentData;
import payment.PaymentHandler;
import payment.PremiumPaymentFrequency;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

public class InsuranceCompany {
    private final Set<AbstractContract> contracts;
    private final PaymentHandler handler;
    private LocalDateTime currentTime;

    public InsuranceCompany(LocalDateTime currentTime) {
        if (currentTime == null) throw new IllegalArgumentException("Current time cannot be null!");
        this.currentTime = currentTime;
        this.contracts = new LinkedHashSet<>();
        this.handler = new PaymentHandler(this);
    }

    public LocalDateTime getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(LocalDateTime currentTime) {
        if (currentTime == null) throw new IllegalArgumentException("Current time cannot be null!");
        this.currentTime = currentTime;
    }

    public Set<AbstractContract> getContracts() {
        return contracts;
    }

    public PaymentHandler getHandler() {
        return handler;
    }

    public SingleVehicleContract insureVehicle(String contractNumber, Person beneficiary, Person policyHolder, int proposedPremium, PremiumPaymentFrequency proposedPaymentFrequency, Vehicle vehicleToInsure) {
        if (vehicleToInsure == null ) throw  new IllegalArgumentException("Vehicle to insure cannot be null!");
        if (proposedPaymentFrequency == null) throw new IllegalArgumentException("Proposed payment frequency cannot be null!");
        if (proposedPremium <= 0) throw new IllegalArgumentException("Proposed premium must be positive");
        int annualPremiumTotal = proposedPremium * (12/proposedPaymentFrequency.getValueInMonths());

        if (annualPremiumTotal < 0.02*vehicleToInsure.getOriginalValue()) throw new IllegalArgumentException("The annual premium per year must be greater then 2!");
        ContractPaymentData contractPaymentData = new ContractPaymentData(proposedPremium, proposedPaymentFrequency, currentTime, 0);
        SingleVehicleContract singleVehicleContract = new SingleVehicleContract(contractNumber, this,beneficiary, policyHolder, contractPaymentData, vehicleToInsure.getOriginalValue()/2, vehicleToInsure );
        chargePremiumOnContract(singleVehicleContract);
        contracts.add(singleVehicleContract);
        policyHolder.addContract(singleVehicleContract);

        return singleVehicleContract;
    }

    public TravelContract insurePersons(String contractNumber, Person policyHolder, int proposedPremium, PremiumPaymentFrequency proposedPaymentFrequency, Set<Person> personsToInsure) {
        if (personsToInsure == null ) throw  new IllegalArgumentException("Persons to insure cannot be null!");
        if (proposedPaymentFrequency == null) throw new IllegalArgumentException("Proposed payment frequency cannot be null!");
        if (proposedPremium <= 0) throw new IllegalArgumentException("Proposed premium must be positive");

        int annualPremiumTotal = proposedPremium * (12/proposedPaymentFrequency.getValueInMonths());
        if (annualPremiumTotal < personsToInsure.size() * 5) throw new IllegalArgumentException("The annual premium per year is incorrect!");

        ContractPaymentData contractPaymentData = new ContractPaymentData(proposedPremium, proposedPaymentFrequency, currentTime, 0);
        int coverageAmount = personsToInsure.size() * 10;
        TravelContract travelContract = new TravelContract(contractNumber, this, policyHolder, contractPaymentData, coverageAmount, personsToInsure);
        chargePremiumOnContract(travelContract);
        contracts.add(travelContract);
        policyHolder.addContract(travelContract);

        return travelContract;
    }

    public MasterVehicleContract createMasterVehicleContract(String contractNumber, Person beneficiary, Person policyHolder) {
        if (!isContractNumberTaken(contractNumber)) throw new IllegalArgumentException("Contract number already exists!");
        if (beneficiary == null || policyHolder == null )  throw new IllegalArgumentException("Beneficiary or policy holder is null!");

        MasterVehicleContract masterVehicleContract = new MasterVehicleContract(contractNumber, this, beneficiary, policyHolder);
        contracts.add(masterVehicleContract);
        policyHolder.addContract(masterVehicleContract);

        return masterVehicleContract;
    }

    public void moveSingleVehicleContractToMasterVehicleContract(MasterVehicleContract masterVehicleContract, SingleVehicleContract singleVehicleContract) {
        if (masterVehicleContract == null || singleVehicleContract == null) throw new IllegalArgumentException("Master or single vehicle contract is null!");
        if (!masterVehicleContract.isActive() || !singleVehicleContract.isActive()) throw new IllegalArgumentException("Master or single vehicle contract is inactive!");
        if (!masterVehicleContract.getInsurer().equals(singleVehicleContract.getInsurer())) throw new IllegalArgumentException("The insurers is different!");
        if (!masterVehicleContract.getPolicyHolder().equals(singleVehicleContract.getPolicyHolder())) throw new IllegalArgumentException("The policy holders is different!");

        singleVehicleContract.getInsurer().contracts.remove(singleVehicleContract);
        singleVehicleContract.getPolicyHolder().removeContract(singleVehicleContract);

        masterVehicleContract.addChildContract(singleVehicleContract);
    }

    public void chargePremiumsOnContracts() {
        for(AbstractContract contract : contracts) {
            if (contract.isActive()) {
                contract.updateBalance();
            }
        }
    }

    public void chargePremiumOnContract(MasterVehicleContract contract) {
        for (AbstractContract childContract : contract.getChildContracts()) {
            chargePremiumOnContract(childContract);
        }
    }

    public void chargePremiumOnContract(AbstractContract contract) {
        while (!contract.getContractPaymentData().getNextPaymentTime().isAfter(currentTime)) {
                int contractBalance = contract.getContractPaymentData().getOutstandingBalance();
                int contractPremium = contract.getContractPaymentData().getPremium();
                contract.getContractPaymentData().setOutstandingBalance(contractBalance + contractPremium);
                contract.getContractPaymentData().updateNextPaymentTime();
        }
    }

    private boolean isContractNumberTaken(String contractNumber) {
        if(contractNumber == null) return false;

        for (AbstractContract contract : contracts) {
            if (contract.getContractNumber().equals(contractNumber)) {
                return true;
            }
        }
        return false;
    }

    public void processClaim(TravelContract travelContract, Set<Person> affectedPersons) {
        if (travelContract == null) throw new IllegalArgumentException("Travel contract cannot be null!");
        if (affectedPersons == null || affectedPersons.isEmpty()) throw new IllegalArgumentException("Affected persons cannot be null or empty!");

        Set<Person> insuredPersons = travelContract.getInsuredPersons();
        if (!insuredPersons.containsAll(affectedPersons)) throw new IllegalArgumentException("Affected persons and insured persons from travel contract didn't match!");

        if (!travelContract.isActive()) throw new InvalidContractException("Travel contract cannot be inactive!");

        int sum = travelContract.getCoverageAmount()/ affectedPersons.size();
        for (Person person : travelContract.getInsuredPersons()) {
            person.payout(sum);
        }

        travelContract.setInactive();
    }

    public void processClaim(SingleVehicleContract singleVehicleContract, int expectedDamage) {
        if (singleVehicleContract == null) throw new IllegalArgumentException("Single vehicle contract cannot be null!");
        if (expectedDamage <= 0) throw new IllegalArgumentException("Expected damage must be positive!");
        if (!singleVehicleContract.isActive()) throw new InvalidContractException("Single vehicle contract is inactive!");
        if (singleVehicleContract.getBeneficiary() != null) singleVehicleContract.getBeneficiary().payout(singleVehicleContract.getCoverageAmount());
        else singleVehicleContract.getPolicyHolder().payout(singleVehicleContract.getCoverageAmount());

        if(expectedDamage >= 0.7 * singleVehicleContract.getInsuredVehicle().getOriginalValue()) singleVehicleContract.setInactive();
    }
}
