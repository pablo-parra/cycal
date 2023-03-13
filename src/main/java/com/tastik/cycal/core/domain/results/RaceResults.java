package com.tastik.cycal.core.domain.results;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public record RaceResults(List<RaceResult> results, Podium podium) {
    public Optional<Rider> first() {
        return Optional.ofNullable(podium.podium().get(0));
    }

    public Optional<Rider> second() {
        return Optional.ofNullable(podium.podium().get(1));
    }

    public Optional<Rider> third() {
        return Optional.ofNullable(podium.podium().get(2));
    }

    public PodiumPositions calculatePodium() {
        final var first = first();
        final var second = second();
        final var third = third();

        if (first.isEmpty() || second.isEmpty() || third.isEmpty()) {
            return new PodiumPositions(emptyList());
        }

        return new PodiumPositions(
                List.of(
                        first.map(rider -> new PodiumPosition(1, rider.firstname(), rider.lastname(), rider.time())).orElseGet(PodiumPosition::errorPosition),
                        second.map(rider -> new PodiumPosition(2, rider.firstname(), rider.lastname(), difference(first.get().time(), rider.time()))).orElseGet(PodiumPosition::errorPosition),
                        third.map(rider -> new PodiumPosition(3, rider.firstname(), rider.lastname(), difference(first.get().time(), rider.time()))).orElseGet(PodiumPosition::errorPosition)
                ));
    }

    private String difference(String timeA, String timeB) {
        final var a = timeA.split(":");
        final var b = timeB.split(":");
        if (a[0].length() == 1) timeA = "0" + timeA;
        if (b[0].length() == 1) timeB = "0" + timeB;
        try{
            final var aTime = LocalTime.parse(timeA);
            final var bTime = LocalTime.parse(timeB);

            final var hoursDifference = aTime.until(bTime, ChronoUnit.HOURS) < 10 ? "0"+ aTime.until(bTime, ChronoUnit.HOURS) : Long.toString(aTime.until(bTime, ChronoUnit.HOURS));
            final var minutesDifference = aTime.until(bTime, ChronoUnit.MINUTES) < 10 ? "0"+ aTime.until(bTime, ChronoUnit.MINUTES) : Long.toString(aTime.until(bTime, ChronoUnit.MINUTES));
            final var secondsDifference = aTime.until(bTime, ChronoUnit.SECONDS) < 10 ? "0"+ aTime.until(bTime, ChronoUnit.SECONDS) : Long.toString(aTime.until(bTime, ChronoUnit.SECONDS));

            return String.format("%s:%s:%s", hoursDifference, minutesDifference, secondsDifference);
        }catch(Exception ex){
            LOG.error("There was a problem getting difference between {} and {}. Not able to do the parse in expected HH:MM:SS format", timeA, timeB);
            return "---";
        }
    }


    private static final Logger LOG = LoggerFactory.getLogger(RaceResults.class);
}
