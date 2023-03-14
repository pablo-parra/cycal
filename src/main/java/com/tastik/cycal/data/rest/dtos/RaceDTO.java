package com.tastik.cycal.data.rest.dtos;

import com.tastik.cycal.core.domain.races.CompetitionProperties;
import com.tastik.cycal.core.domain.races.Race;

public record RaceDTO(
        String name,
        String colourCode,
        String venue,
        String country,
        String dates,
        DetailsDTO detailsLink,
        boolean isUciEvent
) {
    public Race toDomain(CompetitionProperties competitionProperties) {
        return new Race(
                name,
                colourCode,
                venue,
                country,
                dates,
                detailsLink.toDomain(),
                competitionProperties
        );
    }

    public boolean isRoadRace() {
        return ROAD_RACE.equals(colourCode);
    }

    private static final String ROAD_RACE = "road";
}
