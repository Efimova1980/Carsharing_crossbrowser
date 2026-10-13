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
                .password("Abcd1234!")
                .build();
        return user;
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
