package com.tastik.cycal.core.domain.results;

public record PodiumPosition(int position, String firstName, String lastName, String time) {
    public static PodiumPosition errorPosition() {
        return new PodiumPosition(0, ERROR, ERROR, ERROR);
    }
    private static final String ERROR = "---";
}
