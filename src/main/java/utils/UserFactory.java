package utils;

import dto.AuthenticationBodyDto;
import dto.RegistrationBodyDto;
import net.datafaker.Faker;

public class UserFactory {
    static Faker faker = new Faker();
    public static RegistrationBodyDto positiveUser(){
        RegistrationBodyDto user = RegistrationBodyDto.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .username(faker.internet().emailAddress())
                .password("Pass123!")   // minimal allowed length: exactly 8 chars
                .build();
        return user;
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

    public static RegistrationBodyDto emptyUser(){
        RegistrationBodyDto user = RegistrationBodyDto.builder()
                .firstName("")
                .lastName("")
                .username("")
                .password("")
                .build();
        return user;
    }
}
