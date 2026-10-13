package enums;

public enum Fuel {
    DIESEL("//option[@value='Diesel']"),
    PETROL("//option[@value='Petrol']"),
    HYBRID("//option[@value='Hybrid']"),
    ELECTRIC("//option[@value='Electric']"),
    GAS("//option[@value='Gas']");

    private String locator;

    Fuel(String locator) {
        this.locator = locator;
    }

    public String getLocator() {
        return locator;
    }

    @Override
    public String toString() {
        return super.toString().substring(0, 1).toUpperCase() + super.toString().substring(1);
    }
}
