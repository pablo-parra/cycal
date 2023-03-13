package com.tastik.cycal.data.rest;

import com.tastik.cycal.core.domain.results.RaceResults;
import com.tastik.cycal.core.interactors.ResultsReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;

@Service
public class ResultsReaderImpl implements ResultsReader {

    @Value("${source.url:https://www.uci.org}")
    private String host;

    @Value("${source.results:/api/calendar/results/%s?discipline=ROA&raceType=A&raceName=Stage+Classification}")
    private String resultSource;

    RestTemplate restTemplate;

    public ResultsReaderImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Optional<RaceResults> readRaceResults(String raceCode) {
        try{
            ResponseEntity<RaceResults> response = restTemplate.getForEntity(url(raceCode), RaceResults.class);
            return Objects.nonNull(response.getBody()) ? Optional.of(response.getBody()) : Optional.empty();
        } catch (Exception ex){
            LOG.error("There was a problem getting the RESULTS for the raceCode {}: {}", raceCode, ex.getMessage());
            return Optional.empty();
        }
    }

    private String url(String raceCode) {
        return String.format(host+resultSource, raceCode);
    }

    private static final Logger LOG = LoggerFactory.getLogger(ResultsReaderImpl.class);
}
