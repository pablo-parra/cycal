package com.tastik.cycal.data.rest;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RacesReaderImplTests {

    @Test
    void test() {
        String f = "false";
        assertThat(Boolean.valueOf(f)).isFalse();
        assertThat(true).isTrue();
    }

}
