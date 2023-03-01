package com.tastik.cycal.core.domain;

public record TeamRanking (
        TeamDiscipline mens,
        TeamDiscipline womens){

    public static TeamRanking empty() {
        return new TeamRanking(TeamDiscipline.empty(), TeamDiscipline.empty());
    }
}
