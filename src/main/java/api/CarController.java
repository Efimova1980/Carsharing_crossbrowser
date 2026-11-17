package api;

import dto.CarDto;
import dto.CarsDto;
import dto.SearchDto;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Car Controller of the backend (the name matches the Swagger tag).
 * Search needs no authorization; adding a car needs a Bearer token
 * from {@link AuthenticationController}.
 */
public class CarController extends BaseApi implements Endpoints {

    public static Response search(SearchDto searchDto) {
        logger.info("POST {} city='{}' {}..{}", SEARCH_PATH, searchDto.getCity(),
                searchDto.getStartDate(), searchDto.getEndDate());
        return given(baseRequestSpecification())
                .body(searchDto)
                .post(SEARCH_PATH);
    }

    public static Response addCar(CarDto car, String bearerToken) {
        logger.info("POST {} serialNumber={}", CARS_PATH, car.getSerialNumber());
        return given(baseRequestSpecification())
                .header("Authorization", bearerToken)
                .body(car)
                .post(CARS_PATH);
    }

    public static Response addCarWithoutToken(CarDto car) {
        logger.info("POST {} without token, serialNumber={}", CARS_PATH, car.getSerialNumber());
        return given(baseRequestSpecification())
                .body(car)
                .post(CARS_PATH);
    }

    public static Response deleteCar(String serialNumber, String bearerToken) {
        logger.info("DELETE {} serialNumber={}", CAR_BY_SERIAL_NUMBER_PATH, serialNumber);
        return given(baseRequestSpecification())
                .header("Authorization", bearerToken)
                .delete(CAR_BY_SERIAL_NUMBER_PATH, serialNumber);
    }

    public static Response deleteCarWithoutToken(String serialNumber) {
        logger.info("DELETE {} without token, serialNumber={}", CAR_BY_SERIAL_NUMBER_PATH, serialNumber);
        return given(baseRequestSpecification())
                .delete(CAR_BY_SERIAL_NUMBER_PATH, serialNumber);
    }

    // cars of the user the token belongs to
    public static Response getUserCars(String bearerToken) {
        logger.info("GET {}", MY_CARS_PATH);
        return given(baseRequestSpecification())
                .header("Authorization", bearerToken)
                .get(MY_CARS_PATH);
    }

    // cleanup after a test: deletes the car only if it is in the user's cars;
    // a failed delete is only logged, it must not break the next tests
    public static void deleteCarIfExists(String serialNumber, String bearerToken) {
        boolean exists = getUserCars(bearerToken).as(CarsDto.class).getCars().stream()
                .anyMatch(c -> serialNumber.equals(c.getSerialNumber()));
        if (exists) {
            Response response = deleteCar(serialNumber, bearerToken);
            if (response.statusCode() != 200)
                logger.warn("Cleanup: car {} was not deleted, status {}", serialNumber, response.statusCode());
        }
    }
}
