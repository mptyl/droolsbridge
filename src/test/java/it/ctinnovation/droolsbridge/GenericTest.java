package it.ctinnovation.droolsbridge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.ctinnovation.droolsbridge.model.*;
import it.ctinnovation.droolsbridge.util.GeoUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;

public class GenericTest {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GenericTest.class);

    @Test
    public void testAssetCreation() {
        Measurement mn = new Measurement("key01","10");
        Measurement mw = new Measurement("key01","10","30");
        Measurement md = new Measurement("key01","30");
        // Set thresolds
        Threshold threshold1 = new Threshold(ThresholdLevel.SUCCESS,mn, ThresholdOperatorType.LT);
        Threshold threshold2 = new Threshold(ThresholdLevel.WARNING,mw, ThresholdOperatorType.BETWEEN);
        Threshold threshold3 = new Threshold(ThresholdLevel.DANGER,md, ThresholdOperatorType.GT);
        ArrayList<Threshold> thresholds = new ArrayList<>();
        thresholds.add(threshold1);
        thresholds.add(threshold2);
        thresholds.add(threshold3);
        // Set attribute
        Attribute attribute1 = new Attribute("attr01", "descrAttr01", AttributeType.CO2, thresholds);


        Measurement m2 = new Measurement("key01","35");
        ArrayList<Measurement> payload = new ArrayList<>();
        payload.add(m2);

        Asset asset = new Asset();
        asset.setPlacemarkId("placeId01");
        asset.setStatus(Status.NORMAL);
        Position position = new Position(10.10f, 20.20f);
        asset.setPosition(position);
        asset.setTimestamp(new Date());
        asset.setAttribute(attribute1);
        asset.setPayload(payload);

        ObjectMapper om = new ObjectMapper();
        try {
            log.info(om.writeValueAsString(asset));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDistanceCalculator(){
        Asset a = new Asset();
        Position p= new Position(45.49262821528002, 9.321798469966655);
        a.setPosition(p);
        System.out.println("Distanza da ex Filanda="+GeoUtil.calculateDistance(45.52621584206354, 9.328162441064132, a)*100+" km");
        System.out.println("Distanza da Campo sportivo Cassina de Pecchi="+GeoUtil.calculateDistance(45.519322665469275, 9.358348653540615, a)*100+" km");
        System.out.println("Distanza da Parco Europa Segrate="+GeoUtil.calculateDistance(45.526232328033686, 9.328173776388475, a)*100+" km");
    }
}
