package com.tastik.cycal.data.rest.dtos;

import java.time.LocalDate;
import java.util.List;

public record DayDTO(
        int day,
        int month,
        int year,
        String competitionDate,
        List<RaceDTO> items
) {
    public boolean isToday(){
        final var today = LocalDate.now();
//        return today.getDayOfMonth() == day && today.getMonthValue() == month && today.getYear() == year;
        return day == 25 && month == 2 && year == 2023;
    }
}
