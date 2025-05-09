package contracts;

import company.InsuranceCompany;
import objects.LegalForm;
import objects.Person;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class MasterVehicleContract extends AbstractVehicleContract{
    private final Set<SingleVehicleContract> childContracts;

    public MasterVehicleContract(String contractNumber, InsuranceCompany insurer, Person beneficiary, Person policyHolder) {
        super(contractNumber, insurer, beneficiary, policyHolder, null, 0);
        if (policyHolder.getLegalForm() != LegalForm.LEGAL) throw new IllegalArgumentException("Policy holder must be a legal entity.");

        this.childContracts = new LinkedHashSet<>();
    }

    public Set<SingleVehicleContract> getChildContracts() {
        return  childContracts;
    }

    public void requestAdditionOfChildContract(SingleVehicleContract contract) {
        contract.getInsurer().moveSingleVehicleContractToMasterVehicleContract(this, contract);
    }

    public void addChildContract(SingleVehicleContract contract) {
        if (contract == null) throw new IllegalArgumentException("Contract cannot be null!");
        childContracts.add(contract);
    }

    @Override
    public void setInactive() {
        for (SingleVehicleContract contract : childContracts) {
            contract.setInactive();
        }
        super.setInactive();
    }

    @Override
    public boolean isActive() {
        if (childContracts.isEmpty()) {
            return this.isActive;
        }

        for (SingleVehicleContract contract : childContracts) {
            if (contract.isActive()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void pay(int amount) {
        insurer.getHandler().pay(this, amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MasterVehicleContract)) return false;
        MasterVehicleContract that = (MasterVehicleContract) o;
        return this.getContractNumber().equals(that.getContractNumber()) &&
                this.insurer.equals(that.insurer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContractNumber(), insurer);
    }
}
