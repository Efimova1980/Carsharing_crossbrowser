package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Single place to read test settings (browser, gridUrl, baseUrl, headless, login, password).
 * <p>
 * Where a value comes from, highest priority first:
 * <ol>
 *     <li>JVM system property: {@code .\gradlew crossbrowser -Dbrowser=firefox}.
 *         Gradle passes to the tests only the keys listed in {@code configKeys} in build.gradle;</li>
 *     <li>config.properties (src/main/resources) - default values, file is ignored by git;</li>
 *     <li>defaultValue in {@link #get(String, String)}, if the key is not set anywhere.</li>
 * </ol>
 * Use ConfigReader.get(...) in the code, not System.getProperty(...) directly:
 * System.getProperty alone doesn't see the defaults from config.properties.
 */
public class ConfigReader {
    // key=value pairs from config.properties, read once on the first call to ConfigReader
    private static final Properties properties = load();

    // -Dkey=... from the command line wins over config.properties;
    // an empty value (e.g. "gridUrl=") means "not set" -> null
    public static String get(String key) {
        String value = System.getProperty(key, properties.getProperty(key));
        return value == null || value.isBlank() ? null : value.trim();
    }

    // same as get(key), but returns defaultValue instead of null
    public static String get(String key, String defaultValue) {
        String value = get(key);
        return value == null ? defaultValue : value;
    }

    // config.properties is looked up on the classpath (src/main/resources is copied there by the build)
    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null)
                properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read config.properties", e);
        }
        return properties;
    }
}
