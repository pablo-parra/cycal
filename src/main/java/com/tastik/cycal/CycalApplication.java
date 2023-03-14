package com.tastik.cycal;

import com.google.gson.GsonBuilder;
import com.tastik.cycal.core.domain.races.CompetitionProperties;
import com.tastik.cycal.core.config.LocalDateDeserializer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;

@SpringBootApplication
@EnableScheduling
public class CycalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CycalApplication.class, args);
//        crawl();
    }

    private static void crawl() {
        try{
            final var url = "https://www.uci.org/competition-details/2023/ROA/68719";
            Document doc = Jsoup.connect(url). get();
            Elements links = doc.select("a[href]");
            Elements media = doc.select("[src]");
            Elements details = doc.select("div.competition-details__group-item");
            Elements data = doc.select("div[data-component=\"CompetitionDetailsModule\"]");
            final var dataZero = data.get(0);
            final var dataProps = dataZero.attr("data-props");
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer());
            final var gson = gsonBuilder.setPrettyPrinting().create();
            CompetitionProperties o = gson.fromJson(dataProps, CompetitionProperties.class);
            System.out.println(doc.getElementsByTag("main").select("div").select("[data-component]").attr("data-props"));

            System.out.println("Bye");
        } catch(Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }
}
