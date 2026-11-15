package dto;

import lombok.*;

// error body with one text message, e.g. {"message": "User already exists"}
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class ErrorMessageDtoString {
    private String timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
}
