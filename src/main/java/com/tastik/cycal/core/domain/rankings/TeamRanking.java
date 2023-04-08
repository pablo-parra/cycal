package com.tastik.cycal.core.domain.rankings;

public record TeamRanking (
        TeamDiscipline mens,
        TeamDiscipline womens){

    public static TeamRanking empty() {
        return new TeamRanking(TeamDiscipline.empty(), TeamDiscipline.empty());
    }

    public boolean isEmpty() {
        return this.equals(empty());
    }

    public boolean isNotEmpty() {
        return !this.equals(empty());
    }
}
