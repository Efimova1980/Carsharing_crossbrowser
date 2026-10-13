package pages;

import dto.User;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class LoginPage extends BasePage{
    public LoginPage(WebDriver driver) {
        super(driver);
    }


    @FindBy(xpath = "//button[@type='submit']")
    WebElement btnYalla;
    @FindBy(xpath = "//h2[@class='message']")
    WebElement popupSuccessfullLogin;

    By btnYalla_locator = By.xpath("//button[@type='submit']");
    By btnOk_locator = By.xpath("//button[@class='positive-button ng-star-inserted']");
    By email_locator = By.xpath("//input[@id='email']");
    By password_locator = By.xpath("//input[@id='password']");

    public void clickBtnOk(){
        click(btnOk_locator);
    }

    public void clickBtnYalla(){
        click(btnYalla_locator);
    }

    public boolean isBtnYallaEnabled(){
        return isElementEnabled(btnYalla);
    }

    public void typeLoginForm(User user){
        type(email_locator, user.getUsername(), Keys.TAB);
        type(password_locator, user.getPassword(), Keys.TAB);
    }

    public boolean isLoggedInDisplayed(){
        return isElementDisplayed(popupSuccessfullLogin);
    }

}
