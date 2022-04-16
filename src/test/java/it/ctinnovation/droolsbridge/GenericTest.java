package it.ctinnovation.droolsbridge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.ctinnovation.droolsbridge.model.*;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;

public class GenericTest {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GenericTest.class);

    @Test
    public void testAssetCreation() {
        Measurement m = new Measurement("key1","35");
        // Set thresolds
        Threshold threshold1 = new Threshold(ThresholdLevel.SUCCESS,m,MeasurementValueType.FLOAT, ThresholdOperatorType.LT, "10","ND");
        Threshold threshold2 = new Threshold(ThresholdLevel.WARNING,m,MeasurementValueType.FLOAT, ThresholdOperatorType.BETWEEN, "10","30");
        Threshold threshold3 = new Threshold(ThresholdLevel.DANGER,m,MeasurementValueType.FLOAT, ThresholdOperatorType.GT, "30","ND");
        ArrayList<Threshold> thresholds = new ArrayList<>();
        thresholds.add(threshold1);
        thresholds.add(threshold2);
        thresholds.add(threshold3);
        // Set attribute
        Attribute attribute1 = new Attribute("attr01", "descrAttr01", AttributeType.CO2, thresholds);


        Measurement m2 = new Measurement("key2","222.0");
        ArrayList<Measurement> payload = new ArrayList<>();
        payload.add(m2);

        Asset asset = new Asset();
        asset.setPlacemarkId("placeIdAAA");
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
}
