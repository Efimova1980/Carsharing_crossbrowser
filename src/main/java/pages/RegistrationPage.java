package pages;

import dto.RegistrationBodyDto;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

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

    By btnYalla_locator = By.xpath("//button[@type='submit']");

    public void clickYalla(){
        click(btnYalla_locator);
    }

    public void setCheckBoxAgree_WithActions(boolean value){
        int x = checkBoxAgreeLabel.getSize().width;

        // offset is from the label's center; +5 keeps the click inside the label
        // (exactly on the edge Firefox misses the checkbox)
        if (checkBoxAgreeInput.isSelected() != value)
            new Actions(getDriver())
                    .moveToElement(checkBoxAgreeLabel, -x/2 + 5, 0)
                    .click()
                    .perform();
    }

    public void typeRegistrationForm(RegistrationBodyDto user){
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
        return isElementEnabled(getDriver().findElement(btnYalla_locator));
    }
}
