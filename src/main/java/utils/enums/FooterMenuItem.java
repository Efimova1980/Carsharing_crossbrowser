package utils.enums;

public enum FooterMenuItem {

    ICON_FACEBOOK("//i[@class='icon-facebook-squared']"),
    ICON_TELEGRAM("//i[@class='icon-telegram']"),
    ICON_VK("//i[@class='icon-vkontakte']"),
    ICON_INSTAGRAM("//i[@class='icon-instagram']"),
    ICON_SLACK("//i[@class='icon-slack']");

    private String locator;

    FooterMenuItem(String locator) {
        this.locator = locator;
    }

    public String getLocator() {
        return locator;
    }
}
