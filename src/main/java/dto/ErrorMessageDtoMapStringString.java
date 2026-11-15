package dto;

import lombok.*;

import java.util.Map;

// validation error body: message is a map "field -> error text",
// e.g. {"message": {"username": "must be a well-formed email address"}}
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class ErrorMessageDtoMapStringString {
    private String timestamp;
    private Integer status;
    private String error;
    private Map<String, String> message;
    private String path;
}
