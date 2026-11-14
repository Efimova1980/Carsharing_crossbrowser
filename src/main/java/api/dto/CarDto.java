package api.dto;

import lombok.*;

// one car in the search response; unknown fields of the answer (bookedPeriods, ...) are ignored
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class CarDto {
    private String serialNumber;
    private String manufacture;
    private String model;
    private String year;
    private String fuel;
    private Integer seats;
    private String carClass;
    private Double pricePerDay;
    private String about;
    private String city;
    private Double lat;
    private Double lng;
    private String owner;
    private String image;
}
