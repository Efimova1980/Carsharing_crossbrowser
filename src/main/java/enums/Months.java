package enums;

public enum Months {
    JAN("//div[text()=' JAN ']"),
    FEB("//div[text()=' FEB ']"),
    MAR("//div[text()=' MAR ']"),
    APR("//div[text()=' APR ']"),
    MAY("//div[text()=' MAY ']"),
    JUN("//div[text()=' JUN ']"),
    JUL("//div[text()=' JUL ']"),
    AUG("//div[text()=' AUG ']"),
    SEP("//div[text()=' SEP ']"),
    OCT("//div[text()=' OCT ']"),
    NOV("//div[text()=' NOV ']"),
    DEC("//div[text()=' DEC ']");

    private String locator;

    Months(String locator) {
        this.locator = locator;
    }

    public String getLocator() {
        return locator;
    }
}
