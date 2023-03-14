package com.tastik.cycal.data.rest;

import com.tastik.cycal.core.domain.races.Race;
import com.tastik.cycal.core.domain.races.Races;
import com.tastik.cycal.core.interactors.RacesReader;
import com.tastik.cycal.data.rest.dtos.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Service
public class RacesReaderImpl implements RacesReader {

    @Value("${source.url:https://www.uci.org}")
    private String host;

    @Value("${source.upcoming:/api/calendar/upcoming}")
    private String upcoming;

    @Value("${source.past:/api/calendar/past}")
    private String past;

//    @Value("${race.discipline:ROA}")
//    private String discipline;
    @Value("${race.class:1.UWT,2.UWT}")
    private String raceClass;

    private final RestTemplate restTemplate;

    private final HtmlCrawler crawler;

    public RacesReaderImpl(@Autowired HtmlCrawler crawler) {
        this.restTemplate =  new RestTemplate();
        this.crawler = crawler;
    }

    @Override
    public Races readRacesData() {
        ResponseDTO pastRacesResponse = getPastRaces();
        ResponseDTO upcomingRacesResponse = getUpcomingRaces();

        return new Races(
                Optional.ofNullable(pastRacesResponse).map(this::extractYesterdayRoadRacesFrom).orElse(emptyList()),
                Optional.ofNullable(upcomingRacesResponse).map(this::extractTodayRoadRacesFrom).orElse(emptyList())
        );
    }

    private ResponseDTO getPastRaces() {
        try{
            return restTemplate.getForObject(pastRacesUrl(), ResponseDTO.class);
        } catch (Exception ex) {
            LOG.error("There was a problem getting the PAST RACES data: {}", ex.getMessage());
            return null;
        }
    }

    private ResponseDTO getUpcomingRaces() {
        try{
            return restTemplate.getForObject(upcomingRacesUrl(), ResponseDTO.class);
        } catch (Exception ex) {
            LOG.error("There was a problem getting the UPCOMING RACES data: {}", ex.getMessage());
            return null;
        }
    }

    private List<Race> extractYesterdayRoadRacesFrom(ResponseDTO response) {
        return response.extractYesterdayRoadRacesWith(crawler);
    }

    private List<Race> extractTodayRoadRacesFrom(ResponseDTO response) {
        return response.extractTodayRoadRacesWith(crawler);
    }

    private String pastRacesUrl() {
        return host+ past;
//        return """
//                %s?raceClass=%s
//                """.formatted(host+api, raceClass);
    }

    private String upcomingRacesUrl() {
        return host+ upcoming;
//        return """
//                %s?raceClass=%s
//                """.formatted(host+api, raceClass);
    }

    private static final Logger LOG = LoggerFactory.getLogger(RacesReaderImpl.class);
}
