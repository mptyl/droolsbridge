package it.ctinnovation.droolsbridge.service;

import it.ctinnovation.droolsbridge.model.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SetupService {

    public EventAsset setCernusco(){
        String placemarkId="co2001";
        double latitude=45.52621584206354;
        double longitude=9.328162441064132;
        return setupPlacemark(placemarkId,latitude,longitude,createCo2Attribute(),setupCo2Mesurement(2000));
    }

    public EventAsset setCassina(){
        String placemarkId="co2-b01";
        double latitude=45.519322665469275;
        double longitude=9.358348653540615;
        return setupPlacemark(placemarkId,latitude,longitude,createCo2Attribute(),setupCo2Mesurement(500));
    }

    public EventAsset setSegrate(){
        String placemarkId="co2-b02";
        double latitude=45.492113;
        double longitude=9.284531;
        return setupPlacemark(placemarkId,latitude,longitude,createCo2Attribute(),setupCo2Mesurement(900));
    }

    public TheaterPointOfAttention setPioltello(){
        String placemarkId="Pioltello";
        double latitude=45.49262821528002;
        double longitude=9.321798469966655;
        Position position = new Position(latitude,longitude);
        TheaterPointOfAttention poa = new TheaterPointOfAttention();
        poa.setPlacemarkId(placemarkId);
        poa.setPosition(position);
        poa.setMeasurementKey("ppm");
        return poa;
    }

    public TheaterPointOfAttention setMelzo(){
        String placemarkId="Melzo";
        double latitude=45.495635 ;
        double longitude=9.417300;
        Position position = new Position(latitude,longitude);
        TheaterPointOfAttention poa = new TheaterPointOfAttention();
        poa.setPlacemarkId(placemarkId);
        poa.setPosition(position);
        poa.setMeasurementKey("ppm");
        return poa;
    }

    public EventAsset setupPlacemark(String placemarkId, double latitude, double longitude, TheaterAttribute attribute, Measurement measurement){
        EventAsset asset = new EventAsset();
        asset.setPlacemarkId(placemarkId);
        asset.setStatus(Status.UNDEFINED);
        asset.setPosition(new Position(latitude, longitude));
        asset.setTimestamp(new Date());
        asset.setAttribute(attribute);
        ArrayList<Measurement> measurements = new ArrayList<>();
        measurements.add(measurement);
        asset.setPayload(measurements);
        return asset;
    }

    private TheaterAttribute createCo2Attribute() {
        TheaterAttribute attr= new TheaterAttribute();
        attr.setAttributeId("co2");
        attr.setAttributeDescription("Rilevatore C02");
        attr.setAttributeType(AttributeType.CO2);
        attr.setThresholds(setupCo2ThresoldList());
        return attr;
    }


    private ArrayList<Threshold> setupCo2ThresoldList(){
        ArrayList<Threshold> thresoldList=new ArrayList<>();
        thresoldList.add(setupCo2Thresold(ThresholdLevel.SUCCESS,
                ThresholdOperatorType.LT,
                setupCo2Mesurement(1000)));
        thresoldList.add(setupCo2Thresold(ThresholdLevel.WARNING,
                ThresholdOperatorType.BETWEEN,
                setupCo2Mesurement(1000,5000)));
        thresoldList.add(setupCo2Thresold(ThresholdLevel.DANGER,
                ThresholdOperatorType.GT,
                setupCo2Mesurement(5000)));
        return thresoldList;
    }

    private Threshold setupCo2Thresold(ThresholdLevel type, ThresholdOperatorType operator, Measurement measurement){
        Threshold co2Threshold= new Threshold();
        co2Threshold.setType(type);
        co2Threshold.setThresholdOperator(operator);
        co2Threshold.setMeasurement(measurement);
        return co2Threshold;
    }

    private Measurement setupCo2Mesurement(int value){
        return setValue(value);
    }

    private Measurement setupCo2Mesurement(int value, int value2){
        Measurement m = setValue(value);
        m.setValue2(String.valueOf(value2));
        return m;
    }

    private Measurement setValue(int value) {
        Measurement co2= new Measurement();
        co2.setDescription("Livello CO2");
        co2.setKey("co2");
        co2.setMeasurementType(MeasurementType.PPM);
        co2.setMeasurementValueType(MeasurementValueType.INT);
        co2.setValue(String.valueOf(value));
        return co2;
    }



}
