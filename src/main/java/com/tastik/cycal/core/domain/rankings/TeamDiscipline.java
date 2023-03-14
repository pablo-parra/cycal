package com.tastik.cycal.core.domain.rankings;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;

public record TeamDiscipline (
        String disciplinePageUrl,
        LocalDateTime lastUpdated,
        List<TeamRankingPosition> teamResults) {

    public static TeamDiscipline empty() {
        return new TeamDiscipline(null, null, emptyList());
    }
}
