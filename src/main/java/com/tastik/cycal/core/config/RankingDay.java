package com.tastik.cycal.core.config;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;

public enum RankingDay {
    MONDAY(DayOfWeek.MONDAY),
    FRIDAY(DayOfWeek.FRIDAY);

    private final DayOfWeek rankingDay;

    RankingDay (DayOfWeek rankingDay){
        this.rankingDay = rankingDay;
    }

    public static boolean isTodayRankingDay() {
        return Arrays.stream(values()).anyMatch(week -> week.rankingDay.equals(LocalDate.now().getDayOfWeek()));
    }
}
