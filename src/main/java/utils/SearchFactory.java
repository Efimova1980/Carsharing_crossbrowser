package utils;

import dto.SearchDto;

import java.time.LocalDate;

// bodies for the car search; dates are relative to today, ISO format (yyyy-MM-dd)
public class SearchFactory {
    // city where CarFactory adds the cars
    private static final String CITY = "Rehovot";

    public static SearchDto positiveSearch(){
        return SearchDto.builder()
                .city(CITY)
                .startDate(LocalDate.now().plusDays(10).toString())
                .endDate(LocalDate.now().plusDays(20).toString())
                .build();
    }

    public static SearchDto blankCitySearch(){
        return SearchDto.builder()
                .city("")
                .startDate(LocalDate.now().plusDays(10).toString())
                .endDate(LocalDate.now().plusDays(20).toString())
                .build();
    }

    // no fields at all: sent as {}
    public static SearchDto emptySearch(){
        return SearchDto.builder().build();
    }

    // start and end on the same day: less than one day of rent
    public static SearchDto sameDaysSearch(){
        return SearchDto.builder()
                .city(CITY)
                .startDate(LocalDate.now().plusDays(10).toString())
                .endDate(LocalDate.now().plusDays(10).toString())
                .build();
    }

    // both dates before today (relative, so they stay in the past on every run)
    public static SearchDto datesInPastSearch(){
        return SearchDto.builder()
                .city(CITY)
                .startDate(LocalDate.now().minusDays(20).toString())
                .endDate(LocalDate.now().minusDays(10).toString())
                .build();
    }

    public static SearchDto endDateBeforeStartDateSearch(){
        return SearchDto.builder()
                .city(CITY)
                .startDate(LocalDate.now().plusDays(20).toString())
                .endDate(LocalDate.now().plusDays(10).toString())
                .build();
    }
}
