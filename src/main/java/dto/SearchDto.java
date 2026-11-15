package dto;

import lombok.*;

// request body of POST /v1/cars/search; dates in ISO format (yyyy-MM-dd)
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class SearchDto {
    private String city;
    private String startDate;
    private String endDate;
}
