package com.tastik.cycal.core.usecases;

import com.tastik.cycal.core.domain.Races;
import com.tastik.cycal.core.interactors.RacesReader;
import com.tastik.cycal.core.interactors.UseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ReadDataFromSources")
public class ReadDataFromSources implements UseCase<Races> {

    private RacesReader reader;

    public ReadDataFromSources(@Autowired RacesReader reader) {
        this.reader = reader;
    }

    @Override
    public Races execute() {
        return reader.readRacesData();
    }
}
