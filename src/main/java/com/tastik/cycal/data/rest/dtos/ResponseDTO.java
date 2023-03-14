package com.tastik.cycal.data.rest.dtos;

import com.tastik.cycal.core.domain.races.Race;
import com.tastik.cycal.data.rest.HtmlCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;

public record ResponseDTO(
        List<MonthDTO> items
) {
    public List<Race> extractYesterdayRoadRacesWith(HtmlCrawler crawler) {
        try{
            final var yesterday = LocalDate.now().minusDays(1);
            final var yesterdayMonthRaces = items.stream().filter(month -> month.matchesMonth(yesterday.getMonthValue())).findFirst().orElseThrow();
            final var yesterdayRaces = yesterdayMonthRaces.items().stream().filter(day -> day.matchesDay(yesterday.getDayOfMonth())).findFirst();
            return yesterdayRaces.isPresent() ? extractRoadRacesFrom(yesterdayRaces.get(), crawler) : emptyList();
        } catch (Exception ex) {
            LOG.error("There was a problem extracting YESTERDAY ROAD RACES: {}", ex.getMessage());
            return emptyList();
        }
    }

    public List<Race> extractTodayRoadRacesWith(HtmlCrawler crawler) {
        try{
            final var thisMonthRaces = items.stream().filter(MonthDTO::isCurrentMonth).findFirst().orElseThrow();
            final var todayRaces = thisMonthRaces.items().stream().filter(DayDTO::isToday).findFirst();
            return todayRaces.isPresent() ? extractRoadRacesFrom(todayRaces.get(), crawler) : emptyList();
        } catch (Exception ex) {
            LOG.error("There was a problem extracting TODAY ROAD RACES: {}", ex.getMessage());
            return emptyList();
        }
    }

    private List<Race> extractRoadRacesFrom(DayDTO races, HtmlCrawler crawler) {
        return races.items().stream().filter(RaceDTO::isRoadRace).map(race -> race.toDomain(crawler.getCompetitionPropertiesFor(race))).toList();
    }

    private static final Logger LOG = LoggerFactory.getLogger(ResponseDTO.class);
}
