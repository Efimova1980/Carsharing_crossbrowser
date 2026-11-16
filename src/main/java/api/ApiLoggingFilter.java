package api;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.net.URI;

import static api.BaseApi.logger;

// writes every API request and response to the log (not to the Allure report)
public class ApiLoggingFilter implements Filter {
    private static final int MAX_BODY_LENGTH = 200;

    @Override
    public Response filter(FilterableRequestSpecification request,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        // only the path, without the backend address
        String path = URI.create(request.getURI()).getPath();
        logger.info("Request: {} {} body: {}", request.getMethod(), path, request.getBody());
        Response response = ctx.next(request, responseSpec);
        logger.info("Response: {} body: {}", response.getStatusCode(), shorten(response.getBody().asString()));
        return response;
    }

    // a search can return all cars of a city (over 1 MB): log only the beginning
    private static String shorten(String body) {
        if (body.length() <= MAX_BODY_LENGTH) {
            return body;
        }
        return body.substring(0, MAX_BODY_LENGTH) + "... (" + body.length() + " chars in total)";
    }
}
