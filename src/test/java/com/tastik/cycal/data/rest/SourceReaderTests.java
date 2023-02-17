package com.tastik.cycal.data.rest;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SourceReaderTests {

    @Test
    void test() {
        String f = "false";
        assertThat(Boolean.valueOf(f)).isFalse();
        assertThat(true).isTrue();
    }

}
