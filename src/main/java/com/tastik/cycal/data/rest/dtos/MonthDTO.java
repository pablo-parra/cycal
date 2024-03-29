package com.tastik.cycal.data.rest.dtos;

import java.util.List;

public record MonthDTO(
        int month,
        int year,
        String monthName,
        boolean isCurrentMonth,
        List<DayDTO> items
) {
    public boolean matchesMonth(int month) {
        return this.month == month;
    }
}
