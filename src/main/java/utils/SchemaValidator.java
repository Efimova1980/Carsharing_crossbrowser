package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

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
        JsonNode json = mapper.readTree(responseBody);                    // json tree of the response body
        // loaded from the classpath, so "$ref": "CarDto.json" inside a schema finds the file next to it
        JsonSchema jsonSchema = factory.getSchema(SchemaLocation.of("classpath:schemas/" + schemaName));
        return jsonSchema.validate(json);
    }
}
