package ui_tests;

import dto.User;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import manager.AppManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.Header;
import pages.HomePage;
import pages.LoginPage;
import pages.PopupPage;
import utils.ConfigReader;
import utils.TestNGListener;
import utils.enums.HeaderMenuItem;

import static utils.UserFactory.positiveUser;
import static utils.UserFactory.userWithCredentials;
@Listeners(TestNGListener.class)

@Epic("User account")
@Feature("Login and logout")
@Severity(SeverityLevel.CRITICAL)
public class LoginTests extends AppManager {
    LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void goToLoginPage(){
        loginPage = new HomePage(getDriver()).getHeader().click(HeaderMenuItem.LOGIN);
    }

    //------------------LOGIN POSITIVE TESTS----------------------------
    @Severity(SeverityLevel.BLOCKER)
    @Story("Successful login")
    @Test(groups = "smoke")
    public void loginPositiveTest(){
        User user = userWithCredentials(ConfigReader.get("login"), ConfigReader.get("password"));
        loginPage.typeLoginForm(user);
        loginPage.clickBtnYalla();
        Assert.assertTrue(new PopupPage(getDriver()).isTextInPopupMessagePresent("Logged in success"));
    }

    // email is not case-sensitive: HARRY@GMAIL.COM is the same user as harry@gmail.com
    @Story("Successful login")
    @Test(groups = "regression")
    public void loginPositiveTest_EmailInUpperCase(){
        User user = userWithCredentials(ConfigReader.get("login").toUpperCase(), ConfigReader.get("password"));
        loginPage.typeLoginForm(user);
        loginPage.clickBtnYalla();
        Assert.assertTrue(new PopupPage(getDriver()).isTextInPopupMessagePresent("Logged in success"));
    }

    //------------------LOGOUT----------------------------
    @Story("Logout")
    @Test(groups = "regression")
    public void logoutTest(){
        SoftAssert softAssert = new SoftAssert();
        loginPage.typeLoginForm(userWithCredentials(ConfigReader.get("login"), ConfigReader.get("password")));
        loginPage.clickBtnYalla();
        PopupPage popupPage = new PopupPage(getDriver());
        Assert.assertTrue(popupPage.isTextInPopupMessagePresent("Logged in success"),
                "precondition: login succeeded");
        popupPage.clickBtnOk();

        Header header = loginPage.getHeader();
        header.clickWithoutNavigation(HeaderMenuItem.LOG_OUT);

        softAssert.assertTrue(header.isItemDisplayed(HeaderMenuItem.LOGIN), "validate 'Log in' is shown after logout");
        softAssert.assertTrue(header.isItemDisplayed(HeaderMenuItem.SIGN_UP), "validate 'Sign up' is shown after logout");
        softAssert.assertTrue(header.isItemNotDisplayed(HeaderMenuItem.LOG_OUT), "validate 'Logout' is hidden after logout");
        softAssert.assertAll();
    }

    //------------------LOGIN NEGATIVE TESTS: WRONG CREDENTIALS----------------------------
    // the form is valid, the site answers "Login or Password incorrect"
    // row: case name, login, password
    @DataProvider
    public Object[][] wrongCredentials() {
        String login = ConfigReader.get("login");
        String password = ConfigReader.get("password");
        User notRegistered = positiveUser();
        return new Object[][]{
                {"password without special symbol", login, "Pass1234"},
                {"password with trailing spaces", login, password + "   "},
                {"password in lower case", login, password.toLowerCase()},   // password is case-sensitive
                {"email valid but wrong", "harry1@gmail.com", password},
                {"user is not registered", notRegistered.getUsername(), notRegistered.getPassword()}
        };
    }

    @Story("Wrong credentials")
    @Test(groups = "regression", dataProvider = "wrongCredentials")
    public void loginNegativeTest_WrongCredentials(String caseName, String login, String password){
        loginPage.typeLoginForm(userWithCredentials(login, password));
        loginPage.clickBtnYalla();
        Assert.assertTrue(new PopupPage(getDriver()).isTextInPopupMessagePresent("Login or Password incorrect"),
                "validate popup for " + caseName);
    }

    //------------------LOGIN NEGATIVE TESTS: INVALID FORM----------------------------
    // the form shows errors and Yalla stays disabled (so it is not clicked)
    // row: case name, login, password, expected error texts
    @DataProvider
    public Object[][] invalidLoginForm() {
        String login = ConfigReader.get("login");
        String password = ConfigReader.get("password");
        return new Object[][]{
                {"empty fields", "", "",
                        new String[]{"Email is required", "Password is required"}},
                {"empty email", "", password,
                        new String[]{"Email is required"}},
                {"empty password", login, "",
                        new String[]{"Password is required"}},
                {"wrong email, empty password", "harrygmail.com", "",
                        new String[]{"It'snot look like email", "Password is required"}}
        };
    }

    @Severity(SeverityLevel.NORMAL)
    @Story("Login form validation")
    @Test(groups = "regression", dataProvider = "invalidLoginForm")
    public void loginNegativeTest_InvalidForm(String caseName, String login, String password, String[] expectedErrors){
        SoftAssert softAssert = new SoftAssert();
        loginPage.typeLoginForm(userWithCredentials(login, password));
        for (String error : expectedErrors) {
            softAssert.assertTrue(loginPage.isTextInErrorPresent(error),
                    "validate error '" + error + "' for " + caseName);
        }
        softAssert.assertFalse(loginPage.isBtnYallaEnabled(), "validate button Yalla is disabled for " + caseName);
        softAssert.assertAll();
    }
}
