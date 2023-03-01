package com.tastik.cycal.core.domain;

import java.util.List;

import static java.util.Collections.emptyList;

public record Races(List<Race> yesterdayRaces, List<Race> todayRaces) {
    public static Races empty() {
        return new Races(emptyList(), emptyList());
    }
}
