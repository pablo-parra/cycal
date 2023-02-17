package com.tastik.cycal.core.usecases;

import com.tastik.cycal.core.domain.Races;
import com.tastik.cycal.core.interactors.RacesSender;
import com.tastik.cycal.core.interactors.UseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier ("SendTodayRoadRaces")
public class SendTodayRoadRaces implements UseCase<Races> {

//    @Autowired
//    @Qualifier("ReadDataFromSources")
    private final UseCase<Races> readData;

//    @Autowired
    private final RacesSender sender;

    public SendTodayRoadRaces(
            @Qualifier("ReadDataFromSources")UseCase<Races> readData,
            @Autowired RacesSender sender) {
        this.readData = readData;
        this.sender = sender;
    }

    public Races execute() {
        Races races = readData.execute();
        sender.send(races);
        return races;
    }
}
