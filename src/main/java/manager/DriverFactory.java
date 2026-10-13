package manager;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import utils.ConfigReader;

import java.net.MalformedURLException;
import java.net.URI;

public class DriverFactory {

    /**
     * Creates a local browser, or a remote one when gridUrl is set.
     *
     * @param browser  chrome | firefox | edge
     * @param testName shown in the Selenium Grid UI for this session
     */
    public static WebDriver createDriver(String browser, String testName) {
        boolean headless = Boolean.parseBoolean(
                ConfigReader.get("headless", "false")
        );

        String gridUrl = ConfigReader.get("gridUrl");

        MutableCapabilities options = switch (browser.toLowerCase()) {
            case "chrome" -> chromeOptions(headless);
            case "firefox" -> firefoxOptions(headless);
            case "edge" -> edgeOptions(headless);
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };

        if (gridUrl == null)
            return createLocalDriver(options);

        options.setCapability("se:name", testName);
        return createRemoteDriver(gridUrl, options);
    }

    private static WebDriver createLocalDriver(Capabilities options) {
        if (options instanceof ChromeOptions chrome)
            return new ChromeDriver(chrome);
        if (options instanceof FirefoxOptions firefox)
            return new FirefoxDriver(firefox);
        if (options instanceof EdgeOptions edge)
            return new EdgeDriver(edge);
        throw new IllegalArgumentException("Unsupported options: " + options.getClass());
    }

    private static WebDriver createRemoteDriver(String gridUrl, Capabilities options) {
        try {
            RemoteWebDriver driver = new RemoteWebDriver(URI.create(gridUrl).toURL(), options);
            // upload files from this machine to the remote node (input type=file)
            driver.setFileDetector(new LocalFileDetector());
            return driver;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Wrong gridUrl: " + gridUrl, e);
        }
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--lang=en", "--window-size=1920,1080");
        if (headless)
            options.addArguments("--headless=new");
        return options;
    }

    private static EdgeOptions edgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--lang=en", "--window-size=1920,1080");
        if (headless)
            options.addArguments("--headless=new");
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("intl.accept_languages", "en-US, en");
        options.addArguments("--width=1920", "--height=1080");
        if (headless)
            options.addArguments("-headless");
        return options;
    }
}
