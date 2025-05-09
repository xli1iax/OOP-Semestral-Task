package objects;

import contracts.AbstractContract;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class Person {
    private final static int MAX_OLD_RC_YEAR = 1953;

    private final  String id;
    private LegalForm legalForm;
    private int paidOutAmount;
    private final Set<AbstractContract> contracts;

    public Person(String id) {
        if(id == null || id.isEmpty()) throw new IllegalArgumentException("ID cannot be null or empty!");

        if(!isValidRegistrationNumber(id)) throw new IllegalArgumentException("The id is incorrect!");
        if(isICO(id)) legalForm = LegalForm.LEGAL;
        else if (isRC(id)) legalForm = LegalForm.NATURAL;

        this.id = id;
        this.paidOutAmount = 0;
        this.contracts = new LinkedHashSet<>();
    }

    public static boolean isValidRegistrationNumber(String registrationNumber) {
        return isICO(registrationNumber) || isRC(registrationNumber);
    }

    private static boolean canParseToInteger(String string) {
        try {
            Long.parseLong(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private  static boolean isICO(String id) {
        if (id.length() != 6 && id.length() != 8) return false;

        return canParseToInteger(id);
    }

    private static boolean isRC(String id) {
        if (id.length() != 9 && id.length() != 10) return false;

        if (!canParseToInteger(id)) return false;

        int year = 1900 + Integer.parseInt(id.substring(0, 2));
        int month = Integer.parseInt(id.substring(2,4));

        if((month < 1 || month > 12) && (month < 51 || month > 62)) return false;

        if(id.length() == 9 && year > MAX_OLD_RC_YEAR) return false;
        else if (id.length() == 9) {
            return isValidBirthNumber(id.substring(0,6));
        }

        if(tenSumRC(id)) return isValidBirthNumber(id.substring(0, 6));

        return true;
    }

    private static boolean tenSumRC(String id) {
        char[] array = id.toCharArray();
        int sum = 0;

        for (int i = 0; i < 10; i++) {
            int digit = array[i] - '0';
            sum += (i % 2 == 0 ? digit : -digit);
        }

        return sum % 11 == 0;
    }

    public static boolean isValidBirthNumber(String birthNumber) {
        int year = 1900 + Integer.parseInt(birthNumber.substring(0, 2));
        int month = Integer.parseInt(birthNumber.substring(2,4));
        if(month >= 51) month -= 50;
        int day = Integer.parseInt(birthNumber.substring(4,6));

        try {
            LocalDate.of(year, month, day);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

    public String getId() {
        return id;
    }

    public int getPaidOutAmount() {
        return paidOutAmount;
    }

    public LegalForm getLegalForm() {
        return legalForm;
    }

    public Set<AbstractContract> getContracts() {
        return Collections.unmodifiableSet(contracts);
    }

    public void addContract(AbstractContract contract) {
        if (contract == null) throw new IllegalArgumentException("Contract cannot be null!");
        if (!contract.getPolicyHolder().equals(this)) throw new IllegalArgumentException("This person is not the policy holder of the contract.");

        contracts.add(contract);
    }

    public void payout(int paidOutAmount) {
        if (paidOutAmount <= 0) throw new IllegalArgumentException("Paid out amount cannot be negative!");
        this.paidOutAmount += paidOutAmount;
    }

    public void removeContract(AbstractContract contract) {

        contracts.remove(contract);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Person)) return false;

        Person that = (Person) obj;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
