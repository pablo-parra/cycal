package com.tastik.cycal.core.domain.races;

import java.util.List;

public record Results(String discipline, String disciplineCode, List<StageResults> accordion) {
}
