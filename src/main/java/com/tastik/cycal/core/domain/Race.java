package com.tastik.cycal.core.domain;

import java.util.Optional;

import static java.util.Objects.nonNull;

public record Race(
        String name,
        String colourCode,
        String venue,
        String country,
        String dates,
        Details details,
        CompetitionProperties properties) {



    public boolean isOneDayRace() {
        return nonNull(properties)
                && nonNull(properties.schedule())
                && nonNull(properties.schedule().items())
                && properties.schedule().items().size() == ONE_DAY;
    }

    public boolean isMultiDayRace() {
        return nonNull(properties)
                && nonNull(properties.schedule())
                && nonNull(properties.schedule().items())
                && properties.schedule().items().size() > ONE_DAY;
    }

    public boolean isNationalChampionship() {
        return name.toUpperCase().contains(NATIONAL)
                && (name.toUpperCase().contains(CHAMPIONSHIP) || name.toUpperCase().contains(CHAMPIONSHIPS));
    }

    public boolean hasUrl() {
        return nonNull(properties) && nonNull(properties.competitionDetails())
                && nonNull(properties.competitionDetails().website())
                && nonNull(properties.competitionDetails().website().url);
    }

    public String url() {
        return Optional.ofNullable(properties.competitionDetails().website().url).orElse(null);
    }

    private static final int ONE_DAY = 1;
    private static final String NATIONAL = "NATIONAL";
    public static final String CHAMPIONSHIP = "CHAMPIONSHIP";
    public static final String CHAMPIONSHIPS = "CHAMPIONSHIPS";
}
