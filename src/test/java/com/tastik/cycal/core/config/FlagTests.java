package com.tastik.cycal.core.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FlagTests {
    @Test
    void get_flag_returns_expected_flag() {
        final var spainFlag = Flag.get("ESP");
        assertThat(spainFlag).isEqualTo("🇪🇸");
    }
}
