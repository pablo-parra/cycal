package com.tastik.cycal.core.domain.races;

import com.tastik.cycal.core.config.RaceCategory;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record Races(List<Race> yesterdayRaces, List<Race> todayRaces) {
    public Map<RaceCategory, List<Race>> yesterdayRacesByCategory() {
        return mapOf(yesterdayRaces);
    }

    public Map<RaceCategory, List<Race>> todayRacesByCategory() {
        return mapOf(todayRaces);
    }

    public Map<RaceCategory, List<Race>> mapOf(List<Race> races) {
        final var raceCategories = RaceCategory.values();
        final var racesMap = new LinkedHashMap<RaceCategory, List<Race>>();
        Arrays.stream(raceCategories).forEach(raceCategory -> {
            final var racesByCategory = races.stream().filter(race -> raceCategory.equals(race.category())).collect(Collectors.toList());
            racesMap.put(raceCategory, racesByCategory);
        });
        return racesMap;
    }
}
