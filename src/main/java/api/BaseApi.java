package api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import enums.Fuel;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.ConfigReader;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Base of the API layer, the API analogue of {@link manager.AppManager}.
 * Controllers extend it to get the base request specification (config key apiUrl,
 * required), and API tests inherit it through the controllers to get per-test
 * logging routed into the api log file.
 */
public abstract class BaseApi {
    public static final Logger logger = LoggerFactory.getLogger(BaseApi.class);

    // Heroku free dynos sleep: the first request may take tens of seconds to wake the app
    private static final int TIMEOUT_MS = 60_000;

    // the SiftingAppender in logback.xml splits logs by the logName MDC key,
    // so API logs land in their own file: build/logs/log-<time>-api.log
    @BeforeMethod(alwaysRun = true)
    public void setup(Method method) {
        MDC.put("logName", "api");
        logger.info("Start API test: {}.{}", getClass().getSimpleName(), method.getName());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        MDC.remove("logName");
    }

    // base specification for every request: URI from config, JSON, Allure attachments
    protected static RequestSpecification requestSpec() {
        String apiUrl = ConfigReader.get("apiUrl");
        if (apiUrl == null) {
            throw new IllegalStateException(
                    "apiUrl is not set: add it to config.properties or pass -DapiUrl=...");
        }
        return new RequestSpecBuilder()
                .setBaseUri(apiUrl)
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .setConfig(restAssuredConfig())
                .build();
    }

    private static RestAssuredConfig restAssuredConfig() {
        return RestAssuredConfig.config()
                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                        .jackson2ObjectMapperFactory((type, charset) -> objectMapper()))
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParams(Map.of(
                                "http.connection.timeout", TIMEOUT_MS,
                                "http.socket.timeout", TIMEOUT_MS)));
    }

    private static ObjectMapper objectMapper() {
        SimpleModule fuelModule = new SimpleModule();
        // the API expects "Petrol", "Diesel", ...; Jackson's default (enum name) is "PETROL",
        // and Fuel.toString() keeps the name uppercase too, so a custom serializer is needed
        fuelModule.addSerializer(Fuel.class, new JsonSerializer<>() {
            @Override
            public void serialize(Fuel fuel, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(fuel.name().charAt(0) + fuel.name().substring(1).toLowerCase());
            }
        });
        return JsonMapper.builder()
                .addModule(fuelModule)
                // search response has fields our DTOs don't declare (bookedPeriods, ...)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                // partially built DTOs (e.g. a user without names) must not send nulls
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .build();
    }
}
