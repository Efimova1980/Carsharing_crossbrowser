package dto;

import lombok.*;

// one city of CitiesDto
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class CityDto {
    private String city;
    private Double lat;
    private Double lng;
}
