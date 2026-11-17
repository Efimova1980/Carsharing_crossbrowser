package ui_tests;
import dto.RegistrationBodyDto;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import manager.AppManager;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.*;
import utils.TestNGListener;
import utils.enums.HeaderMenuItem;
import static utils.UserFactory.*;

@Listeners(TestNGListener.class)

@Severity(SeverityLevel.NORMAL)
public class RegistrationTests extends AppManager {
    RegistrationPage registrationPage;

    @BeforeMethod(alwaysRun = true)
    public void goToRegistrationPage(){
        WebDriver driver = getDriver();
        HomePage homePage =  new HomePage(driver);
        Header header = homePage.getHeader();
        registrationPage = header.click(HeaderMenuItem.SIGN_UP);
    }

    //----------------------------------POSITIVE TESTS-------------------------------------
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "smoke")
    public void registrationPositiveTest(){
        RegistrationBodyDto user = positiveUser();
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(true);
        registrationPage.clickYalla();
        Assert.assertTrue(new PopupPage(getDriver()).isTextInPopupMessagePresent("You are logged in success"));
    }


    //--------------------------------NEGATIVE TESTS-------------------------------------

    @Test(groups = "regression")
    public void registrationNegativeTest_EmptyFields(){
        SoftAssert softAssert = new SoftAssert();
        registrationPage.typeRegistrationFormEmpty();
        registrationPage.setCheckBoxAgree_WithActions(true);
        softAssert.assertFalse(registrationPage.isBtnYallaEnabled(),
                "validate button Yalla is disabled");
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("Name is required"),
                "validate field Name");
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("Last name is required"),
                "validate field Last name");
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("Email is required"),
                "validate field Email");
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("Password is required"),
                "validate field Password");
        softAssert.assertAll();
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = "regression")
    public void registrationNegativeTest_UserAlreadyExists(){
        RegistrationBodyDto user = existingUser();
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(true);
        registrationPage.clickYalla();
        Assert.assertTrue(new PopupPage(getDriver()).isTextInPopupMessagePresent("User already exists"));
    }

    @Test(groups = "regression")
    public void registrationNegativeTest_NoCheckboxAgree(){
        RegistrationBodyDto user = positiveUser();
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(false);
        Assert.assertFalse(registrationPage.isBtnYallaEnabled());
    }

    //------------------------------INVALID PASSWORD---------------------------------------
    /*
    password rule: ^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@$#^&*!])(?=.*[a-zA-Z]).{8,}$
    row: password, expected error text
     */
    @DataProvider
    public Object[][] invalidPasswords() {
        return new Object[][]{
                {"", "Password is required"},
                {"Pas123#", "Password must contain minimum 8 symbols"},   // < 8 chars
                {"Pass1234", "one special symbol"},                       // no special char
                {"pass123#", "1 uppercase letter"},                       // no A-Z
                {"PASS1234#", "1 lowercase letter"},                      // no a-z
                {"Password#", "1 number"}                                 // no digits
        };
    }

    @Test(groups = "regression", dataProvider = "invalidPasswords")
    public void registrationNegativeTest_InvalidPassword(String password, String expectedError){
        RegistrationBodyDto user = positiveUser();
        user.setPassword(password);
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(true);
        assertErrorAndYallaDisabled(expectedError, "password '" + password + "'");
    }

    //------------------------------INVALID EMAIL------------------------------------------
    /*
    row: email, expected error text
    Chars ! # $ % & ' * + - / = ? ^ _ ` { | } ~ ARE allowed before '@' (RFC 5322),
    even as the only char ("!@gmail.com"), so they are not here.
     */
    @DataProvider
    public Object[][] invalidEmails() {
        String wrongFormat = "Wrong email format";
        return new Object[][]{
                {"", "Email is required"},
                // '@' problems
                {"mailgmail.com", wrongFormat},          // no '@'
                {"mail@@gmail.com", wrongFormat},        // two '@'
                {"ma@il@gmail.com", wrongFormat},        // '@' inside the name
                {"mail@", wrongFormat},                  // nothing after '@'
                {"@gmail.com", wrongFormat},             // nothing before '@'
                // cyrillic, alone and mixed with latin
                {"почта@gmail.com", wrongFormat},        // only cyrillic before '@'
                {"maилl@gmail.com", wrongFormat},        // cyrillic inside latin name
                {"почтаmail@gmail.com", wrongFormat},    // cyrillic start, latin end
                {"mailпочта@gmail.com", wrongFormat},    // latin start, cyrillic end
                {"mаil@gmail.com", wrongFormat},    // one cyrillic 'а' that looks like latin 'a'
                {"mail@gmaил.com", wrongFormat},         // cyrillic inside domain
                {"mail@почта.com", wrongFormat},         // cyrillic domain
                {"mail@gmail.ру", wrongFormat},          // cyrillic top-level domain
                // chars not allowed before '@'
                {"ma<il@gmail.com", wrongFormat},
                {"ma>il@gmail.com", wrongFormat},
                {"ma(il@gmail.com", wrongFormat},
                {"ma)il@gmail.com", wrongFormat},
                {"ma[il@gmail.com", wrongFormat},
                {"ma]il@gmail.com", wrongFormat},
                {"ma:il@gmail.com", wrongFormat},
                {"ma;il@gmail.com", wrongFormat},
                {"ma,il@gmail.com", wrongFormat},
                {"ma\\il@gmail.com", wrongFormat},
                {"ma\"il@gmail.com", wrongFormat},
                {"ma il@gmail.com", wrongFormat},
                // wrong dot position
                {".mail@gmail.com", wrongFormat},
                {"mail.@gmail.com", wrongFormat},
                {"ma..il@gmail.com", wrongFormat}
        };
    }

    @Test(groups = "regression", dataProvider = "invalidEmails")
    public void registrationNegativeTest_InvalidEmail(String email, String expectedError){
        RegistrationBodyDto user = positiveUser();
        user.setUsername(email);
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(true);
        assertErrorAndYallaDisabled(expectedError, "email '" + email + "'");
    }

    // the field error is shown and Yalla stays disabled
    private void assertErrorAndYallaDisabled(String expectedError, String field){
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(registrationPage.isTextInErrorPresent(expectedError),
                "validate error '" + expectedError + "' for " + field);
        softAssert.assertFalse(registrationPage.isBtnYallaEnabled(),
                "validate button Yalla is disabled for " + field);
        softAssert.assertAll();
    }
}
