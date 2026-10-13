package manager;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.WDListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppManager {
    private WebDriver driver;

    public static final Logger logger =
            LoggerFactory.getLogger(AppManager.class);

    public WebDriver getDriver() {
        return driver;
    }

    @BeforeMethod(alwaysRun = true)
    public void setup() throws MalformedURLException {
        logger.info(
                "Start testing: " + LocalDate.now() + " : " + LocalTime.now()
        );

        WebDriverListener webDriverListener = new WDListener();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--lang=en");
        options.addArguments("--window-size=1920,1080");

        String seleniumRemoteUrl =
                System.getenv("SELENIUM_REMOTE_URL");

        if (seleniumRemoteUrl == null || seleniumRemoteUrl.isBlank()) {
            driver = new ChromeDriver(options);
        } else {
            driver = new RemoteWebDriver(
                    new URL(seleniumRemoteUrl),
                    options
            );
        }

        driver = new EventFiringDecorator<>(webDriverListener)
                .decorate(driver);

        driver.manage().window().maximize();
        driver.manage()
                .timeouts()
                .pageLoadTimeout(Duration.ofSeconds(10));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        logger.info(
                "Stop testing: " + LocalDate.now() + " : " + LocalTime.now()
        );

        if (driver != null) {
            driver.quit();
        }
    }
}