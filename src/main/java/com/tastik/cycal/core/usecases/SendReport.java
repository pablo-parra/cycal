package com.tastik.cycal.core.usecases;

import com.tastik.cycal.core.config.RankingDay;
import com.tastik.cycal.core.domain.races.Races;
import com.tastik.cycal.core.domain.rankings.Ranking;
import com.tastik.cycal.core.domain.report.Report;
import com.tastik.cycal.core.interactors.ReportSender;
import com.tastik.cycal.core.interactors.UseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier ("SendReport")
public class SendReport implements UseCase<Report> {

    private final UseCase<Races> readRaces;

    private final UseCase<Ranking> readRanking;

    private final ReportSender sender;

    public SendReport(
            @Qualifier("ReadRaces")UseCase<Races> readRaces,
            @Qualifier("ReadRanking")UseCase<Ranking> readRanking,
            @Autowired ReportSender sender) {
        this.readRaces = readRaces;
        this.readRanking = readRanking;
        this.sender = sender;
    }

    public Report execute() {
        final var races = readRaces.execute();
        final var ranking = RankingDay.isTodayRankingDay() ? readRanking.execute() : Ranking.empty();
        final var report = new Report(races, ranking);
        sender.send(report);
        return report;
    }
}
