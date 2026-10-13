package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class PopupPage extends BasePage{

    public PopupPage(WebDriver driver) {
        //setDriver(driver);
        super(driver);
        //PageFactory.initElements(new AjaxElementLocatorFactory(driver,10), this);
    }

    @FindBy(xpath = "//mat-dialog-container//h2")
    WebElement message;

    By btnOk_locator = By.xpath("//button[@class='positive-button ng-star-inserted']");
    By dialog_locator = By.cssSelector("mat-dialog-container, .cdk-overlay-backdrop");

    public boolean isTextInPopupMessagePresent(String text){
        return isTextInElementPresentWait(message, text);
    }

    public void clickBtnOk(){
        click(btnOk_locator);
        // wait until the dialog is closed, otherwise the next click may hit its overlay (Firefox)
        getWait().until(ExpectedConditions.invisibilityOfElementLocated(dialog_locator));
    }
}
