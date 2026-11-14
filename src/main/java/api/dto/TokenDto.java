package api.dto;

import lombok.*;

// success body of registration and login: {"accessToken": "<JWT>"}
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class TokenDto {
    private String accessToken;
}
