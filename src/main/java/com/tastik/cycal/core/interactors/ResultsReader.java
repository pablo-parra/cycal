package com.tastik.cycal.core.interactors;

import com.tastik.cycal.core.domain.results.RaceResults;

import java.util.Optional;

public interface ResultsReader {
    Optional<RaceResults> readRaceResults(String raceCode);
}
