package api_tests;

import api.CarController;
import dto.CarDto;
import dto.ErrorMessageDtoMapStringString;
import dto.ErrorMessageDtoString;
import dto.ResponseMessageDto;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utils.TestNGListener;

import java.io.IOException;

import static api.AuthenticationController.loginAndGetToken;
import static utils.CarFactory.positiveCar;
import static utils.SchemaValidator.assertJsonSchema;
import static utils.UserFactory.existingUserCredentials;

@Listeners(TestNGListener.class)

@Severity(SeverityLevel.CRITICAL)
public class AddCarApiTests extends CarController {
    private String token;
    // serial number of the car a test has sent; if the server has added it, it is deleted after the test
    private String addedCarSerialNumber;

    @BeforeMethod(alwaysRun = true)
    public void login() {
        token = loginAndGetToken(existingUserCredentials());
    }

    // runs even if the test failed: a car the test sent must not stay on the user's account
    // if the server has added it
    @AfterMethod(alwaysRun = true)
    public void deleteCarIfLeft() {
        if (addedCarSerialNumber == null)
            return;
        deleteCarIfExists(addedCarSerialNumber, token);
        addedCarSerialNumber = null;
    }

    //----------------------------ADD CAR: POSITIVE TESTS----------------------------------
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"api", "api_smoke"})
    public void addCarPositiveTest() throws IOException {
        CarDto car = positiveCar();
        addedCarSerialNumber = car.getSerialNumber();   // deleted in deleteCarIfLeft() after the test
        Response response = addCar(car, token);
        Assert.assertEquals(response.statusCode(), 200, "validate status code");
        assertJsonSchema(response, "ResponseMessageDto.json");
        Assert.assertEquals(response.as(ResponseMessageDto.class).getMessage(), "Car added successfully",
                "validate success message");
    }

    //----------------------------ADD CAR: NEGATIVE TESTS----------------------------------
    @Test(groups = "api")
    public void addCarNegativeTest_MissingSerialNumber() throws IOException {
        CarDto car = positiveCar();
        car.setSerialNumber(null);   // dropped from JSON by the NON_NULL mapper setting
        Response response = addCar(car, token);
        Assert.assertEquals(response.statusCode(), 400, "validate status code");
        assertJsonSchema(response, "ErrorMessageDtoMapStringString.json");
        Assert.assertEquals(response.as(ErrorMessageDtoMapStringString.class).getMessage().get("serialNumber"),
                "must not be blank", "validate error message");
    }

    // expected by Swagger: 401 (the endpoint needs a Bearer token; 403 is not documented)
    @Test(groups = "api")
    public void addCarNegativeTest_NoToken() throws IOException {
        CarDto car = positiveCar();
        addedCarSerialNumber = car.getSerialNumber();   // deleted after the test if the server has added it
        Response response = addCarWithoutToken(car);
        Assert.assertEquals(response.statusCode(), 401, "validate status code");
        assertJsonSchema(response, "ErrorMessageDtoString.json");
    }

    @Test(groups = "api")
    public void addCarNegativeTest_InvalidToken() throws IOException {
        CarDto car = positiveCar();
        addedCarSerialNumber = car.getSerialNumber();   // deleted after the test if the server has added it
        Response response = addCar(car, "Bearer garbage.token.here");
        Assert.assertEquals(response.statusCode(), 401, "validate status code");
        assertJsonSchema(response, "ErrorMessageDtoString.json");
        Assert.assertTrue(response.as(ErrorMessageDtoString.class).getMessage().contains("Malformed JWT"),
                "validate error message");
    }
}
