package com.tastik.cycal.core.domain;

public record TeamRankingPosition(
        String teamName,
        String countryCode,
        int rankingId,
        String rankingName,
        String rankingDate,
        Integer rank,
        double totalPoints,
        String disciplineCode,
        String season,
        Integer seasonYear,
        String teamType,
        String categoryCode
) {
}
