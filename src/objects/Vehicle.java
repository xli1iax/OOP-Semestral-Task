package objects;

public class Vehicle {
    private final String licensePlate;
    private final int originalValue;

    public Vehicle(String licensePlate, int originalValue) {
        if (licensePlate == null || !isValidLicensePlate(licensePlate)) throw new IllegalArgumentException("The license plate is incorrect!");
        if (originalValue <= 0) throw new IllegalArgumentException("The original value must be positive!");

        this.licensePlate = licensePlate;
        this.originalValue = originalValue;
    }

    private boolean isValidLicensePlate(String licensePlate) {
        char[] array = licensePlate.toCharArray();
        for(char element : array) {
            if (!Character.isUpperCase(element) && !Character.isDigit(element)) return false;
        }
        return licensePlate.length() == 7;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public int getOriginalValue() {
        return originalValue;
    }
}
