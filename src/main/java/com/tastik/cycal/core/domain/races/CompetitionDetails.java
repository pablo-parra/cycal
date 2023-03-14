package com.tastik.cycal.core.domain.races;

public record CompetitionDetails(
        String name,
        String dates,
        String venue,
        String country,
        String competitionClass,
        Website website,
        Email email) {
}
