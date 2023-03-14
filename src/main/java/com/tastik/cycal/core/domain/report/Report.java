package com.tastik.cycal.core.domain.report;

import com.tastik.cycal.core.domain.rankings.Ranking;
import com.tastik.cycal.core.domain.races.Races;

public record Report (Races races, Ranking ranking){
}
