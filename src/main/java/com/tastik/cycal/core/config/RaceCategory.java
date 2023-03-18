package com.tastik.cycal.core.config;

import java.util.List;

import static java.util.Collections.emptyList;

public enum RaceCategory {
    UWT(List.of(".UWT", ".WWT")),
    PRO(List.of(".Pro")),
    CATEGORY_1(List.of(".1")),
    CATEGORY_2(List.of(".2")),
    UNKNOWN(emptyList());

    private final List<String> ids;

    RaceCategory(List<String> ids) {
        this.ids = ids;
    }

    public static RaceCategory by(String term) {
        if(UWT.ids.stream().anyMatch(term::contains)) return RaceCategory.UWT;
        if(PRO.ids.stream().anyMatch(term::contains)) return RaceCategory.PRO;
        if(CATEGORY_1.ids.stream().anyMatch(term::contains)) return RaceCategory.CATEGORY_1;
        if(CATEGORY_2.ids.stream().anyMatch(term::contains)) return RaceCategory.CATEGORY_2;

        return RaceCategory.UNKNOWN;
    }
}
