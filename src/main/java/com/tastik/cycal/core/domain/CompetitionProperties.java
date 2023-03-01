package com.tastik.cycal.core.domain;

public record CompetitionProperties (
        String competitionName,
        CompetitionDetails competitionDetails,
        Schedule schedule,
        Results results) {
}
