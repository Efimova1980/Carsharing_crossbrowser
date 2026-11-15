package dto;

import lombok.*;

// one booked period of a car (CarDto.bookedPeriods); dates in ISO format (yyyy-MM-dd)
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class BookedDto {
    private String email;
    private String startDate;
    private String endDate;
}
