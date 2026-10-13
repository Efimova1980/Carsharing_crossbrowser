package dto;

import enums.Fuel;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class Car {
    private String city;
    private String manufacture;
    private String model;
    private String year;
    private Fuel fuel;
    private Integer seats;
    private String carClass;
    private String serialNumber;
    private Double pricePerDay;
    private String about;
    private String image;
}

