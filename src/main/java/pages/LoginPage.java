package pages;

import dto.AuthenticationBodyDto;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage{
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    By btnYalla_locator = By.xpath("//button[@type='submit']");
    By email_locator = By.xpath("//input[@id='email']");
    By password_locator = By.xpath("//input[@id='password']");

    public void clickBtnOk(){
        new PopupPage(getDriver()).clickBtnOk();
    }

    public void clickBtnYalla(){
        click(btnYalla_locator);
    }

    public boolean isBtnYallaEnabled(){
        return isElementEnabled(getDriver().findElement(btnYalla_locator));
    }

    public void typeLoginForm(AuthenticationBodyDto user){
        type(email_locator, user.getUsername(), Keys.TAB);
        type(password_locator, user.getPassword(), Keys.TAB);
    }

    public void loginWithConfirm(AuthenticationBodyDto user){
        typeLoginForm(user);
        clickBtnYalla();
    }

    public void loginWOConfirm(AuthenticationBodyDto user){
        typeLoginForm(user);
    }

    // a user types the password and presses Enter in the password field instead of clicking Yalla
    public void loginWithEnterConfirm(AuthenticationBodyDto user){
        type(email_locator, user.getUsername(), Keys.TAB);
        type(password_locator, user.getPassword(), Keys.ENTER);
    }
}
