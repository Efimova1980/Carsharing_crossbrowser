package api_tests;

import api.AuthenticationController;
import dto.ErrorMessageDtoString;
import dto.RegistrationBodyDto;
import dto.TokenDto;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.TestNGListener;

import java.io.IOException;

import static utils.SchemaValidator.assertJsonSchema;
import static utils.UserFactory.*;

@Listeners(TestNGListener.class)

@Severity(SeverityLevel.CRITICAL)
public class LoginApiTests extends AuthenticationController {

    //----------------------------LOGIN: POSITIVE TESTS------------------------------------
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"api", "api_smoke"})
    public void loginPositiveTest() throws IOException {
        Response response = login(existingUserCredentials());
        Assert.assertEquals(response.statusCode(), 200, "validate status code");
        assertJsonSchema(response, "TokenDto.json");
        Assert.assertFalse(response.as(TokenDto.class).getAccessToken().isBlank(),
                "validate accessToken is returned");
    }

    //----------------------------LOGIN: NEGATIVE TESTS------------------------------------
    // no data provider: Allure shows test parameters, and the login of the config user must not get into the report
    @Test(groups = "api")
    public void loginNegativeTest_WrongPassword() throws IOException {
        Response response = login(userWithCredentials(ConfigReader.get("login"), positivePassword()));
        checkWrongCredentialsAnswer(response);
    }

    @Test(groups = "api")
    public void loginNegativeTest_NotRegisteredUser() throws IOException {
        RegistrationBodyDto notRegistered = positiveUser();
        Response response = login(userWithCredentials(notRegistered.getUsername(), notRegistered.getPassword()));
        checkWrongCredentialsAnswer(response);
    }

    @Test(groups = "api")
    public void loginNegativeTest_BlankUsername() throws IOException {
        Response response = login(userWithCredentials("", positivePassword()));
        checkWrongCredentialsAnswer(response);
    }

    // the server answers the same way for every wrong-credentials case;
    // these are the only tests that get the login 401 answer, so its schema is checked here
    private void checkWrongCredentialsAnswer(Response response) throws IOException {
        Assert.assertEquals(response.statusCode(), 401, "validate status code");
        assertJsonSchema(response, "ErrorMessageDtoString.json");
        Assert.assertEquals(response.as(ErrorMessageDtoString.class).getMessage(), "Login or Password incorrect",
                "validate error message");
    }
}
