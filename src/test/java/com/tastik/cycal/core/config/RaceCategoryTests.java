package com.tastik.cycal.core.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RaceCategoryTests {
    @Test
    void race_category_by_term_returns_expected_category() {
        var uwt = RaceCategory.by(".UWT");
        assertThat(uwt).isEqualTo(RaceCategory.UWT);
        uwt = RaceCategory.by(".WWT");
        assertThat(uwt).isEqualTo(RaceCategory.UWT);
    }

    @Test
    void unknown_race_category_returns_unknown_category() {
        final var unknown = RaceCategory.by("some-unknown-term");
        assertThat(unknown).isEqualTo(RaceCategory.UNKNOWN);
    }
}
