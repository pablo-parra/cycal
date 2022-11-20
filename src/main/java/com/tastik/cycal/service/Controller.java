package com.tastik.cycal.service;

import com.tastik.cycal.core.domain.Races;
import com.tastik.cycal.core.interactors.UseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cycal")
public class Controller {

    @Autowired
    private UseCase<Races> readRaces;

    @GetMapping("/health")
    public String health() {
        return """ 
                { "status": "OK" }
                """;
    }

    @GetMapping("/today")
    public ResponseEntity<Object> races() {
        return ResponseEntity.ok().body(this.readRaces.execute());
    }
}
