package api;

import dto.AuthenticationBodyDto;
import dto.RegistrationBodyDto;
import dto.TokenDto;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Authentication Controller of the backend (the name matches the Swagger tag).
 * Both calls answer 200 with {@link TokenDto} {"accessToken": "<JWT>"} on success.
 */
public class AuthenticationController extends BaseApi implements Endpoints {

    public static Response registration(RegistrationBodyDto user) {
        logger.info("POST {} for user {}", REGISTRATION_PATH, user.getUsername());
        return given(baseRequestSpecification())
                .body(user)
                .post(REGISTRATION_PATH);
    }

    public static Response login(AuthenticationBodyDto user) {
        logger.info("POST {} for user {}", LOGIN_PATH, user.getUsername());
        return given(baseRequestSpecification())
                .body(user)
                .post(LOGIN_PATH);
    }

    // logs in, checks the call succeeded and returns a ready-to-use Authorization header value
    public static String loginAndGetToken(AuthenticationBodyDto user) {
        Response response = login(user);
        // a failed login is a broken precondition, not a product defect: Allure shows it as Broken
        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "Precondition failed: login as the existing user failed, status " + response.statusCode());
        }
        return "Bearer " + response.as(TokenDto.class).getAccessToken();
    }
}
