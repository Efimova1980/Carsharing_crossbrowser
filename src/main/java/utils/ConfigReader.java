package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = load();

    // -Dkey=... from the command line wins over config.properties
    public static String get(String key) {
        String value = System.getProperty(key, properties.getProperty(key));
        return value == null || value.isBlank() ? null : value.trim();
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return value == null ? defaultValue : value;
    }

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
