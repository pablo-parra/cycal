package com.tastik.cycal.core.domain.races;

import com.tastik.cycal.core.config.Gender;

import java.util.Objects;

public record Stage(String raceName, String raceType, String raceClass, String category) {
    public boolean isStageRace() {
        return Objects.nonNull(raceName) && raceName.toUpperCase().contains(STAGE);
    }

    public static final String STAGE = "STAGE";

    public boolean isFinalClassification(){
        return FINAL_CLASSIFICATION.equals(raceName);
    }

    public boolean isWomenRace(){
        return Objects.nonNull(category) && category.toUpperCase().contains(Gender.WOMEN.toString());
    }

    public boolean isMenRace(){
        return Objects.nonNull(category) && category.toUpperCase().contains(Gender.MEN.toString());
    }

    private static final String FINAL_CLASSIFICATION = "Final Classification";
}
