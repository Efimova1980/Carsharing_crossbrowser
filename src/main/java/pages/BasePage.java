package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.enums.HeaderMenuItem;

import java.time.Duration;
import java.util.List;

import static manager.AppManager.logger;

public abstract class BasePage {

    @FindBy(css = ".error")
    List<WebElement> listErrors;
    //    @FindBy(xpath = "//div[@class='error']")
    //    List<WebElement> listErrors;

    private WebDriver driver; //!
    private WebDriverWait wait; //!
    protected Header header;


    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20)); //!
        this.header = new Header(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 20),this);//!
    }

    //--------------------------------getters , setters -----------------------
    protected WebDriver getDriver() {
        return driver;
    }

    protected WebDriverWait getWait() {
        return wait;
    }

//    public static void setDriver(WebDriver driver) {
//        //BasePage.driver = driver;
//    }

    public Header getHeader() {
        return header;
    }


    //----------------------------------base actions---------------------------------------------
    protected void click(By locator) {
        logger.info("clicking on element" + locator.toString());
        // 1. is element exist
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        // 2. is element clickable
        element = wait.until(ExpectedConditions.elementToBeClickable(element));
        // 3. Scroll to element
        Actions actions = new Actions(driver);
        actions.moveToElement(element).click().perform();
    }

    protected void type(By locator, CharSequence... charSequence) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        for (CharSequence cs : charSequence) {
            element.sendKeys(cs);
        }
    }

    protected static void pause(int time){
        try {
            Thread.sleep(time * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    //----------------------------------------------------------------------------------------------------

    public boolean isTextInErrorPresent(String text){
        if (listErrors == null || listErrors.isEmpty())
            return false;
        for (WebElement element:listErrors) {
            if (element.getText().contains(text))
                return true;
        }
        return false;
    }

    protected boolean isElementDisplayed(WebElement element){
        return element.isDisplayed();
    }

    protected boolean isTextInElementPresentWait(WebElement element, String text){
        return wait.until(ExpectedConditions.textToBePresentInElement(element,text));
    }

    protected boolean isElementEnabled(WebElement element){
        return element.isEnabled();
    }

//    public void clickWait(WebElement element, int time){
//        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
//    }

    public boolean isUrlContains(String partOfUrl, int time){
        try {
            return wait.until(ExpectedConditions.urlContains(partOfUrl));
        }catch (TimeoutException e){
            System.err.println("Error in runtime urlContains" + e);
            return false;
        }
    }
}
