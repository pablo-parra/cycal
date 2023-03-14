package com.tastik.cycal.core.domain.rankings;

import java.time.LocalDateTime;

public record IndividualRankingPosition(
        String individualUCIID,
        String individualName,
        String firstName,
        String lastName,
        String teamName,
        String countryCode,
        String genderCode,
        LocalDateTime individualBirthDate,
        int rankingId,
        String rankingName,
        LocalDateTime rankingDate,
        Integer rank,
        double totalPoints,
        int sanctionPoints,
        String disciplineCode,
        String season,
        Integer seasonYear,
        String categoryCode) {

}
