package com.tastik.cycal.data.rest;

import com.tastik.cycal.core.domain.rankings.IndividualRanking;
import com.tastik.cycal.core.domain.rankings.Ranking;
import com.tastik.cycal.core.domain.rankings.TeamRanking;
import com.tastik.cycal.core.interactors.RankingReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Service
public class RankingReaderImpl implements RankingReader {

    @Value("${source.url:https://www.uci.org}")
    private String host;

    @Value("${source.ranking:/api/rankings/details}")
    private String ranking;

    RestTemplate restTemplate;

    public RankingReaderImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RankingReaderImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Ranking readRankingData() {
        try{
            final var individualRanking = getIndividualRanking();
            final var teamRanking = getTeamRanking();
            return new Ranking(
                    nonNull(individualRanking) ? individualRanking : IndividualRanking.empty(),
                    nonNull(teamRanking) ? teamRanking : TeamRanking.empty()
            );
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            return new Ranking(IndividualRanking.empty(), TeamRanking.empty());
        }
    }

    private IndividualRanking getIndividualRanking() {
        try{
            ResponseEntity<IndividualRanking> response = restTemplate.getForEntity(url(INDIVIDUAL), IndividualRanking.class);
            return response.getBody();
        }catch(Exception ex) {
            LOG.error("There was a problem getting the INDIVIDUAL RANKING DATA: {}", ex.getMessage());
            return IndividualRanking.empty();
        }
    }

    private TeamRanking getTeamRanking() {
        try{
            ResponseEntity<TeamRanking> response = restTemplate.getForEntity(url(TEAM), TeamRanking.class);
            return response.getBody();
        }catch(Exception ex) {
            LOG.error("There was a problem getting the TEAM RANKING DATA: {}", ex.getMessage());
            return TeamRanking.empty();
        }
    }

    private String url(String rankingType) {
        final var queryParams = String.format(params, DISCIPLINE, LocalDateTime.now().getYear(), CATEGORY, rankingType);
        return String.format("%s%s%s", host, ranking, queryParams);
    }

    private static final String DISCIPLINE = "ROA";
    private static final String CATEGORY = "E";
    private static final String INDIVIDUAL = "Individual";
    private static final String TEAM = "Team";
    private static String params = "?DisciplineCode=%s&SeasonYear=%s&Category=%s&rankingType=%s";
    private static final Logger LOG = LoggerFactory.getLogger(RankingReaderImpl.class);
}
