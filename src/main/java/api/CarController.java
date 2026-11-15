package api;

import dto.CarDto;
import dto.SearchDto;
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
    public static Response search(SearchDto searchDto) {
        logger.info("POST {} city='{}' {}..{}", SEARCH_PATH, searchDto.getCity(),
                searchDto.getStartDate(), searchDto.getEndDate());
        return given(requestSpec())
                .body(searchDto)
                .post(SEARCH_PATH);
    }

    @Step("API: add car {car.serialNumber}")
    public static Response addCar(CarDto car, String bearerToken) {
        logger.info("POST {} serialNumber={}", CARS_PATH, car.getSerialNumber());
        return given(requestSpec())
                .header("Authorization", bearerToken)
                .body(car)
                .post(CARS_PATH);
    }

    @Step("API: add car without Authorization header")
    public static Response addCarWithoutToken(CarDto car) {
        logger.info("POST {} without token, serialNumber={}", CARS_PATH, car.getSerialNumber());
        return given(requestSpec())
                .body(car)
                .post(CARS_PATH);
    }

    @Step("API: delete car {serialNumber}")
    public static Response deleteCar(String serialNumber, String bearerToken) {
        logger.info("DELETE {} serialNumber={}", CAR_BY_SERIAL_NUMBER_PATH, serialNumber);
        return given(requestSpec())
                .header("Authorization", bearerToken)
                .delete(CAR_BY_SERIAL_NUMBER_PATH, serialNumber);
    }

    @Step("API: delete car {serialNumber} without Authorization header")
    public static Response deleteCarWithoutToken(String serialNumber) {
        logger.info("DELETE {} without token, serialNumber={}", CAR_BY_SERIAL_NUMBER_PATH, serialNumber);
        return given(requestSpec())
                .delete(CAR_BY_SERIAL_NUMBER_PATH, serialNumber);
    }

    // cars of the user the token belongs to
    @Step("API: get my cars")
    public static Response getUserCars(String bearerToken) {
        logger.info("GET {}", MY_CARS_PATH);
        return given(requestSpec())
                .header("Authorization", bearerToken)
                .get(MY_CARS_PATH);
    }
}
