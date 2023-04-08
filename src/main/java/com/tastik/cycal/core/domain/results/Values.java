package com.tastik.cycal.core.domain.results;

import com.tastik.cycal.core.config.TimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static java.util.Objects.isNull;

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
    public String startingWithAPlus() {
        return result.startsWith("+") ? result.replace("+", PLUS) : PLUS.concat(result);
    }

    public String result() {
        return result.trim();
    }

    public boolean hasDifferenceAlreadyCalculatedWith(String time) {
        return Arrays.stream(TimeFormat.values())
                .map(TimeFormat::regEx).noneMatch(result::matches)
                || isLessThan(time);
//        return !result.matches(".*:.*:.*")
//                && !result.matches(".*h.*'.*''")
//                && !result.matches(".*h.*:.*");
    }

    public String differenceWith(String time) {
        try {
//            final var simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//            final var date1 = simpleDateFormat.parse(time);
//            final var date2 = simpleDateFormat.parse(result);
            final var date1 = getDateFrom(time);
            final var date2 = getDateFrom(result);

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

    public boolean isLessThan(String time) {
        try {
            final var date1 = getDateFrom(time);
            final var date2 = getDateFrom(result);

            return date2.before(date1);
        } catch (Exception ex) {
            LOG.error("There was a problem evaluating if {} is less than {}: {}", time, result, ex.getMessage());
            return false;
        }
    }

    private static Date getDateFrom(String time) {
        final var timeFormats = Arrays.stream(TimeFormat.values()).map(TimeFormat::format).toList();
        var date = new Date();
        for(String format : timeFormats){
            try {
                final var simpleDateFormat = new SimpleDateFormat(format);
                date = simpleDateFormat.parse(time);
                return date;
            } catch (Exception ex) {
                LOG.info("Unable to parse time {} to format {}", time, format);
            }
        }
        return date;
    }

    private static Object withTwoDigits(long number) {
        return number < 10 ? ZERO.concat(String.valueOf(number)) : number;
    }

    public String initial() {
        if(isNull(firstname)) return "";
        return firstname.substring(0,1).toUpperCase().concat(".");
    }

    private static final String PLUS = "\\%2B";
    private static final String ZERO = "0";
    public static final String DEFAULT = "---";
    private static final Logger LOG = LoggerFactory.getLogger(Values.class);
}
