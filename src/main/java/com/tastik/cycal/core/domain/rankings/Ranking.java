package com.tastik.cycal.core.domain.rankings;

public record Ranking (IndividualRanking individualRanking, TeamRanking teamRanking){
    public static Ranking empty(){
        return new Ranking(null, null);
    }

    public boolean isEmpty() {
        return this.equals(new Ranking(null, null));
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }
}
