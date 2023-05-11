package com.tastik.cycal.core.usecases;

import com.tastik.cycal.core.config.RankingDay;
import com.tastik.cycal.core.domain.races.Races;
import com.tastik.cycal.core.domain.rankings.Ranking;
import com.tastik.cycal.core.domain.report.Report;
import com.tastik.cycal.core.interactors.UseCase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ReportContent")
public class ReportContent implements UseCase<Report> {

    private final UseCase<Races> readRaces;
    private final UseCase<Ranking> readRanking;

    public ReportContent(
            @Qualifier("ReadRaces") UseCase<Races> readRaces,
            @Qualifier("ReadRanking") UseCase<Ranking> readRanking) {
        this.readRaces = readRaces;
        this.readRanking = readRanking;
    }

    @Override
    public Report execute() {
        return new Report(
                readRaces.execute(),
                RankingDay.isTodayRankingDay() ? readRanking.execute() : Ranking.empty()
        );
    }
}
