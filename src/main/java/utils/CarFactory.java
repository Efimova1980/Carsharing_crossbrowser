package utils;

import dto.CarDto;
import utils.enums.Fuel;
import net.datafaker.Faker;

public class CarFactory {
    static Faker faker = new Faker();
    public static CarDto positiveCar(){
        return CarDto.builder()
                .city("Rehovot")
                .manufacture(faker.vehicle().manufacturer())
                .model(faker.vehicle().model())
                .year(Integer.toString(faker.number().numberBetween(1885, 2025)))
                .serialNumber(faker.vehicle().licensePlate())
                .seats(faker.number().numberBetween(2,20))
                .fuel(faker.options().option(Fuel.values()).getValue())
                .carClass(faker.vehicle().carType())
                .pricePerDay(faker.number().randomDouble(2,1, 999))
                .about(faker.text().text(0, 500))
                .build();
    }

    // a serial number no car can have: unique per call, unlike a random license plate
    public static String notExistingSerialNumber(){
        return "NOT-EXISTING-" + System.currentTimeMillis();
    }
}
