package com.tastik.cycal.data.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tastik.cycal.core.config.LocalDateDeserializer;
import com.tastik.cycal.core.domain.races.CompetitionProperties;
import com.tastik.cycal.data.rest.dtos.RaceDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Component
public class HtmlCrawler {

    @Value("${source.url:https://www.uci.org}")
    private String host;

    private final Gson gson;
    public HtmlCrawler(){
        this.gson = createGson();
    }

    private Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer());
        return gsonBuilder.setPrettyPrinting().create();
    }
    public CompetitionProperties getCompetitionPropertiesFor(RaceDTO race) {
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

    private static final Logger LOG = LoggerFactory.getLogger(HtmlCrawler.class);
}
