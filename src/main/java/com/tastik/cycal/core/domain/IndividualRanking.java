package com.tastik.cycal.core.domain;

public record IndividualRanking(
        Discipline mens,
        Discipline womens) {

    public static IndividualRanking empty() {
        return new IndividualRanking(Discipline.empty(), Discipline.empty());
    }
}