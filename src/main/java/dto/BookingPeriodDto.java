package dto;

import lombok.*;

// body of POST /v1/cars/{serialNumber}/booking; dates in ISO format (yyyy-MM-dd)
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class BookingPeriodDto {
    private String startDate;
    private String endDate;
}
