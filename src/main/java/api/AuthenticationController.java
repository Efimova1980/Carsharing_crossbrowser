package api;

import dto.AuthenticationBodyDto;
import dto.RegistrationBodyDto;
import dto.TokenDto;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Authentication Controller of the backend (the name matches the Swagger tag).
 * Both calls answer 200 with {@link TokenDto} {"accessToken": "<JWT>"} on success.
 */
public class AuthenticationController extends BaseApi implements Endpoints {

    @Step("API: register user {user.username}")
    public static Response registration(RegistrationBodyDto user) {
        logger.info("POST {} for user {}", REGISTRATION_PATH, user.getUsername());
        return given(requestSpec())
                .body(user)
                .post(REGISTRATION_PATH);
    }

    @Step("API: login as {user.username}")
    public static Response login(AuthenticationBodyDto user) {
        logger.info("POST {} for user {}", LOGIN_PATH, user.getUsername());
        return given(requestSpec())
                .body(user)
                .post(LOGIN_PATH);
    }

    // logs in, checks the call succeeded and returns a ready-to-use Authorization header value
    @Step("API: login and get token for {user.username}")
    public static String loginAndGetToken(AuthenticationBodyDto user) {
        Response response = login(user);
        response.then().statusCode(200);
        return "Bearer " + response.as(TokenDto.class).getAccessToken();
    }
}
