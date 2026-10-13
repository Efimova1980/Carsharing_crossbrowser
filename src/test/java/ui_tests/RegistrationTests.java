package ui_tests;
import dto.User;
import manager.AppManager;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.*;
import utils.TestNGListener;
import utils.enums.HeaderMenuItem;
import static utils.UserFactory.*;
import java.util.Random;

@Listeners(TestNGListener.class)

public class RegistrationTests extends AppManager {
    RegistrationPage registrationPage;
    SoftAssert softAssert = new SoftAssert();

    @BeforeMethod(alwaysRun = true)
    public void goToRegistrationPage(){
        WebDriver driver = getDriver();
        HomePage homePage =  new HomePage(driver);
        Header header = homePage.getHeader();
        registrationPage = header.click(HeaderMenuItem.SIGN_UP);

        //new HomePage(getDriver()).clickBtnSignUp();
        //registrationPage = new RegistrationPage(getDriver());
    }

    //----------------------------------POSITIVE TESTS-------------------------------------
    //in the class
    @Test(groups = {"smoke", "test_workflow"})
    public void registrationPositiveTest(){
        Random rand = new Random();
        String email = "henry" + rand.nextInt(1000) + "@gmail.com";

        User user = User.builder()
                .firstName("Henry")
                .lastName("Gross")
                .username(email)
                .password("Pass1234!")
                .build();

        registrationPage.typeRegistrationForm(user);
        //registrationPage.setCheckBoxAgree_WithJavascript(true);
        registrationPage.setCheckBoxAgree_WithActions(true);
        registrationPage.clickYalla();
        Assert.assertTrue(new PopupPage(getDriver()).isTextInPopupMessagePresent("You are logged in success"));
        //Assert.assertTrue(registrationPage.isRegisteredDisplayed());
    }

    //in the class
    @Test(groups = "reg-regression")
    public void registrationPositiveTest_WithFaker(){
        User user = positiveUser();
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(true);
        registrationPage.clickYalla();
        Assert.assertTrue(new PopupPage(getDriver()).isTextInPopupMessagePresent("You are logged in success"));
    }

    //--------------------------------NEGATIVE TESTS-------------------------------------

    @Test(groups = "reg-regression")
    public void registrationNegativeTest_EmptyFields(){
        registrationPage.typeRegistrationFormEmpty();
        registrationPage.setCheckBoxAgree_WithActions(true);
        softAssert.assertFalse(registrationPage.isBtnYallaEnabled(), "validate button Yalla is disabled");
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("Name is required"), "validate field Name");
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("Last name is required"), "validate field Last name");
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("Email is required"), "validate field Email");
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("Password is required"), "validate field Password");
        softAssert.assertAll();
    }

    @Test(groups = "reg-regression")
    public void registrationNegativeTest_UserAlreadyExists(){
        User user = new User("ola_mail@gmail.com","Pass1234!", "Ola", "Ola");
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(true);
        registrationPage.clickYalla();
        Assert.assertTrue(new PopupPage(getDriver()).isTextInPopupMessagePresent("User already exists"));
    }

    //-------------------------------HOMEWORK 7 -----------------------------------------------
    @Test(groups = "reg-regression")
    public void registrationNegativeTest_NoCheckboxAgree(){
        User user = positiveUser();
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(false);
        Assert.assertFalse(registrationPage.isBtnYallaEnabled());
    }
    /*
    negative tests with invalid password (^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@$#^&*!])(?=.*[a-zA-Z]).{8,}$):
    Empty password
    <8 characters
    no special character
    no A-Z
    no a-z
    no digits
     */

    @Test(groups = "reg-regression")
    public void registrationNegativeTest_EmptyPassword(){
        User user = positiveUser();
        user.setPassword("");
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(true);
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("Password is required")
                , "validate field password");
        softAssert.assertFalse(registrationPage.isBtnYallaEnabled(), "validate button Yalla enabled");
        softAssert.assertAll();
    }

    @Test(groups = "reg-regression")
    public void registrationNegativeTest_ShortPassword(){
        User user = positiveUser();
        user.setPassword("Pas123#");
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(true);
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("Password must contain minimum 8 symbols")
                , "validate field password");
        softAssert.assertFalse(registrationPage.isBtnYallaEnabled(), "validate button Yalla enabled");
        softAssert.assertAll();
    }

    @Test(groups = "reg-regression")
    public void registrationNegativeTest_NoSpecialCharInPassword(){
        User user = positiveUser();
        user.setPassword("Pass1234");
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(true);
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("one special symbol")
                , "validate field password");
        softAssert.assertFalse(registrationPage.isBtnYallaEnabled(), "validate button Yalla enabled");
        softAssert.assertAll();
    }

    @Test(groups = "reg-regression")
    public void registrationNegativeTest_NoBigCharInPassword(){
        User user = positiveUser();
        user.setPassword("pass123#");
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(true);
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("1 uppercase letter")
                , "validate field password");
        softAssert.assertFalse(registrationPage.isBtnYallaEnabled(), "validate button Yalla enabled");
        softAssert.assertAll();
    }

    @Test(groups = "reg-regression")
    public void registrationNegativeTest_NoSmallCharInPassword(){
        User user = positiveUser();
        user.setPassword("PASS1234#");
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(true);
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("1 lowercase letter")
                , "validate field password");
        softAssert.assertFalse(registrationPage.isBtnYallaEnabled(), "validate button Yalla enabled");
        softAssert.assertAll();
    }

    @Test(groups = "reg-regression")
    public void registrationNegativeTest_NoDigitsInPassword(){
        User user = positiveUser();
        user.setPassword("Password#");
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(true);
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("1 number")
                , "validate field password");
        softAssert.assertFalse(registrationPage.isBtnYallaEnabled(), "validate button Yalla enabled");
        softAssert.assertAll();
    }

    /*
    negative tests with invalid email:
    empty email
    no '@'
    more than 1 char '@'
    no char before '@'
    no char after '@'
    cyrillic in email
    unresolved symbol in email
     */

    @Test(groups = "reg-regression")
    public void registrationNegativeTest_EmptyEmail(){
        User user = positiveUser();
        user.setUsername("");
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(true);
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("Email is required")
                , "validate field email - empty");
        softAssert.assertFalse(registrationPage.isBtnYallaEnabled(), "validate button Yalla enabled");
        softAssert.assertAll();
    }

    @Test(groups = "reg-regression")
    public void registrationNegativeTest_NoMonkeyInEmail(){
        Random rn = new Random();
        String email = "mail" + rn.nextInt(10000) + "gmail.com";

        User user = positiveUser();
        user.setUsername(email);
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(true);
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("Wrong email format")
                , "validate field email - no @");
        softAssert.assertFalse(registrationPage.isBtnYallaEnabled(), "validate button Yalla enabled");
        softAssert.assertAll();
    }

    @Test(groups = "reg-regression")
    public void registrationNegativeTest_TwoMonkeyInEmail(){
        Random rn = new Random();
        String email = "mail" + rn.nextInt(10000) + "@@gmail.com";

        User user = positiveUser();
        user.setUsername(email);
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(true);
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("Wrong email format")
                , "validate field email - mo than one @");
        softAssert.assertFalse(registrationPage.isBtnYallaEnabled(), "validate button Yalla enabled");
        softAssert.assertAll();
    }

    @Test(groups = "reg-regression")
    public void registrationNegativeTest_NoCharsAfterMonkeyInEmail(){
        Random rn = new Random();
        String email = "mail" + rn.nextInt(10000) + "@";

        User user = positiveUser();
        user.setUsername(email);
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(true);
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("Wrong email format")
                , "validate field email - no chars after @");
        softAssert.assertFalse(registrationPage.isBtnYallaEnabled(), "validate button Yalla enabled");
        softAssert.assertAll();
    }

    @Test(groups = "reg-regression")
    public void registrationNegativeTest_NoCharsBeforeMonkeyInEmail(){
        Random rn = new Random();
        String email = "@" + rn.nextInt(10000) + "gmail.com";

        User user = positiveUser();
        user.setUsername(email);
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(true);
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("Wrong email format")
                , "validate field email - no chars before @");
        softAssert.assertFalse(registrationPage.isBtnYallaEnabled(), "validate button Yalla enabled");
        softAssert.assertAll();
    }

    @Test(groups = "reg-regression")
    public void registrationNegativeTest_CyrillicCharsInEmail(){
        Random rn = new Random();
        String email = "почта" + rn.nextInt(10000) + "@gmail.com";

        User user = positiveUser();
        user.setUsername(email);
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(true);
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("Wrong email format")
                , "validate field email - cyrillic chars in email");
        softAssert.assertFalse(registrationPage.isBtnYallaEnabled(), "validate button Yalla enabled");
        softAssert.assertAll();
    }

    @Test(groups = "reg-regression")
    public void registrationNegativeTest_UnresolvedCharInEmail(){
        Random rn = new Random();
        String email = "%@" + rn.nextInt(10000) + "gmail.com";

        User user = positiveUser();
        user.setUsername(email);
        registrationPage.typeRegistrationForm(user);
        registrationPage.setCheckBoxAgree_WithActions(true);
        softAssert.assertTrue(registrationPage.isTextInErrorPresent("Wrong email format")
                , "validate field email - unresolved char (%) in email");
        softAssert.assertFalse(registrationPage.isBtnYallaEnabled(), "validate button Yalla enabled");
        softAssert.assertAll();
    }

}
