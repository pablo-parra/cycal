package com.tastik.cycal.core.domain.rankings;

public record IndividualRanking(
        Discipline mens,
        Discipline womens) {

    public static IndividualRanking empty() {
        return new IndividualRanking(Discipline.empty(), Discipline.empty());
    }

    public boolean isEmpty() {
        return this.equals(empty());
    }

    public boolean isNotEmpty() {
        return !this.equals(empty());
    }
}