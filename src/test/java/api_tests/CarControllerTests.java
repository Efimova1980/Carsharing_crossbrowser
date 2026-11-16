package api_tests;

import api.CarController;
import com.networknt.schema.ValidationMessage;
import dto.CarDto;
import dto.CarsDto;
import dto.ErrorMessageDtoMapStringString;
import dto.ErrorMessageDtoString;
import dto.ResponseMessageDto;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utils.TestNGListener;

import java.io.IOException;
import java.util.Set;

import static api.AuthenticationController.loginAndGetToken;
import static utils.CarFactory.notExistingSerialNumber;
import static utils.CarFactory.positiveCar;
import static utils.SchemaValidator.validateJsonSchema;
import static utils.SearchFactory.*;
import static utils.UserFactory.existingUserCredentials;

@Listeners(TestNGListener.class)

@Severity(SeverityLevel.CRITICAL)
public class CarControllerTests extends CarController {
    // serial number of the car a test has added; such a car is deleted after the test
    private String addedCarSerialNumber;

    // runs even if the test failed, so cars added by tests don't pile up on the user's account;
    // a failed delete is only logged: it must not break the next tests
    @AfterMethod(alwaysRun = true)
    public void deleteAddedCar() {
        if (addedCarSerialNumber == null)
            return;
        Response response = deleteCar(addedCarSerialNumber, loginAndGetToken(existingUserCredentials()));
        if (response.statusCode() != 200)
            logger.warn("Cleanup: car {} was not deleted, status {}", addedCarSerialNumber, response.statusCode());
        addedCarSerialNumber = null;
    }

    //----------------------------SEARCH: POSITIVE TESTS-----------------------------------
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"api", "api_smoke"})
    public void searchPositiveTest() throws IOException {
        Response response = search(positiveSearch());
        Assert.assertEquals(response.statusCode(), 200, "validate status code");
        Set<ValidationMessage> errors = validateJsonSchema(response.asString(), "CarsDto.json");
        Assert.assertTrue(errors.isEmpty(), "validate json schema: " + errors);

        CarsDto carsDto = response.as(CarsDto.class);
        Assert.assertNotNull(carsDto.getCars(), "validate cars list is present");
        SoftAssert softAssert = new SoftAssert();
        for (CarDto car : carsDto.getCars()) {
            softAssert.assertEquals(car.getCity(), "Rehovot",
                    "validate city of found car " + car.getSerialNumber());
        }
        softAssert.assertAll();
    }

    //----------------------------SEARCH: NEGATIVE TESTS-----------------------------------
    // expected as in the UI: a search without a city is rejected with a field error
    // known backend bug: answers 200 with an empty list. The test fails until the bug is fixed (product defect).
    // TO CLARIFY: Swagger has no constraints for SearchDto; expected result is taken from the UI
    @Test(groups = "api")
    public void searchNegativeTest_BlankCity() throws IOException {
        Response response = search(blankCitySearch());
        Assert.assertEquals(response.statusCode(), 400, "validate a blank city is rejected");
        Set<ValidationMessage> errors = validateJsonSchema(response.asString(), "ErrorMessageDtoMapStringString.json");
        Assert.assertTrue(errors.isEmpty(), "validate json schema: " + errors);
        ErrorMessageDtoMapStringString body = response.as(ErrorMessageDtoMapStringString.class);
        Assert.assertTrue(body.getMessage() != null && body.getMessage().containsKey("city"),
                "validate error for field city");
    }

    // expected as in the UI: city and both dates are required, an empty request is rejected
    // known backend bug: answers 200 with an empty list. The test fails until the bug is fixed (product defect).
    // TO CLARIFY: Swagger has no constraints for SearchDto; expected result is taken from the UI
    @Test(groups = "api")
    public void searchNegativeTest_EmptyBody() throws IOException {
        Response response = search(emptySearch());
        Assert.assertEquals(response.statusCode(), 400, "validate an empty request is rejected");
        Set<ValidationMessage> errors = validateJsonSchema(response.asString(), "ErrorMessageDtoMapStringString.json");
        Assert.assertTrue(errors.isEmpty(), "validate json schema: " + errors);
        ErrorMessageDtoMapStringString body = response.as(ErrorMessageDtoMapStringString.class);
        Assert.assertNotNull(body.getMessage(), "validate field errors are returned");
        SoftAssert softAssert = new SoftAssert();
        for (String field : new String[]{"city", "startDate", "endDate"}) {
            softAssert.assertTrue(body.getMessage().containsKey(field), "validate error for field " + field);
        }
        softAssert.assertAll();
    }

    // expected as in the UI: an end date before the start date is rejected
    // known backend bug: accepted, cars are returned. The test fails until the bug is fixed (product defect).
    // TO CLARIFY: Swagger has no constraints for the dates and does not say which error format
    // a date range error has; a text error is expected (the error is not about one field),
    // change to ErrorMessageDtoMapStringString.json if the backend answers with field errors
    @Test(groups = "api")
    public void searchNegativeTest_EndDateBeforeStartDate() throws IOException {
        Response response = search(endDateBeforeStartDateSearch());
        Assert.assertEquals(response.statusCode(), 400,
                "validate an invalid date range is rejected");
        Set<ValidationMessage> errors = validateJsonSchema(response.asString(), "ErrorMessageDtoString.json");
        Assert.assertTrue(errors.isEmpty(), "validate json schema: " + errors);
    }

    // expected as in the UI ("You can't book car for less than a day"): the same start and end day is rejected
    // known backend bug: accepted, cars are returned. The test fails until the bug is fixed (product defect).
    // TO CLARIFY: Swagger has no constraints for the dates and does not say which error format
    // a date range error has; a text error is expected, as for the end date before the start date
    @Test(groups = "api")
    public void searchNegativeTest_SameDays() throws IOException {
        Response response = search(sameDaysSearch());
        Assert.assertEquals(response.statusCode(), 400,
                "validate a search for less than a day is rejected");
        Set<ValidationMessage> errors = validateJsonSchema(response.asString(), "ErrorMessageDtoString.json");
        Assert.assertTrue(errors.isEmpty(), "validate json schema: " + errors);
    }

    // expected as in the UI ("You can't pick date before today"): dates in the past are rejected
    // known backend bug: accepted, cars are returned. The test fails until the bug is fixed (product defect).
    // TO CLARIFY: Swagger has no constraints for the dates and does not say which error format
    // a date error has; a text error is expected, as for the end date before the start date
    @Test(groups = "api")
    public void searchNegativeTest_DatesInPast() throws IOException {
        Response response = search(datesInPastSearch());
        Assert.assertEquals(response.statusCode(), 400,
                "validate a search with dates in the past is rejected");
        Set<ValidationMessage> errors = validateJsonSchema(response.asString(), "ErrorMessageDtoString.json");
        Assert.assertTrue(errors.isEmpty(), "validate json schema: " + errors);
    }

    //----------------------------ADD CAR: POSITIVE TESTS----------------------------------
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"api", "api_smoke"})
    public void addCarPositiveTest() throws IOException {
        CarDto car = positiveCar();
        addedCarSerialNumber = car.getSerialNumber();   // deleted in deleteAddedCar() after the test
        Response response = addCar(car, loginAndGetToken(existingUserCredentials()));
        Assert.assertEquals(response.statusCode(), 200, "validate status code");
        Set<ValidationMessage> errors = validateJsonSchema(response.asString(), "ResponseMessageDto.json");
        Assert.assertTrue(errors.isEmpty(), "validate json schema: " + errors);
        Assert.assertEquals(response.as(ResponseMessageDto.class).getMessage(), "Car added successfully",
                "validate success message");
    }

    //----------------------------ADD CAR: NEGATIVE TESTS----------------------------------
    @Test(groups = "api")
    public void addCarNegativeTest_MissingSerialNumber() throws IOException {
        CarDto car = positiveCar();
        car.setSerialNumber(null);   // dropped from JSON by the NON_NULL mapper setting
        Response response = addCar(car, loginAndGetToken(existingUserCredentials()));
        Assert.assertEquals(response.statusCode(), 400, "validate status code");
        Set<ValidationMessage> errors = validateJsonSchema(response.asString(), "ErrorMessageDtoMapStringString.json");
        Assert.assertTrue(errors.isEmpty(), "validate json schema: " + errors);
        Assert.assertEquals(response.as(ErrorMessageDtoMapStringString.class).getMessage().get("serialNumber"),
                "must not be blank", "validate error message");
    }

    // expected by Swagger: 401 (the endpoint needs a Bearer token; 403 is not documented),
    // with an error body like for an invalid token
    // known backend bug: answers 403 with an empty body. The test fails until the bug is fixed (product defect).
    // TO CLARIFY: the error body format for a missing token is not specified in Swagger
    @Test(groups = "api")
    public void addCarNegativeTest_NoToken() throws IOException {
        Response response = addCarWithoutToken(positiveCar());
        Assert.assertEquals(response.statusCode(), 401, "validate status code");
        Set<ValidationMessage> errors = validateJsonSchema(response.asString(), "ErrorMessageDtoString.json");
        Assert.assertTrue(errors.isEmpty(), "validate json schema: " + errors);
    }

    @Test(groups = "api")
    public void addCarNegativeTest_InvalidToken() throws IOException {
        Response response = addCar(positiveCar(), "Bearer garbage.token.here");
        Assert.assertEquals(response.statusCode(), 401, "validate status code");
        Set<ValidationMessage> errors = validateJsonSchema(response.asString(), "ErrorMessageDtoString.json");
        Assert.assertTrue(errors.isEmpty(), "validate json schema: " + errors);
        Assert.assertTrue(response.as(ErrorMessageDtoString.class).getMessage().contains("Malformed JWT"),
                "validate error message");
    }

    //----------------------------DELETE CAR: POSITIVE TESTS-------------------------------
    // the car is created by this test, so nothing else is deleted; the answer text is not
    // specified in Swagger, so the result is checked by the user's car list instead
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"api", "api_smoke"})
    public void deleteCarPositiveTest() throws IOException {
        String token = loginAndGetToken(existingUserCredentials());
        CarDto car = positiveCar();
        Assert.assertEquals(addCar(car, token).statusCode(), 200, "precondition: car is added");

        Response response = deleteCar(car.getSerialNumber(), token);
        Assert.assertEquals(response.statusCode(), 200, "validate status code");
        Set<ValidationMessage> errors = validateJsonSchema(response.asString(), "ResponseMessageDto.json");
        Assert.assertTrue(errors.isEmpty(), "validate json schema: " + errors);

        Response myCars = getUserCars(token);
        Assert.assertEquals(myCars.statusCode(), 200, "precondition: user's cars are returned");
        Assert.assertTrue(myCars.as(CarsDto.class).getCars().stream()
                        .noneMatch(c -> car.getSerialNumber().equals(c.getSerialNumber())),
                "validate the deleted car is not in the user's cars");
    }

    //----------------------------DELETE CAR: NEGATIVE TESTS-------------------------------
    // expected by Swagger: 401 (the endpoint needs a Bearer token; 403 is not documented)
    // known backend bug: answers 403, as for adding a car without a token.
    // The test fails until the bug is fixed (product defect).
    // TO CLARIFY: the error body format for a missing token is not specified in Swagger
    @Test(groups = "api")
    public void deleteCarNegativeTest_NoToken() throws IOException {
        Response response = deleteCarWithoutToken(notExistingSerialNumber());
        Assert.assertEquals(response.statusCode(), 401, "validate status code");
        Set<ValidationMessage> errors = validateJsonSchema(response.asString(), "ErrorMessageDtoString.json");
        Assert.assertTrue(errors.isEmpty(), "validate json schema: " + errors);
    }

    // expected: an error for a car that does not exist
    // TO CLARIFY: REST convention is 404 Not Found, but Swagger documents only 200/400/401/500
    // for this endpoint, so 400 with a text error is expected; change to 404 if the backend
    // is meant to answer so
    @Test(groups = "api")
    public void deleteCarNegativeTest_NotExistingCar() throws IOException {
        Response response = deleteCar(notExistingSerialNumber(), loginAndGetToken(existingUserCredentials()));
        Assert.assertEquals(response.statusCode(), 400, "validate status code");
        Set<ValidationMessage> errors = validateJsonSchema(response.asString(), "ErrorMessageDtoString.json");
        Assert.assertTrue(errors.isEmpty(), "validate json schema: " + errors);
    }
}
