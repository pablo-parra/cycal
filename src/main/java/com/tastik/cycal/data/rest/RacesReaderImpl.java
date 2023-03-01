package com.tastik.cycal.data.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tastik.cycal.core.domain.CompetitionProperties;
import com.tastik.cycal.core.domain.LocalDateDeserializer;
import com.tastik.cycal.core.domain.Race;
import com.tastik.cycal.core.domain.Races;
import com.tastik.cycal.core.interactors.RacesReader;
import com.tastik.cycal.data.rest.dtos.DayDTO;
import com.tastik.cycal.data.rest.dtos.MonthDTO;
import com.tastik.cycal.data.rest.dtos.RaceDTO;
import com.tastik.cycal.data.rest.dtos.ResponseDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
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
    private final Gson gson;

    public RacesReaderImpl() {
        this.restTemplate =  new RestTemplate();
        this.gson = createGson();
    }

    private Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer());
        return gsonBuilder.setPrettyPrinting().create();
    }

    @Override
    public Races readRacesData() {
        ResponseDTO pastRacesResponse = getPastRaces();
        ResponseDTO upcomingRacesResponse = getUpcomingRaces();

        return new Races(
                Optional.ofNullable(pastRacesResponse).map(this::extractYesterdayRoadRacesFrom).orElse(emptyList()),
                Optional.ofNullable(upcomingRacesResponse).map(this::extractTodayRoadRacesFrom).orElse(emptyList())
//                nonNull(pastRacesResponse) ? extractYesterdayRoadRacesFrom(pastRacesResponse) : emptyList(),
//                nonNull(upcomingRacesResponse) ? extractTodayRoadRacesFrom(upcomingRacesResponse) : emptyList()
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

    // TODO move this filters logic to core use-case? rest client agnostic of app business logic?
    private List<Race> extractYesterdayRoadRacesFrom(ResponseDTO response) {
        try{
            final var yesterday = LocalDate.now().minusDays(1);
            final var yesterdayMonthRaces = response.items().stream().filter(month -> month.matchesMonth(yesterday.getMonthValue())).findFirst().orElseThrow();
            final var yesterdayRaces = yesterdayMonthRaces.items().stream().filter(day -> day.matchesDay(yesterday.getDayOfMonth())).findFirst();
            return yesterdayRaces.isPresent() ? extractRoadRacesFrom(yesterdayRaces) : emptyList();
        } catch (Exception ex) {
            LOG.error("There was a problem extracting YESTERDAY ROAD RACES: {}", ex.getMessage());
            return emptyList();
        }

    }

    private List<Race> extractTodayRoadRacesFrom(ResponseDTO response) {
        try{
            final var thisMonthRaces = response.items().stream().filter(MonthDTO::isCurrentMonth).findFirst().orElseThrow();
//        final var thisMonthRaces = response.items().stream().filter(monthItem -> monthItem.thisMonthRaces() == 2 && monthItem.year() == 2023).findFirst().orElseThrow();
            final var todayRaces = thisMonthRaces.items().stream().filter(DayDTO::isToday).findFirst();
            return todayRaces.isPresent() ? extractRoadRacesFrom(todayRaces) : emptyList();
//        return new Races(todayRaces.isPresent() ? todayRoadRacesFrom(todayRaces) : emptyList());
        } catch (Exception ex) {
            LOG.error("There was a problem extracting TODAY ROAD RACES: {}", ex.getMessage());
            return emptyList();
        }
    }

    private List<Race> extractRoadRacesFrom(Optional<DayDTO> races) {
        return races.get().items().stream().filter(RaceDTO::isRoadRace).map(race -> race.toDomain(getCompetitionPropertiesFor(race))).toList();
    }

    private CompetitionProperties getCompetitionPropertiesFor(RaceDTO race) {
        try{
            if(Objects.isNull(race) || Objects.isNull(race.detailsLink()) || Objects.isNull(race.detailsLink().url())){
                LOG.error("Not able to build URL to recover COMPETITION PROPERTIES for {}", Optional.ofNullable(race).map(RaceDTO::name).orElse(null));
                return null;
            }
            final var url = host + race.detailsLink().url();
            Document doc = Jsoup.connect(url).get();
            Elements data = doc.select("div[data-component=\"CompetitionDetailsModule\"]");
            final var dataZero = data.get(0);
            final var dataProps = dataZero.attr("data-props");
            return gson.fromJson(dataProps, CompetitionProperties.class);
        } catch (Exception ex) {
            LOG.error("There was a problem getting COMPETITION PROPERTIES FOR {}: {}", race.name(), ex.getMessage());
            return null;
        }

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
