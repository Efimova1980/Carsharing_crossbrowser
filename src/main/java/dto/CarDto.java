package dto;

import lombok.*;

import java.util.List;

// a car: body of POST /v1/cars and an item of CarsDto; also fills the "Let the car work" form
// fuel is a string as in the Swagger schema: "Diesel", "Petrol", "Hybrid", "Electric", "Gas"
// lat, lng, owner, bookedPeriods are filled by the server
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
    private String image;
    private String owner;
    private List<BookedDto> bookedPeriods;
}
