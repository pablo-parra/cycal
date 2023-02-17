package com.tastik.cycal.service;

import com.tastik.cycal.core.domain.Races;
import com.tastik.cycal.core.interactors.UseCase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cycal")
public class Controller {

//    @Autowired
    private UseCase<Races> readRaces;

//    @Autowired
    private UseCase<Races> sendTodayRaces;

    public Controller(
            @Qualifier("ReadDataFromSources") UseCase<Races> readRaces,
            @Qualifier ("SendTodayRoadRaces") UseCase<Races> sendTodayRaces) {
        this.readRaces = readRaces;
        this.sendTodayRaces = sendTodayRaces;
    }

    @GetMapping("/health")
    public String health() {
        return """ 
                { "status": "OK" }
                """;
    }

    @GetMapping("/today")
    public ResponseEntity<Object> getTodayRaces() {
        return ResponseEntity.ok().body(this.readRaces.execute());
    }

    @PostMapping("/sendTodayRaces")
    public ResponseEntity<Object> sendTodayRaces() {
        return ResponseEntity.ok().body(this.sendTodayRaces.execute());
    }
}
