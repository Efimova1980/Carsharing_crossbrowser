package utils;

import dto.AuthenticationBodyDto;
import dto.RegistrationBodyDto;
import net.datafaker.Faker;

public class UserFactory {
    static Faker faker = new Faker();
    public static RegistrationBodyDto positiveUser(){
        return RegistrationBodyDto.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .username(faker.internet().emailAddress())
                .password(positivePassword())
                .build();
    }

    // a password with the minimal length (8 characters) and all character types required by Swagger
    public static String positivePassword(){
        return faker.regexify("[A-Z][a-z]{3}[0-9]{3}[@$#^&*!]");
    }

    // user for the login form / login call: only email and password are needed
    public static AuthenticationBodyDto userWithCredentials(String username, String password){
        return AuthenticationBodyDto.builder()
                .username(username)
                .password(password)
                .build();
    }

    // user who is already registered on the site: login and password from config;
    // registration body - for the "user already exists" checks
    public static RegistrationBodyDto existingUser(){
        return RegistrationBodyDto.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .username(ConfigReader.get("login"))
                .password(ConfigReader.get("password"))
                .build();
    }

    // the same registered user for login (login and password from config)
    public static AuthenticationBodyDto existingUserCredentials(){
        return userWithCredentials(ConfigReader.get("login"), ConfigReader.get("password"));
    }
}
