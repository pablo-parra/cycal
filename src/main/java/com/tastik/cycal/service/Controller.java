package com.tastik.cycal.service;

import com.tastik.cycal.core.domain.races.Races;
import com.tastik.cycal.core.domain.report.Report;
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
    private final UseCase<Races> readRaces;
    private final UseCase<Report> sendReport;
    private final UseCase<Report> reportContent;

    public Controller(
            @Qualifier("ReadRaces") UseCase<Races> readRaces,
            @Qualifier("SendReport") UseCase<Report> sendReport,
            @Qualifier("ReportContent") UseCase<Report> reportContent) {
        this.readRaces = readRaces;
        this.sendReport = sendReport;
        this.reportContent = reportContent;
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

    @GetMapping("/reportContent")
    public ResponseEntity<Object> reportContent() {
        return ResponseEntity.ok().body(this.reportContent.execute());
    }
}
