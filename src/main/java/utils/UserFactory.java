package utils;

import dto.User;
import net.datafaker.Faker;

public class UserFactory {
    static Faker faker = new Faker();
    public static User positiveUser(){
        User user = User.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .username(faker.internet().emailAddress())
                .password("Pass123!")   // minimal allowed length: exactly 8 chars
                .build();
        return user;
    }

    // user for the login form: only email and password are needed
    public static User userWithCredentials(String username, String password){
        return User.builder()
                .username(username)
                .password(password)
                .build();
    }

    // user who is already registered on the site: login and password from config
    public static User existingUser(){
        return User.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .username(ConfigReader.get("login"))
                .password(ConfigReader.get("password"))
                .build();
    }

    public static User emptyUser(){
        User user = User.builder()
                .firstName("")
                .lastName("")
                .username("")
                .password("")
                .build();
        return user;
    }
}
