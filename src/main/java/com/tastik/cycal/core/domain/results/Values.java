package com.tastik.cycal.core.domain.results;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

public record Values(
        String team,
        String result,
        String points,
        String rank,
        String firstname,
        String lastname,
        int age,
        String nationality
) {
    public String startingWithAPlus(){
        return result.startsWith("+") ? result.replace("+", PLUS) : PLUS.concat(result);
    }

    public String result(){
        return result.trim();
    }

    public boolean hasDifferenceAlreadyCalculated() {
        return !result.matches(".*:.*:.*");
    }

    public String differenceWith(String time){
        try {
            final var simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            final var date1 = simpleDateFormat.parse(time);
            final var date2 = simpleDateFormat.parse(result);

            long differenceInMilliSeconds
                    = Math.abs(date2.getTime() - date1.getTime());

            long differenceInHours
                    = (differenceInMilliSeconds / (60 * 60 * 1000))
                    % 24;

            long differenceInMinutes
                    = (differenceInMilliSeconds / (60 * 1000)) % 60;

            long differenceInSeconds
                    = (differenceInMilliSeconds / 1000) % 60;

            if (differenceInHours > 0) {
                return String.format("%s%s:%s:%s",
                        PLUS,
                        differenceInHours,
                        withTwoDigits(differenceInMinutes),
                        withTwoDigits(differenceInSeconds)
                );
            }
            return String.format("%s%s:%s",
                    PLUS,
                    withTwoDigits(differenceInMinutes),
                    withTwoDigits(differenceInSeconds)
            );

        } catch (Exception ex) {
            LOG.error("There was a problem getting difference between {} and {}: {}", time, result, ex.getMessage());
            return DEFAULT;
        }
    }

    private static Object withTwoDigits(long number) {
        return number < 10 ? ZERO.concat(String.valueOf(number)) : number;
    }

    private static final String PLUS = "\\%2B";
    private static final String ZERO = "0";
    public static final String DEFAULT = "---";
    private static final Logger LOG = LoggerFactory.getLogger(Values.class);
}
