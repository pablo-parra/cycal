package com.tastik.cycal.core.domain.races;

import java.time.LocalDate;
import java.util.List;

public record RaceDay(LocalDate date, List<Stage> races) {
}
