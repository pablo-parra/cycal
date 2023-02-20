package com.tastik.cycal.task;

import com.tastik.cycal.core.domain.Races;
import com.tastik.cycal.core.interactors.UseCase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CycalScheduler {

    private UseCase<Races> sendTodayRaces;

    public CycalScheduler(@Qualifier("SendTodayRoadRaces")UseCase<Races> sendTodayRaces) {
        this.sendTodayRaces = sendTodayRaces;
    }

    @Scheduled(cron = "${cron.expression}")
    public void report() {
        this.sendTodayRaces.execute();
    }
}
