package com.tastik.cycal.data.rest.dtos;

import com.tastik.cycal.core.domain.Race;

public record RaceDTO(
        String name,
        String colourCode,
        String venue,
        String country,
        String dates,
        DetailsDTO detailsLink,
        boolean isUciEvent
) {
    public Race toDomain() {
        return new Race(
                name,
                colourCode,
                venue,
                country,
                dates,
                detailsLink.toDomain()
        );
    }

    public boolean isRoadRace() {
        return ROAD_RACE.equals(colourCode);
    }

    private static final String ROAD_RACE = "road";
}
