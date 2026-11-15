package dto;

import lombok.*;

// body of POST /v1/user/login/usernamepassword; also fills the login form
// username is the email
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class AuthenticationBodyDto {
    private String username;
    private String password;
}
