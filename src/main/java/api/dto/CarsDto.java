package api.dto;

import lombok.*;

import java.util.List;

// success body of POST /v1/cars/search: {"cars": [...]}
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class CarsDto {
    private List<CarDto> cars;
}
