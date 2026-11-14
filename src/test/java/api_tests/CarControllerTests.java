package api_tests;

import api.AuthenticationController;
import api.CarController;
import api.dto.CarDto;
import api.dto.CarsDto;
import api.dto.SearchDto;
import dto.Car;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utils.CarFactory;
import utils.TestNGListener;

import java.time.LocalDate;

import static utils.UserFactory.existingUser;

@Listeners(TestNGListener.class)

@Epic("API")
@Feature("Car Controller")
@Severity(SeverityLevel.CRITICAL)
public class CarControllerTests extends CarController {
    AuthenticationController authenticationController = new AuthenticationController();

    // Bearer token of the existing site user (login/password from config)
    private String freshToken() {
        return authenticationController.loginAndGetToken(existingUser());
    }

    // a car for the API: the factory's image is a local file path, meaningless for a direct call
    private Car apiCar() {
        Car car = CarFactory.positiveCar();
        car.setImage(null);
        return car;
    }

    private SearchDto rehovotSearch(LocalDate start, LocalDate end) {
        return SearchDto.builder()
                .city("Rehovot")
                .startDate(start.toString())
                .endDate(end.toString())
                .build();
    }

    //----------------------------SEARCH: POSITIVE TESTS-----------------------------------
    @Severity(SeverityLevel.BLOCKER)
    @Story("Successful search")
    @Test(groups = {"api", "api_smoke"})
    public void searchPositiveTest() {
        Response response = search(
                rehovotSearch(LocalDate.now().plusDays(10), LocalDate.now().plusDays(20)));
        Assert.assertEquals(response.statusCode(), 200, "validate status code");

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
    // unlike the UI form, the server does not validate the city: it answers 200
    // with an empty list instead of an error — the actual contract is asserted here
    @Story("Search validation")
    @Test(groups = "api")
    public void searchNegativeTest_BlankCity() {
        Response response = search(SearchDto.builder()
                .city("")
                .startDate(LocalDate.now().plusDays(10).toString())
                .endDate(LocalDate.now().plusDays(20).toString())
                .build());
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 200, "validate status code");
        softAssert.assertTrue(response.as(CarsDto.class).getCars().isEmpty(),
                "validate no cars are found for a blank city");
        softAssert.assertAll();
    }

    @Story("Search validation")
    @Test(groups = "api")
    public void searchNegativeTest_EmptyBody() {
        Response response = search(SearchDto.builder().build());
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 200, "validate status code");
        softAssert.assertTrue(response.as(CarsDto.class).getCars().isEmpty(),
                "validate no cars are found for an empty request");
        softAssert.assertAll();
    }

    // known backend bug: an end date before the start date is accepted and cars are
    // returned; the UI blocks such a search. The test fails on purpose (product defect).
    @Story("Search validation")
    @Test(groups = "api")
    public void searchNegativeTest_EndDateBeforeStartDate() {
        Response response = search(
                rehovotSearch(LocalDate.now().plusDays(20), LocalDate.now().plusDays(10)));
        Assert.assertEquals(response.statusCode(), 400,
                "validate an invalid date range is rejected");
    }

    //----------------------------ADD CAR: POSITIVE TESTS----------------------------------
    @Severity(SeverityLevel.BLOCKER)
    @Story("Add car with valid data")
    @Test(groups = {"api", "api_smoke"})
    public void addCarPositiveTest() {
        Response response = addCar(apiCar(), freshToken());
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 200, "validate status code");
        softAssert.assertEquals(response.jsonPath().getString("message"), "Car added successfully",
                "validate success message");
        softAssert.assertAll();
    }

    //----------------------------ADD CAR: NEGATIVE TESTS----------------------------------
    @Story("Add car validation")
    @Test(groups = "api")
    public void addCarNegativeTest_MissingSerialNumber() {
        Car car = apiCar();
        car.setSerialNumber(null);   // dropped from JSON by the NON_NULL mapper setting
        Response response = addCar(car, freshToken());
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 400, "validate status code");
        softAssert.assertEquals(response.jsonPath().getString("message.serialNumber"),
                "must not be blank", "validate error message");
        softAssert.assertAll();
    }

    // without the Authorization header the server answers 403 with an empty body (not 401)
    @Story("Authorization")
    @Test(groups = "api")
    public void addCarNegativeTest_NoToken() {
        Response response = addCarWithoutToken(apiCar());
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 403, "validate status code");
        softAssert.assertTrue(response.body().asString().isEmpty(), "validate body is empty");
        softAssert.assertAll();
    }

    @Story("Authorization")
    @Test(groups = "api")
    public void addCarNegativeTest_InvalidToken() {
        Response response = addCar(apiCar(), "Bearer garbage.token.here");
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 401, "validate status code");
        softAssert.assertTrue(response.jsonPath().getString("message").contains("Malformed JWT"),
                "validate error message");
        softAssert.assertAll();
    }
}
