package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class TermsOfUsePage extends BasePage{
    public TermsOfUsePage(WebDriver driver) {
        //setDriver(driver);
        super(driver);
        //PageFactory.initElements(new AjaxElementLocatorFactory(driver,10), this);
    }
}
