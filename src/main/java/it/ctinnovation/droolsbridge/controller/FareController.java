package it.ctinnovation.droolsbridge.controller;

import it.ctinnovation.droolsbridge.model.Fare;
import it.ctinnovation.droolsbridge.model.TaxiRide;
import it.ctinnovation.droolsbridge.service.TaxiFareCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/getFare")
public class FareController {

    @Autowired
    private TaxiFareCalculatorService taxiFareCalculatorService;

    @GetMapping(value = "/{night}/{distance}")
    public Long getFare(@PathVariable("night") Boolean night, @PathVariable("distance") Long distance){
        TaxiRide taxiRide = new TaxiRide();
        taxiRide.setIsNightSurcharge(night);
        taxiRide.setDistanceInMile(distance);
        Fare rideFare = new Fare();
        Long totalCharge = taxiFareCalculatorService.calculateFare(taxiRide, rideFare);
        return totalCharge;
    }
}
