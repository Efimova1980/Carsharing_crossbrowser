package dto;

import lombok.*;

// body of POST /v1/user/registration/usernamepassword; also fills the registration form
// username is the email
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class RegistrationBodyDto {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
}
