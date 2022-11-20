package com.tastik.cycal.core.domain;

public record Race(
        String name,
        String colourCode,
        String venue,
        String country,
        String dates,
        Details details) {
}
