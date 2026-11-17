package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.restassured.response.Response;
import org.testng.Assert;

import java.io.IOException;
import java.util.Set;

// checks a response body against a JSON schema from src/test/resources/schemas
// (one file per Swagger schema, e.g. "TokenDto.json")
public class SchemaValidator {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

    // returns all mismatches; an empty set means the body matches the schema
    public static Set<ValidationMessage> validateJsonSchema(String responseBody, String schemaName)
            throws IOException {
        JsonNode json = mapper.readTree(responseBody);
        JsonSchema jsonSchema = factory.getSchema(SchemaLocation.of("classpath:schemas/" + schemaName));
        return jsonSchema.validate(json);
    }

    // fails the test if the response body does not match the schema; the message lists all mismatches
    public static void assertJsonSchema(Response response, String schemaName) throws IOException {
        Set<ValidationMessage> errors = validateJsonSchema(response.asString(), schemaName);
        Assert.assertTrue(errors.isEmpty(), "validate json schema " + schemaName + ": " + errors);
    }
}
