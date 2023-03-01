package com.tastik.cycal.core.domain;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;

public record Discipline(
        String disciplinePageUrl,
        LocalDateTime lastUpdated,
        List<IndividualRankingPosition> individualResults) {

    public static Discipline empty() {
        return new Discipline(null, null, emptyList());
    }
}
