package api;

// all backend endpoints in one place; interface fields are public static final constants,
// controllers implement it to use the paths without a class prefix
public interface Endpoints {
    // Authentication Controller
    String REGISTRATION_PATH = "/v1/user/registration/usernamepassword";
    String LOGIN_PATH = "/v1/user/login/usernamepassword";

    // Car Controller
    String SEARCH_PATH = "/v1/cars/search";
    String CARS_PATH = "/v1/cars";
    String CAR_BY_SERIAL_NUMBER_PATH = "/v1/cars/{serialNumber}";
    String MY_CARS_PATH = "/v1/cars/my";
}
