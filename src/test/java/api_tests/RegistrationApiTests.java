package api_tests;

import api.AuthenticationController;
import com.networknt.schema.ValidationMessage;
import dto.ErrorMessageDtoMapStringString;
import dto.ErrorMessageDtoString;
import dto.RegistrationBodyDto;
import dto.TokenDto;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utils.TestNGListener;

import java.io.IOException;
import java.util.Set;

import static utils.SchemaValidator.validateJsonSchema;
import static utils.UserFactory.*;

@Listeners(TestNGListener.class)

@Severity(SeverityLevel.CRITICAL)
public class RegistrationApiTests extends AuthenticationController {

    //----------------------------REGISTRATION: POSITIVE TESTS-----------------------------
    // datafaker email is Latin-only and unique per run: no clash with the known
    // site bug of accepting Cyrillic domains, no "User already exists" on reruns
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"api", "api_smoke"})
    public void registrationPositiveTest() throws IOException {
        Response response = registration(positiveUser());
        Assert.assertEquals(response.statusCode(), 200, "validate status code");
        Set<ValidationMessage> errors = validateJsonSchema(response.asString(), "TokenDto.json");
        Assert.assertTrue(errors.isEmpty(), "validate json schema: " + errors);
        Assert.assertFalse(response.as(TokenDto.class).getAccessToken().isBlank(),
                "validate accessToken is returned");
    }

    //----------------------------REGISTRATION: NEGATIVE TESTS-----------------------------
    @Test(groups = "api")
    public void registrationNegativeTest_DuplicateUser() throws IOException {
        // the user from config is already registered: no new users are created by this test
        Response response = registration(existingUser());
        Assert.assertEquals(response.statusCode(), 400, "validate status code");
        Set<ValidationMessage> errors = validateJsonSchema(response.asString(), "ErrorMessageDtoString.json");
        Assert.assertTrue(errors.isEmpty(), "validate json schema: " + errors);
        Assert.assertEquals(response.as(ErrorMessageDtoString.class).getMessage(), "User already exists",
                "validate error message");
    }

    //------------------------------INVALID EMAIL------------------------------------------
    // a blank email is a separate case: its message is "must not be blank",
    // not the "well-formed email" one from the invalidEmails provider
    @Test(groups = "api")
    public void registrationNegativeTest_BlankEmail() throws IOException {
        RegistrationBodyDto user = positiveUser();
        user.setUsername("");
        Response response = registration(user);
        Assert.assertEquals(response.statusCode(), 400, "validate status code");
        Set<ValidationMessage> errors = validateJsonSchema(response.asString(), "ErrorMessageDtoMapStringString.json");
        Assert.assertTrue(errors.isEmpty(), "validate json schema: " + errors);
        Assert.assertEquals(response.as(ErrorMessageDtoMapStringString.class).getMessage().get("username"),
                "must not be blank", "validate error message");
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
                // (200 + token), so this row fails until the bug is fixed; the local part is unique per run,
                // otherwise the next run would get "User already exists" instead of the bug
                {positiveUser().getUsername().split("@")[0] + "@почта.com"}
        };
    }

    @Test(groups = "api", dataProvider = "invalidEmails")
    public void registrationNegativeTest_InvalidEmail(String email) {
        RegistrationBodyDto user = positiveUser();
        user.setUsername(email);
        Response response = registration(user);
        Assert.assertEquals(response.statusCode(), 400,
                "validate status code for email '" + email + "'");
        Assert.assertEquals(response.as(ErrorMessageDtoMapStringString.class).getMessage().get("username"),
                "must be a well-formed email address",
                "validate error message for email '" + email + "'");
    }

    //------------------------------INVALID PASSWORD---------------------------------------

    // expected: a field validation error, the same format as for a blank email
    // known backend bug: the answer is the default Spring error (timestamp as a number,
    // no message), not the API error format. The test fails until the bug is fixed (product defect).
    // TO CLARIFY: Swagger allows "any object" for 400, so the error format is not specified
    @Test(groups = "api")
    public void registrationNegativeTest_BlankPassword() throws IOException {
        RegistrationBodyDto user = positiveUser();
        user.setPassword("");
        Response response = registration(user);
        Assert.assertEquals(response.statusCode(), 400, "validate status code");
        Set<ValidationMessage> errors = validateJsonSchema(response.asString(), "ErrorMessageDtoMapStringString.json");
        Assert.assertTrue(errors.isEmpty(), "validate json schema: " + errors);
        ErrorMessageDtoMapStringString body = response.as(ErrorMessageDtoMapStringString.class);
        Assert.assertTrue(body.getMessage() != null && body.getMessage().containsKey("password"),
                "validate error for field password");
    }

    /*
    password rule: ^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@$#^&*!])(?=.*[a-zA-Z]).{8,}$
    the server answers with one combined message, so the row expects its distinctive fragment;
    the blank password is a separate test above: its answer has a different format (known bug)
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

    @Test(groups = "api", dataProvider = "invalidPasswords")
    public void registrationNegativeTest_InvalidPassword(String password, String expectedFragment) {
        RegistrationBodyDto user = positiveUser();
        user.setPassword(password);
        Response response = registration(user);
        Assert.assertEquals(response.statusCode(), 400,
                "validate status code for password '" + password + "'");
        Assert.assertTrue(response.as(ErrorMessageDtoMapStringString.class).getMessage().get("password").contains(expectedFragment),
                "validate error message for password '" + password + "'");
    }
}
