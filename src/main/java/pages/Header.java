package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.enums.HeaderMenuItem;

import java.time.Duration;

public class Header {
    private WebDriver driver;
    private WebDriverWait wait;

    public Header(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public <T extends BasePage> T click(HeaderMenuItem item) {
        By locator = By.xpath(item.getLocator());
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();

        return switch (item) {
            case LOGIN -> (T) new LoginPage(driver);
            case SIGN_UP -> (T) new RegistrationPage(driver);
            case SEARCH, LOG_OUT, DELETE_ACCAUNT -> (T) new HomePage(driver);
            case TERMS_OF_USE -> (T) new TermsOfUsePage(driver);
            case LET_THE_CAR_WORK -> (T) new LetTheCarWorkPage(driver);
            default -> throw new IllegalArgumentException("Invalid parameter");
        };
    }
}
