package dto;

import lombok.*;

// success body of add car, booking and delete car: {"message": "..."}
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class ResponseMessageDto {
    private String message;
}
