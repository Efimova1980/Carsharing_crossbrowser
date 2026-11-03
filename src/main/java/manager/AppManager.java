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
     * Opens a new browser before every test.
     *
     * @param browserParam value of {@code <parameter name="browser">} from the TestNG suite;
     *                     if absent, -Dbrowser / config.properties is used
     * @param method       the test that is about to run (TestNG passes it in)
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters("browser")
    public void setup(@Optional String browserParam, Method method) {

        // ===== 1. MAIN: choose the browser =====
        // suite parameter (crossbrowser*.xml) wins; otherwise -Dbrowser or config.properties
        String browserName = browserParam != null ? browserParam :
                ConfigReader.get("browser", "chrome");
        // remember it for this thread: used in screenshot names (tearDown)
        browser.set(browserName);

        // ===== 2. SERVICE: logging =====
        // every log line of this thread gets [browser] (logback.xml) and goes to log-...-<browser>.log
        MDC.put("browser", browserName);
        logger.info("Start testing: " + LocalDate.now() + " : " + LocalTime.now());

        // ===== 3. MAIN: create the driver =====
        // local browser or RemoteWebDriver on Selenium Grid (when gridUrl is set) - see DriverFactory;
        // the test name is only a label for the session in the Grid UI
        WebDriver webDriver = DriverFactory.createDriver(browserName,
                method.getDeclaringClass().getSimpleName() + "." + method.getName());

        // ===== 4. SERVICE: log driver actions =====
        // wrapper around the driver: WDListener writes clicks, typing, page opens to the log
        WebDriverListener webDriverListener = new WDListener();
        webDriver = new EventFiringDecorator<>(webDriverListener).decorate(webDriver);

        // ===== 5. MAIN: driver settings and storing =====
        // max time to wait for a page to load (an upper limit, not a pause)
        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        // the driver of this thread: getDriver() in tests and pages returns it
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
