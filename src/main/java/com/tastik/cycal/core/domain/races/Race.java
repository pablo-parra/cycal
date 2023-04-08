package com.tastik.cycal.core.domain.races;

import com.tastik.cycal.core.config.Gender;
import com.tastik.cycal.core.config.RaceCategory;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public record Race(
        String name,
        String colourCode,
        String venue,
        String country,
        String dates,
        Details details,
        CompetitionProperties properties) {

    public static final String WOMEN = "WOMEN";
    public static final String MEN = "MEN";

    public boolean isOneDayRace() {
        return nonNull(properties)
                && nonNull(properties.schedule())
                && nonNull(properties.schedule().items())
                && properties.schedule().items().size() == ONE;
    }

    public boolean isMultiDayRace() {
        return nonNull(properties)
                && nonNull(properties.schedule())
                && nonNull(properties.schedule().items())
                && properties.schedule().items().size() > ONE;
    }

    public boolean isNationalChampionship() {
        return name.toUpperCase().contains(NATIONAL)
                && (name.toUpperCase().contains(CHAMPIONSHIP) || name.toUpperCase().contains(CHAMPIONSHIPS));
    }

    public boolean isWomenRace() {
        return nonNull(properties)
                && nonNull(properties.schedule())
                && nonNull(properties.schedule().items())
                && properties.schedule().items().stream()
                .flatMap(item -> item.races().stream())
                .allMatch(race -> Arrays.stream(race.category().toUpperCase().split(" ")).toList().contains(WOMEN));
    }

    public boolean isMenRace() {
        return nonNull(properties)
                && nonNull(properties.schedule())
                && nonNull(properties.schedule().items())
                && properties.schedule().items().stream()
                .flatMap(item -> item.races().stream())
                .allMatch(race -> Arrays.stream(race.category().toUpperCase().split(" ")).toList().contains(MEN));
    }

    public Gender gender() {
        return isWomenRace() ? Gender.WOMEN
                : isMenRace() ? Gender.MEN
                : Gender.MIXED;
    }

    public boolean hasUrl() {
        return nonNull(properties) && nonNull(properties.competitionDetails())
                && nonNull(properties.competitionDetails().website())
                && nonNull(properties.competitionDetails().website().url);
    }

    public String url() {
        return Optional.ofNullable(properties.competitionDetails().website().url).orElse(null);
    }

    public Optional<RaceDay> todayStage() {
        if (isNull(properties)
                || isNull(properties.schedule())
                || isNull(properties.schedule().items())) {
            return Optional.empty();
        }
        return properties.schedule().items().stream().filter(item -> item.date().equals(LocalDate.now())).findFirst();
    }

    public Optional<RaceDay> yesterdayStage() {
        if (isNull(properties)
                || isNull(properties.schedule())
                || isNull(properties.schedule().items())) {
            return Optional.empty();
        }
        return properties.schedule().items().stream().filter(item -> item.date().equals(LocalDate.now().minusDays(1))).findFirst();
    }

    public RaceCategory category() {
        if (isNull(properties)
                || isNull(properties.competitionDetails())
                || isNull(properties.competitionDetails().competitionClass())) {
            return RaceCategory.UNKNOWN;
        }

        return RaceCategory.by(properties.competitionDetails().competitionClass());
    }

    private static final int ONE = 1;
    private static final String NATIONAL = "NATIONAL";
    public static final String CHAMPIONSHIP = "CHAMPIONSHIP";
    public static final String CHAMPIONSHIPS = "CHAMPIONSHIPS";
}
