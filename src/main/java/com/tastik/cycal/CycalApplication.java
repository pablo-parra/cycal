package com.tastik.cycal;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CycalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CycalApplication.class, args);

        System.out.println("STARTING...");
//        crawl();
    }

    private static void crawl() {
        try{
            final var url = "https://www.uci.org/competition-details/2023/ROA/70481";
            Document doc = Jsoup.connect(url). get();
            Elements links = doc.select("a[href]");
            Elements media = doc.select("[src]");
            Elements details = doc.select("div.competition-details__group-item");
            System.out.println(doc.getElementsByTag("main").select("div").select("[data-component]").attr("data-props"));
            System.out.println("Bye");
        } catch(Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }
}
