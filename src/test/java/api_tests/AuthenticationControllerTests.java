package api_tests;

import api.AuthenticationController;
import dto.User;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utils.ConfigReader;
import utils.TestNGListener;

import static utils.UserFactory.*;

@Listeners(TestNGListener.class)

@Epic("API")
@Feature("Authentication Controller")
@Severity(SeverityLevel.CRITICAL)
public class AuthenticationControllerTests extends AuthenticationController {

    //----------------------------REGISTRATION: POSITIVE TESTS-----------------------------
    // datafaker email is Latin-only and unique per run: no clash with the known
    // site bug of accepting Cyrillic domains, no "User already exists" on reruns
    @Severity(SeverityLevel.BLOCKER)
    @Story("Successful registration")
    @Test(groups = {"api", "api_smoke"})
    public void registrationPositiveTest() {
        SoftAssert softAssert = new SoftAssert();
        Response response = registration(positiveUser());
        softAssert.assertEquals(response.statusCode(), 200, "validate status code");
        softAssert.assertFalse(response.jsonPath().getString("accessToken").isBlank(),
                "validate accessToken is returned");
        softAssert.assertAll();
    }

    //----------------------------REGISTRATION: NEGATIVE TESTS-----------------------------
    @Story("Already registered user")
    @Test(groups = "api")
    public void registrationNegativeTest_DuplicateUser() {
        // self-contained: the duplicate is created by this very test, not taken from config
        User user = positiveUser();
        Assert.assertEquals(registration(user).statusCode(), 200,
                "precondition: first registration succeeded");

        Response response = registration(user);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 400, "validate status code");
        softAssert.assertEquals(response.jsonPath().getString("message"), "User already exists",
                "validate error message");
        softAssert.assertAll();
    }

    //------------------------------INVALID EMAIL------------------------------------------
    // a blank email is a separate case: its message is "must not be blank",
    // not the "well-formed email" one from the invalidEmails provider
    @Story("Email validation")
    @Test(groups = "api")
    public void registrationNegativeTest_BlankEmail() {
        User user = positiveUser();
        user.setUsername("");
        Response response = registration(user);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 400, "validate status code");
        softAssert.assertEquals(response.jsonPath().getString("message.username"),
                "must not be blank", "validate error message");
        softAssert.assertAll();
    }

    /*
    server-side validation, one message for every case: "must be a well-formed email address"
    row: email
     */
    @DataProvider
    public Object[][] invalidEmails() {
        return new Object[][]{
                {"harrygmail.com"},           // no '@'
                {"harry@@gmail.com"},         // two '@'
                {"ha@rry@gmail.com"},         // '@' inside the name
                {"harry@"},                   // nothing after '@'
                {"@gmail.com"},               // nothing before '@'
                {"ha rry@gmail.com"},         // space inside the name
                {".harry@gmail.com"},         // dot at the start
                // known site bug (same as in the UI regression): a Cyrillic domain is accepted
                // (200 + token), so this row fails on purpose; the local part is unique per run,
                // otherwise the next run would get "User already exists" instead of the bug
                {positiveUser().getUsername().split("@")[0] + "@почта.com"}
        };
    }

    @Story("Email validation")
    @Test(groups = "api", dataProvider = "invalidEmails")
    public void registrationNegativeTest_InvalidEmail(String email) {
        User user = positiveUser();
        user.setUsername(email);
        Response response = registration(user);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 400,
                "validate status code for email '" + email + "'");
        softAssert.assertEquals(response.jsonPath().getString("message.username"),
                "must be a well-formed email address",
                "validate error message for email '" + email + "'");
        softAssert.assertAll();
    }

    //------------------------------INVALID PASSWORD---------------------------------------

    // the server answers 400 with no message field for a blank password
    @Story("Registration validation")
    @Test(groups = "api")
    public void registrationNegativeTest_BlankPassword() {
        User user = positiveUser();
        user.setPassword("");
        Response response = registration(user);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 400, "validate status code");
        softAssert.assertEquals(response.jsonPath().getString("error"), "Bad Request",
                "validate error field");
        softAssert.assertAll();
    }

    /*
    password rule: ^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@$#^&*!])(?=.*[a-zA-Z]).{8,}$
    the server answers with one combined message, so the row expects its distinctive fragment;
    the blank password is a separate test above: its answer has no message field at all
    row: password, expected fragment of message.password
     */
    @DataProvider
    public Object[][] invalidPasswords() {
        return new Object[][]{
                {"Pa1!", "At least 8 characters"},         // < 8 chars
                {"Pass1234", "special characters"},        // no special char
                {"pass123#", "1 uppercase letter"},        // no A-Z
                {"PASS1234#", "1 lowercase letter"},       // no a-z
                {"Password#", "1 number"}                  // no digits
        };
    }

    @Story("Password validation")
    @Test(groups = "api", dataProvider = "invalidPasswords")
    public void registrationNegativeTest_InvalidPassword(String password, String expectedFragment) {
        User user = positiveUser();
        user.setPassword(password);
        Response response = registration(user);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 400,
                "validate status code for password '" + password + "'");
        softAssert.assertTrue(response.jsonPath().getString("message.password").contains(expectedFragment),
                "validate error message for password '" + password + "'");
        softAssert.assertAll();
    }

    //----------------------------LOGIN: POSITIVE TESTS------------------------------------
    @Severity(SeverityLevel.BLOCKER)
    @Story("Successful login")
    @Test(groups = {"api", "api_smoke"})
    public void loginPositiveTest() {
        Response response = login(existingUser());
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 200, "validate status code");
        softAssert.assertFalse(response.jsonPath().getString("accessToken").isBlank(),
                "validate accessToken is returned");
        softAssert.assertAll();
    }

    //----------------------------LOGIN: NEGATIVE TESTS------------------------------------
    // the server answers the same way for every wrong-credentials case
    // row: case name, user
    @DataProvider
    public Object[][] wrongCredentials() {
        User notRegistered = positiveUser();
        return new Object[][]{
                {"wrong password", userWithCredentials(ConfigReader.get("login"), "Wrong123!")},
                {"user is not registered", notRegistered},
                {"blank username", userWithCredentials("", "Pass123!")}
        };
    }

    @Story("Wrong credentials")
    @Test(groups = "api", dataProvider = "wrongCredentials")
    public void loginNegativeTest_WrongCredentials(String caseName, User user) {
        Response response = login(user);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 401, "validate status code for " + caseName);
        softAssert.assertEquals(response.jsonPath().getString("message"), "Login or Password incorrect",
                "validate error message for " + caseName);
        softAssert.assertAll();
    }
}
