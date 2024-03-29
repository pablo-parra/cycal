package com.tastik.cycal.core.domain.races;

public record CompetitionProperties (
        String competitionName,
        CompetitionDetails competitionDetails,
        Schedule schedule,
        Results results) {
}
