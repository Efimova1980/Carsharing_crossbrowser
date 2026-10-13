package pages;

import dto.User;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class RegistrationPage extends BasePage{
    public RegistrationPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(id = "name")
    WebElement inputName;
    @FindBy(id = "lastName")
    WebElement inputLastName;
    @FindBy(id = "email")
    WebElement inputEmail;
    @FindBy(id = "password")
    WebElement inputPassword;
    @FindBy(id="terms-of-use")
    WebElement checkBoxAgreeInput;
    @FindBy(xpath="//label[@for='terms-of-use']")
    WebElement checkBoxAgreeLabel;
    @FindBy(xpath = "//button[@type='submit']")
    WebElement btnYalla;
    @FindBy(xpath = "//h2[text()='You are logged in success']")
    WebElement popupRegistred;

    By btnYalla_locator = By.xpath("//button[@type='submit']");


    public void clickYalla(){
        click(btnYalla_locator);
    }

    public void setCheckBoxAgree_WithJavascript(boolean value){
        if (checkBoxAgreeInput.isSelected() != value)
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", checkBoxAgreeInput);
    }

    public void setCheckBoxAgree_WithActions(boolean value){
        int x = checkBoxAgreeLabel.getSize().width;

        if (checkBoxAgreeInput.isSelected() != value)
            new Actions(getDriver())
                    .moveToElement(checkBoxAgreeLabel, -x/2 , 0)
                    .click()
                    .perform();
    }

    public void typeRegistrationForm(User user){
        inputName.sendKeys(user.getFirstName(), Keys.TAB);
        inputLastName.sendKeys(user.getLastName(), Keys.TAB);
        inputEmail.sendKeys(user.getUsername(), Keys.TAB);
        inputPassword.sendKeys(user.getPassword(), Keys.TAB);
    }

    public void typeRegistrationFormEmpty(){
        inputName.sendKeys(Keys.TAB);
        inputLastName.sendKeys(Keys.TAB);
        inputEmail.sendKeys(Keys.TAB);
        inputPassword.sendKeys(Keys.TAB);
    }

    public boolean isBtnYallaEnabled(){
        return isElementEnabled(btnYalla);
    }
}
