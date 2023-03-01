package com.tastik.cycal.core.usecases;

import com.tastik.cycal.core.domain.Ranking;
import com.tastik.cycal.core.interactors.RankingReader;
import com.tastik.cycal.core.interactors.UseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ReadRanking")
public class ReadRanking implements UseCase<Ranking> {

    private final RankingReader rankingReader;

    public ReadRanking(@Autowired RankingReader rankingReader) {
        this.rankingReader = rankingReader;
    }

    @Override
    public Ranking execute() {
        return this.rankingReader.readRankingData();
    }
}
