package dto;

import lombok.*;

import java.util.List;

// success body of GET /v1/cars/cities: {"cities": [...]}
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class CitiesDto {
    private List<CityDto> cities;
}
