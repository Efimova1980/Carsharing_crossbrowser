package ui_tests;

import dto.AuthenticationBodyDto;
import dto.RegistrationBodyDto;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
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

@Severity(SeverityLevel.CRITICAL)
public class LoginTests extends AppManager {
    LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void goToLoginPage(){
        loginPage = new HomePage(getDriver()).getHeader().click(HeaderMenuItem.LOGIN);
    }

    //------------------LOGIN POSITIVE TESTS----------------------------
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "smoke")
    public void loginPositiveTest(){
        AuthenticationBodyDto user = userWithCredentials(ConfigReader.get("login"), ConfigReader.get("password"));
        loginPage.loginWithConfirm(user);
        Assert.assertTrue(new PopupPage(getDriver()).isTextInPopupMessagePresent("Logged in success"));
    }

    @Test(groups = "regression")
    public void loginPositiveTest_EmailInUpperCase(){
        AuthenticationBodyDto user = userWithCredentials(ConfigReader.get("login").toUpperCase(), ConfigReader.get("password"));
        loginPage.loginWithConfirm(user);
        Assert.assertTrue(new PopupPage(getDriver()).isTextInPopupMessagePresent("Logged in success"));
    }

    @Test(groups = "regression")
    public void loginPositiveTest_EnterKey(){
        AuthenticationBodyDto user = userWithCredentials(ConfigReader.get("login"), ConfigReader.get("password"));
        loginPage.loginWithEnterConfirm(user);
        Assert.assertTrue(new PopupPage(getDriver()).isTextInPopupMessagePresent("Logged in success"));
    }

    //------------------LOGOUT POSITIVE TESTS----------------------------
    @Test(groups = "regression")
    public void logoutTest(){
        SoftAssert softAssert = new SoftAssert();
        loginPage.loginWithConfirm(userWithCredentials(ConfigReader.get("login"), ConfigReader.get("password")));
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
        RegistrationBodyDto notRegistered = positiveUser();
        return new Object[][]{
                {"password in upper case", login, password.toUpperCase()},
                {"password with trailing spaces", login, password + "   "},
                {"password with leading spaces", login, "   " + password},
                {"email is not registered", notRegistered.getUsername(), password},
                {"user is not registered", notRegistered.getUsername(), notRegistered.getPassword()}
        };
    }

    @Test(groups = "regression", dataProvider = "wrongCredentials")
    public void loginNegativeTest_WrongCredentials(String caseName, String login, String password){
        loginPage.loginWithConfirm(userWithCredentials(login, password));
        Assert.assertTrue(new PopupPage(getDriver()).isTextInPopupMessagePresent("Login or Password incorrect"),
                "validate popup for " + caseName);
    }

    //------------------LOGIN NEGATIVE TESTS: EMPTY FIELDS----------------------------
    // the form shows errors and Yalla stays disabled (so it is not clicked)
    // row: case name, login, password, expected error texts
    @DataProvider
    public Object[][] emptyFields() {
        String login = ConfigReader.get("login");
        String password = ConfigReader.get("password");
        return new Object[][]{
                {"empty fields", "", "",
                        new String[]{"Email is required", "Password is required"}},
                {"empty email", "", password,
                        new String[]{"Email is required"}},
                {"empty password", login, "",
                        new String[]{"Password is required"}}
        };
    }

    @Severity(SeverityLevel.NORMAL)
    @Test(groups = "regression", dataProvider = "emptyFields")
    public void loginNegativeTest_EmptyFields(String caseName, String login, String password, String[] expectedErrors){
        SoftAssert softAssert = new SoftAssert();
        loginPage.loginWOConfirm(userWithCredentials(login, password));
        for (String error : expectedErrors) {
            softAssert.assertTrue(loginPage.isTextInErrorPresent(error),
                    "validate error '" + error + "' for " + caseName);
        }
        softAssert.assertFalse(loginPage.isBtnYallaEnabled(), "validate button Yalla is disabled for " + caseName);
        softAssert.assertAll();
    }
}
