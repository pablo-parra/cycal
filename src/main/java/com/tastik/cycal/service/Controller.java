package com.tastik.cycal.service;

import com.tastik.cycal.core.domain.Races;
import com.tastik.cycal.core.domain.Report;
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
    private final UseCase<Races> readRaces;

//    @Autowired
    private final UseCase<Report> sendReport;

    public Controller(
            @Qualifier("ReadRaces") UseCase<Races> readRaces,
            @Qualifier ("SendReport") UseCase<Report> sendReport) {
        this.readRaces = readRaces;
        this.sendReport = sendReport;
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

    @PostMapping("/sendReport")
    public ResponseEntity<Object> sendReport() {
        return ResponseEntity.ok().body(this.sendReport.execute());
    }
}
