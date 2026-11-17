package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static manager.AppManager.logger;

public abstract class BasePage {
    // field errors of all fields on the page
    By errors_locator = By.cssSelector(".error");
    // field errors appear right after the field loses focus: a short wait is enough
    private static final Duration ERROR_WAIT = Duration.ofSeconds(5);

    private WebDriver driver;
    private WebDriverWait wait;
    protected Header header;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.header = new Header(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 20),this);
    }

    protected WebDriver getDriver() {
        return driver;
    }

    protected WebDriverWait getWait() {
        return wait;
    }

    public Header getHeader() {
        return header;
    }

    protected void click(By locator) {
        logger.info("clicking on element" + locator.toString());
        // 1. is element exist
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        // 2. is element clickable
        element = wait.until(ExpectedConditions.elementToBeClickable(element));
        // 3. Scroll to element
        Actions actions = new Actions(driver);
        actions.moveToElement(element).click().perform();
    }

    protected void type(By locator, CharSequence... charSequence) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        for (CharSequence cs : charSequence) {
            element.sendKeys(cs);
        }
    }

    // waits until one of the field errors contains the text; false (not TimeoutException) if none does,
    // so the test fails on its assert and Allure shows a product defect, not a broken test
    public boolean isTextInErrorPresent(String text){
        try {
            return new WebDriverWait(driver, ERROR_WAIT)
                    .ignoring(StaleElementReferenceException.class)
                    .until(d -> d.findElements(errors_locator).stream()
                            .anyMatch(e -> e.getText().contains(text)));
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected boolean isElementEnabled(WebElement element){
        return element.isEnabled();
    }

    public boolean isUrlContains(String partOfUrl, int time){
        try {
            return wait.until(ExpectedConditions.urlContains(partOfUrl));
        }catch (TimeoutException e){
            System.err.println("Error in runtime urlContains" + e);
            return false;
        }
    }
}
