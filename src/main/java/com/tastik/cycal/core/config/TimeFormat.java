package com.tastik.cycal.core.config;

public enum TimeFormat {
    _24H("HH:mm:ss", ".*:.*:.*"),
    WITH_TIME_MARKS("HH'h'mm''ss''", ".*h.*'.*''"),
    MIXED("HH'h'mm:ss", ".*h.*:.*");

    private final String format;
    private final String regEx;

    TimeFormat(String format, String regEx) {
        this.format = format;
        this.regEx = regEx;
    }

    public String format() {
        return this.format;
    }

    public String regEx() {
        return this.regEx;
    }
}
