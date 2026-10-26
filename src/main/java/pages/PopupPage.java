package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static manager.AppManager.logger;

public class PopupPage extends BasePage{

    public PopupPage(WebDriver driver) {
        //setDriver(driver);
        super(driver);
        //PageFactory.initElements(new AjaxElementLocatorFactory(driver,10), this);
    }

    By message_locator = By.xpath("//mat-dialog-container//h2");
    By btnOk_locator = By.xpath("//button[@class='positive-button ng-star-inserted']");
    By dialog_locator = By.cssSelector("mat-dialog-container, .cdk-overlay-backdrop");

    // waits until the dialog shows any text and returns it (null if no dialog appeared)
    public String getPopupMessage(){
        try {
            return getWait().until(driver -> {
                List<WebElement> elements = driver.findElements(message_locator);
                if (elements.isEmpty())
                    return null;                       // no dialog yet - keep waiting
                String text = elements.get(0).getText();
                return text.isBlank() ? null : text;   // dialog without text yet - keep waiting
            });
        } catch (TimeoutException e) {
            return null;
        }
    }

    // the site answers once: compare with the first message instead of waiting for "our" text
    public boolean isTextInPopupMessagePresent(String text){
        String actual = getPopupMessage();
        logger.info("popup message: '" + actual + "', expected: '" + text + "'");
        return actual != null && actual.contains(text);
    }

    public void clickBtnOk(){
        click(btnOk_locator);
        // wait until the dialog is closed, otherwise the next click may hit its overlay (Firefox)
        getWait().until(ExpectedConditions.invisibilityOfElementLocated(dialog_locator));
    }
}
