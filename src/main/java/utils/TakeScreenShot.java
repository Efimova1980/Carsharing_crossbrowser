package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

import static manager.AppManager.logger;

public class TakeScreenShot {

    // saves the screenshot to build/screenshots and returns it (for the Allure report)
    public static byte[] takeScreenShot(TakesScreenshot screenShot, String browser, String testName) {
        String fileName = createFileName(browser, testName);
        byte[] png = screenShot.getScreenshotAs(OutputType.BYTES);
        try {
            File dir = new File("build/screenshots");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            Files.write(new File(fileName).toPath(), png);
            logger.info("Screenshot saved to: " + fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return png;
    }

    // browser and test name in the file name: parallel tests don't overwrite each other's screenshots
    private static String createFileName(String browser, String testName) {
        SimpleDateFormat formater = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss-SSS");
        String currentDate = formater.format(new Date(System.currentTimeMillis()));
        return "build/screenshots/scr-" + browser + "-" + testName + "-" + currentDate + ".png";
    }
}
