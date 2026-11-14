package api;

import api.dto.SearchDto;
import dto.Car;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Car Controller of the backend (the name matches the Swagger tag).
 * Search needs no authorization; adding a car needs a Bearer token
 * from {@link AuthenticationController}.
 */
public class CarController extends BaseApi implements Endpoints {

    @Step("API: search cars in '{searchDto.city}'")
    public Response search(SearchDto searchDto) {
        logger.info("POST {} city='{}' {}..{}", SEARCH_PATH, searchDto.getCity(),
                searchDto.getStartDate(), searchDto.getEndDate());
        return given(requestSpec())
                .body(searchDto)
                .post(SEARCH_PATH);
    }

    @Step("API: add car {car.serialNumber}")
    public Response addCar(Car car, String bearerToken) {
        logger.info("POST {} serialNumber={}", CARS_PATH, car.getSerialNumber());
        return given(requestSpec())
                .header("Authorization", bearerToken)
                .body(car)
                .post(CARS_PATH);
    }

    @Step("API: add car without Authorization header")
    public Response addCarWithoutToken(Car car) {
        logger.info("POST {} without token, serialNumber={}", CARS_PATH, car.getSerialNumber());
        return given(requestSpec())
                .body(car)
                .post(CARS_PATH);
    }
}
