package contracts;

import company.InsuranceCompany;
import objects.Person;
import objects.Vehicle;
import payment.ContractPaymentData;

import java.util.Objects;

public class SingleVehicleContract extends AbstractVehicleContract {
    private final Vehicle insuredVehicle;

    public SingleVehicleContract(String contractNumber, InsuranceCompany insurer, Person beneficiary, Person policyHolder, ContractPaymentData contractPaymentData, int coverageAmount, Vehicle vehicleToInsure) {
        super(contractNumber, insurer, beneficiary, policyHolder, contractPaymentData, coverageAmount);
        if (vehicleToInsure == null || contractPaymentData == null) throw new IllegalArgumentException("Check that you contract payment data or vehicle to insure is not null!");
        this.insuredVehicle = vehicleToInsure;
    }

    public Vehicle getInsuredVehicle() {
        return insuredVehicle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SingleVehicleContract)) return false;
        SingleVehicleContract that = (SingleVehicleContract) o;
        return this.getContractNumber().equals(that.getContractNumber()) &&
                this.insurer.equals(that.insurer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContractNumber(), insurer);
    }
}
