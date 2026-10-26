package manager;

import io.qameta.allure.Allure;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import utils.ConfigReader;
import utils.TakeScreenShot;
import utils.WDListener;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppManager {
    // one browser per thread: tests can run in parallel
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final ThreadLocal<String> browser = new ThreadLocal<>();

    public static final Logger logger =
            LoggerFactory.getLogger(AppManager.class);

    public WebDriver getDriver() {
        return driver.get();
    }

    public static String getBrowser() {
        return browser.get();
    }

    /**
     * @param browserParam value of {@code <parameter name="browser">} from the TestNG suite;
     *                     if absent, -Dbrowser / BROWSER / config.properties is used
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters("browser")
    public void setup(@Optional String browserParam, Method method) {

        String browserName = browserParam != null ? browserParam :
                ConfigReader.get("browser", "chrome");
        browser.set(browserName);

        MDC.put("browser", browserName);
        logger.info("Start testing: " + LocalDate.now() + " : " + LocalTime.now());

        WebDriverListener webDriverListener = new WDListener();
        WebDriver webDriver = DriverFactory.createDriver(browserName,
                method.getDeclaringClass().getSimpleName() + "." + method.getName());
        webDriver = new EventFiringDecorator<>(webDriverListener).decorate(webDriver);

        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.set(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        logger.info("Stop testing: " + LocalDate.now() + " : " + LocalTime.now());

        WebDriver webDriver = driver.get();
        if (webDriver != null && result.getStatus() == ITestResult.FAILURE) {
            takeScreenShot(webDriver, result.getName());
        }
        if (webDriver != null) {
            webDriver.quit();
        }
        driver.remove();
        browser.remove();
        MDC.remove("browser");
    }

    // here and not in TestNGListener.onTestFailure: by then Allure has already closed the test,
    // in @AfterMethod the screenshot is attached to the test (Tear down section of the report)
    private void takeScreenShot(WebDriver webDriver, String testName) {
        try {
            byte[] png = TakeScreenShot.takeScreenShot((TakesScreenshot) webDriver, getBrowser(), testName);
            Allure.addAttachment("Screenshot (" + getBrowser() + ")", "image/png",
                    new ByteArrayInputStream(png), "png");
        } catch (RuntimeException e) {
            // a failed screenshot must not stop quit(): the browser would stay open on the grid
            logger.error("Can't take screenshot: " + e.getMessage());
        }
    }
}
