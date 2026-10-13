package utils;

import manager.AppManager;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestNGListener implements ITestListener {
    Logger logger = LoggerFactory.getLogger(TestNGListener.class);
    private WebDriver driver;

    @Override
    public void onTestSuccess(ITestResult result) {
        ITestListener.super.onTestSuccess(result);
        logger.info("{} success test --> {}", result.getTestClass(), result.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        ITestListener.super.onTestStart(result);
        logger.info("{} starts test --> {}", result.getTestClass(), result.getName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ITestListener.super.onTestSkipped(result);
        logger.info("Test --> {} was skipped", result.getTestName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ITestListener.super.onTestFailure(result);
        logger.info("{} failed test --> {}", result.getTestClass(), result.getName());
        logger.error("Error");
        this.driver = ((AppManager)result.getInstance()).getDriver();
        TakeScreenShot.takeScreenShot((TakesScreenshot)driver);
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        ITestListener.super.onTestFailedWithTimeout(result);
        logger.info("Test {}failed with timeout", result.getTestName());
        this.driver = ((AppManager)result.getInstance()).getDriver();
        TakeScreenShot.takeScreenShot((TakesScreenshot)driver);
    }

    @Override
    public void onStart(ITestContext context) {
        ITestListener.super.onStart(context);
        logger.info("Information of starting test: {}", context.getStartDate());
    }

    @Override
    public void onFinish(ITestContext context) {
        ITestListener.super.onFinish(context);
        logger.info("Information of ending test: {}", context.getEndDate());
    }
}
