package com.tastik.cycal.core.usecases;

import com.tastik.cycal.core.domain.Races;
import com.tastik.cycal.core.interactors.RacesReader;
import com.tastik.cycal.core.interactors.UseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReadDataFromSources implements UseCase<Races> {

    @Autowired
    private RacesReader reader;

    @Override
    public Races execute() {
        return reader.readRacesData();
    }
}
