package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class PopupPage extends BasePage{

    public PopupPage(WebDriver driver) {
        //setDriver(driver);
        super(driver);
        //PageFactory.initElements(new AjaxElementLocatorFactory(driver,10), this);
    }

    @FindBy(xpath = "//mat-dialog-container//h2")
    WebElement message;

    public boolean isTextInPopupMessagePresent(String text){
        return isTextInElementPresentWait(message, text);
    }
}
