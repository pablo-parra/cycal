package com.tastik.cycal.data.rest;

import com.tastik.cycal.core.domain.Race;
import com.tastik.cycal.core.domain.Races;
import com.tastik.cycal.core.interactors.RacesReader;
import com.tastik.cycal.data.rest.dtos.DayDTO;
import com.tastik.cycal.data.rest.dtos.RaceDTO;
import com.tastik.cycal.data.rest.dtos.ResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Service
public class SourceReader implements RacesReader {

    @Value("${source.url:https://www.uci.org/api/calendar/upcoming}")
    private String url;

//    @Value("${race.discipline:ROA}")
//    private String discipline;
    @Value("${race.class:1.UWT,2.UWT}")
    private String raceClass;

    @Override
    public Races readRacesData() {

        RestTemplate restTemplate = new RestTemplate();

        ResponseDTO response = restTemplate.getForObject(url(), ResponseDTO.class);

        return nonNull(response)
                ? extractTodayRacesFrom(response)
                : Races.empty();
    }

    // TODO move this filters logic to core use-case, rest client agnostic of app business logic
    private Races extractTodayRacesFrom(ResponseDTO response) {
//        final var month = response.items().stream().filter(MonthDTO::isCurrentMonth).findFirst().orElseThrow();
        final var month = response.items().stream().filter(monthItem -> monthItem.month() == 2 && monthItem.year() == 2023).findFirst().orElseThrow();
        final var today = month.items().stream().filter(DayDTO::isToday).findFirst();
        List<Race> todayRaces = new ArrayList<>();
        if(today.isPresent()) {
            todayRaces = today.get().items().stream().map(RaceDTO::toDomain).toList();
        }
        return new Races(todayRaces);
    }

    private String url() {
        return """
                %s?raceClass=%s
                """.formatted(url, raceClass);
    }
}
