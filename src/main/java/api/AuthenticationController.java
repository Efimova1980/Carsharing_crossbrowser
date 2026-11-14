package api;

import dto.User;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Authentication Controller of the backend (the name matches the Swagger tag).
 * Both calls answer 200 with {"accessToken": "<JWT>"} on success.
 */
public class AuthenticationController extends BaseApi implements Endpoints {

    @Step("API: register user {user.username}")
    public Response registration(User user) {
        logger.info("POST {} for user {}", REGISTRATION_PATH, user.getUsername());
        return given(requestSpec())
                .body(user)
                .post(REGISTRATION_PATH);
    }

    @Step("API: login as {user.username}")
    public Response login(User user) {
        logger.info("POST {} for user {}", LOGIN_PATH, user.getUsername());
        return given(requestSpec())
                .body(user)
                .post(LOGIN_PATH);
    }

    // logs in, checks the call succeeded and returns a ready-to-use Authorization header value
    @Step("API: login and get token for {user.username}")
    public String loginAndGetToken(User user) {
        Response response = login(user);
        response.then().statusCode(200);
        return "Bearer " + response.jsonPath().getString("accessToken");
    }
}
