package com.tastik.cycal.core.domain;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;

public record Races(List<Race> races) {
    public static Races empty() {
        return new Races(emptyList());
    }
}
