package com.tastik.cycal.core.domain;

import java.util.List;

public record Results(String discipline, String disciplineCode, List<StageResults> accordion) {
}
