package com.tastik.cycal.core.domain.races;

import com.tastik.cycal.core.config.Gender;

import static java.util.Objects.nonNull;

public record Stage(String raceName, String raceType, String raceClass, String category) {
    public boolean isPrologue() {
        return nonNull(raceName)
                && raceName.toUpperCase().contains(PROLOGUE);
    }
    public boolean isStageRace() {
        return nonNull(raceName)
                && raceName.toUpperCase().contains(STAGE);
    }

    public static final String STAGE = "STAGE";
    public static final String PROLOGUE = "PROLOGUE";

    public boolean isFinalClassification(){
        return FINAL_CLASSIFICATION.equals(raceName);
    }

    public boolean isWomenRace(){
        return nonNull(category) && category.toUpperCase().contains(Gender.WOMEN.toString());
    }

    public boolean isMenRace(){
        return nonNull(category) && category.toUpperCase().contains(Gender.MEN.toString());
    }

    private static final String FINAL_CLASSIFICATION = "Final Classification";
}
