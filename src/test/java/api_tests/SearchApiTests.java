package api_tests;

import api.CarController;
import dto.CarDto;
import dto.CarsDto;
import dto.ErrorMessageDtoMapStringString;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utils.TestNGListener;

import java.io.IOException;

import static utils.SchemaValidator.assertJsonSchema;
import static utils.SearchFactory.*;

@Listeners(TestNGListener.class)

@Severity(SeverityLevel.CRITICAL)
public class SearchApiTests extends CarController {

    //----------------------------SEARCH: POSITIVE TESTS-----------------------------------
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"api", "api_smoke"})
    public void searchPositiveTest() throws IOException {
        Response response = search(positiveSearch());
        Assert.assertEquals(response.statusCode(), 200, "validate status code");
        assertJsonSchema(response, "CarsDto.json");

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
    @Test(groups = "api")
    public void searchNegativeTest_BlankCity() throws IOException {
        Response response = search(blankCitySearch());
        Assert.assertEquals(response.statusCode(), 400, "validate a blank city is rejected");
        assertJsonSchema(response, "ErrorMessageDtoMapStringString.json");
        ErrorMessageDtoMapStringString body = response.as(ErrorMessageDtoMapStringString.class);
        Assert.assertTrue(body.getMessage() != null && body.getMessage().containsKey("city"),
                "validate error for field city");
    }

    // expected as in the UI: city and both dates are required, an empty request is rejected
    @Test(groups = "api")
    public void searchNegativeTest_EmptyBody() throws IOException {
        Response response = search(emptySearch());
        Assert.assertEquals(response.statusCode(), 400, "validate an empty request is rejected");
        assertJsonSchema(response, "ErrorMessageDtoMapStringString.json");
        ErrorMessageDtoMapStringString body = response.as(ErrorMessageDtoMapStringString.class);
        Assert.assertNotNull(body.getMessage(), "validate field errors are returned");
        SoftAssert softAssert = new SoftAssert();
        for (String field : new String[]{"city", "startDate", "endDate"}) {
            softAssert.assertTrue(body.getMessage().containsKey(field), "validate error for field " + field);
        }
        softAssert.assertAll();
    }

    // expected as in the UI: an end date before the start date is rejected
    @Test(groups = "api")
    public void searchNegativeTest_EndDateBeforeStartDate() throws IOException {
        Response response = search(endDateBeforeStartDateSearch());
        Assert.assertEquals(response.statusCode(), 400,
                "validate an invalid date range is rejected");
        assertJsonSchema(response, "ErrorMessageDtoString.json");
    }

    // expected as in the UI ("You can't book car for less than a day"): the same start and end day is rejected
    @Test(groups = "api")
    public void searchNegativeTest_SameDays() throws IOException {
        Response response = search(sameDaysSearch());
        Assert.assertEquals(response.statusCode(), 400,
                "validate a search for less than a day is rejected");
        assertJsonSchema(response, "ErrorMessageDtoString.json");
    }

    // expected as in the UI ("You can't pick date before today"): dates in the past are rejected
    @Test(groups = "api")
    public void searchNegativeTest_DatesInPast() throws IOException {
        Response response = search(datesInPastSearch());
        Assert.assertEquals(response.statusCode(), 400,
                "validate a search with dates in the past is rejected");
        assertJsonSchema(response, "ErrorMessageDtoString.json");
    }
}
