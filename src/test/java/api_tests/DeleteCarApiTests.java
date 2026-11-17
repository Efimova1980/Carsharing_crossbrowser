package api_tests;

import api.CarController;
import dto.CarDto;
import dto.CarsDto;
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
import static utils.CarFactory.notExistingSerialNumber;
import static utils.CarFactory.positiveCar;
import static utils.SchemaValidator.assertJsonSchema;
import static utils.UserFactory.existingUserCredentials;

@Listeners(TestNGListener.class)

@Severity(SeverityLevel.CRITICAL)
public class DeleteCarApiTests extends CarController {
    private String token;
    // the car added before every test: the test deletes it or tries to
    private CarDto car;

    // login and a car to delete
    @BeforeMethod(alwaysRun = true)
    public void loginAndAddCar() {
        token = loginAndGetToken(existingUserCredentials());
        car = positiveCar();
        Response response = addCar(car, token);
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Precondition failed: car was not added, status " + response.statusCode());
        }
    }

    // runs even if the test failed: a car the test did not delete must not stay on the user's account
    @AfterMethod(alwaysRun = true)
    public void deleteCarIfLeft() {
        if (car == null)
            return;
        deleteCarIfExists(car.getSerialNumber(), token);
        car = null;
    }

    //----------------------------DELETE CAR: POSITIVE TESTS-------------------------------
    // the answer text is not specified in Swagger, so the result is checked by the user's car list
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"api", "api_smoke"})
    public void deleteCarPositiveTest() throws IOException {
        Response response = deleteCar(car.getSerialNumber(), token);
        Assert.assertEquals(response.statusCode(), 200, "validate status code");
        assertJsonSchema(response, "ResponseMessageDto.json");

        Response myCars = getUserCars(token);
        if (myCars.statusCode() != 200)
            throw new IllegalStateException("Precondition failed: user's cars are not returned, status " + myCars.statusCode());
        Assert.assertTrue(myCars.as(CarsDto.class).getCars().stream()
                        .noneMatch(c -> car.getSerialNumber().equals(c.getSerialNumber())),
                "validate the deleted car is not in the user's cars");
    }

    //----------------------------DELETE CAR: NEGATIVE TESTS-------------------------------
    // expected by Swagger: 401 (the endpoint needs a Bearer token; 403 is not documented)
    @Test(groups = "api")
    public void deleteCarNegativeTest_NoToken() throws IOException {
        Response response = deleteCarWithoutToken(car.getSerialNumber());
        Assert.assertEquals(response.statusCode(), 401, "validate status code");
        assertJsonSchema(response, "ErrorMessageDtoString.json");
    }

    // 404 Not Found would be more logical, but Swagger does not describe this code for the endpoint
    @Test(groups = "api")
    public void deleteCarNegativeTest_NotExistingCar() throws IOException {
        Response response = deleteCar(notExistingSerialNumber(), token);
        Assert.assertEquals(response.statusCode(), 400, "validate status code");
        assertJsonSchema(response, "ErrorMessageDtoString.json");
    }
}
