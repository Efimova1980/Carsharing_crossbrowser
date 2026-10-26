package pages;

import enums.Months;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;
import utils.enums.FooterMenuItem;
import utils.enums.HeaderMenuItem;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HomePage extends BasePage {
    public HomePage(WebDriver driver) {
        super(driver);
        driver.get(ConfigReader.get("baseUrl") + "/search");
    }

    By btnYalla_locator = By.xpath("//button[@type='submit']");
    By inputDates_locator = By.xpath("//input[@id='dates']");
    By inputCity_locator = By.xpath("//input[@id='city']");
    By btnChooseMonthYear_locator = By.xpath("//button[@aria-label='Choose month and year']");
    By btnGoogleMapsOk_locator = By.cssSelector("button.dismissButton");

    public void clickBtnYalla() {
        closeGoogleMapsErrorIfVisible();
        click(btnYalla_locator);
    }

    // the site can't load Google Maps: after a city is typed Google shows
    // "This page can't load Google Maps correctly" over Dates and Yalla.
    // The dialog appears with a delay, so wait for it a little; if it is shown - press OK
    public void closeGoogleMapsErrorIfVisible() {
        try {
            new WebDriverWait(getDriver(), Duration.ofSeconds(3))
                    .until(ExpectedConditions.elementToBeClickable(btnGoogleMapsOk_locator))
                    .click();
        } catch (TimeoutException e) {
            // no dialog - nothing to close
        }
    }

    public void setDateRange(LocalDate startDate, LocalDate endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");
        String dateRange = startDate.format(formatter)
                + " - "
                + endDate.format(formatter);
        type(inputDates_locator, dateRange);
    }

    public boolean isBtnYallaEnabled() {
        return isElementEnabled(getDriver().findElement(btnYalla_locator));
    }

    public void typeFindYourCarForm(String city, LocalDate startDate, LocalDate endDate) {
        type(inputCity_locator, city);
        setDateRange(startDate, endDate);
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("document.querySelector(\"button[type='submit']\")" +
                ".removeAttribute(\"disabled\")");
    }

    public void typeFindYourCarForm_WOJavascript(String city, LocalDate startDate, LocalDate endDate) {
        type(inputCity_locator, city);
        setDateRange(startDate, endDate);
        // TAB: the site shows date errors only when the field loses focus
        // (not in the positive flow: TAB scrolls the page and Yalla goes out of view)
        getDriver().findElement(inputDates_locator).sendKeys(Keys.TAB);
    }

    //CALENDAR--------------------------------------------------

    private void typeMonth(Months month) {
        WebElement btnMonth = getDriver().findElement(By.xpath(month.getLocator()));
        btnMonth.click();
    }

    private void typeCalendar(LocalDate date) {
        click(btnChooseMonthYear_locator);
        //td[@aria-label="2026"]
        String year = Integer.toString(date.getYear());
        WebElement btnYear = getDriver().findElement(By
                .xpath("//td[@aria-label='" + year + "']"));
        btnYear.click();

//by enum
//        int month = date.getMonth().getValue();
//        typeMonth(Months.values()[month-1]);

        //td[@aria-label="March 2026"] //another locator for month
        StringBuilder month = new StringBuilder();
        month.append(date.getMonth().toString().substring(0, 1).toUpperCase())
                .append(date.getMonth().toString().substring(1).toLowerCase());
        WebElement btnMonth = getDriver().findElement(By
                .xpath("//td[@aria-label='" + month + " " + year + "']"));
        btnMonth.click();

        //div[text()=' 1 ']
//        String day = Integer.toString(date.getDayOfMonth());
//        WebElement btnDay = driver.findElement(By
//                .xpath("//div[text()=' " + day + " ']"));
//        btnDay.click();

        //td[@aria-label="March 18, 2026"] //another locator for day
        StringBuilder day = new StringBuilder();
        day.append(date.getDayOfMonth());
        WebElement btnDay = getDriver().findElement(By
                .xpath("//td[@aria-label='" + month + " " + day + ", " + year + "']"));
        btnDay.click();
    }

    public void typeFindYourCarFormWithCalandar(String city, LocalDate startDate, LocalDate endDate) {
        type(inputCity_locator, city);
        closeGoogleMapsErrorIfVisible();   // otherwise the dialog may cover Dates
        click(inputDates_locator);
        typeCalendar(startDate);
        typeCalendar(endDate);

        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("document.querySelector(\"button[type='submit']\")" +
                ".removeAttribute(\"disabled\")");
    }

    public boolean isTitlePresentOnClickIconFooter(FooterMenuItem item, String title) {
        getDriver().findElement(By.xpath(item.getLocator())).click();
        return new WebDriverWait(getDriver(), Duration.ofSeconds(10))
                .until(ExpectedConditions.titleContains(title));
    }

    public boolean isTitlePresentOnClickBtnHeader(HeaderMenuItem item, String title) {
        getDriver().findElement(By.xpath(item.getLocator())).click();
        return new WebDriverWait(getDriver(), Duration.ofSeconds(10))
                .until(ExpectedConditions.titleContains(title));
    }
}
