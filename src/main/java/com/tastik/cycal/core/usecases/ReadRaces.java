package com.tastik.cycal.core.usecases;

import com.tastik.cycal.core.domain.races.Races;
import com.tastik.cycal.core.interactors.RacesReader;
import com.tastik.cycal.core.interactors.UseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ReadRaces")
public class ReadRaces implements UseCase<Races> {

    private RacesReader reader;

    public ReadRaces(@Autowired RacesReader reader) {
        this.reader = reader;
    }

    @Override
    public Races execute() {
        return reader.readRacesData();
    }
}
