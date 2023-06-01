package it.ctinnovation.droolsbridge;

import com.amazonaws.services.dynamodbv2.xspec.M;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.ctinnovation.droolsbridge.model.*;
import org.apache.poi.ss.formula.functions.T;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GenericTest {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GenericTest.class);

    @Test
    public void testAssetCreation() {
        Measurement mn = new Measurement("key01","1000",MeasurementType.PPM, MeasurementValueType.INT);
        Measurement mw = new Measurement("key01","1000","5000",MeasurementType.PPM, MeasurementValueType.INT);
        Measurement md = new Measurement("key01","5000",MeasurementType.PPM, MeasurementValueType.INT);
        // Set thresolds
        Threshold threshold1 = new Threshold(ThresholdLevel.SUCCESS,mn, ThresholdOperatorType.LT);
        Threshold threshold2 = new Threshold(ThresholdLevel.WARNING,mw, ThresholdOperatorType.BETWEEN);
        Threshold threshold3 = new Threshold(ThresholdLevel.DANGER,md, ThresholdOperatorType.GT);
        ArrayList<Threshold> thresholds = new ArrayList<>();
        thresholds.add(threshold1);
        thresholds.add(threshold2);
        thresholds.add(threshold3);
        // Set attribute
        TheaterAttribute attribute1 = new TheaterAttribute("attr01", "descrAttr01", AttributeType.CO2, thresholds);

        Measurement m2 = new Measurement("key01","3500");
        ArrayList<Measurement> payload = new ArrayList<>();
        payload.add(m2);

        EventAsset asset = new EventAsset();
        asset.setPlacemarkId("placeId01");
        asset.setStatus(Status.SUCCESS);
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
        EventAsset a = new EventAsset();
        Position p= new Position(45.49262821528002, 9.321798469966655);
        a.setPosition(p);
        System.out.println("Distanza da ex Filanda="+GeoUtil.calculateDistance(45.52621584206354, 9.328162441064132, a)*100+" km");
        System.out.println("Distanza da Campo sportivo Cassina de Pecchi="+GeoUtil.calculateDistance(45.519322665469275, 9.358348653540615, a)*100+" km");
        System.out.println("Distanza da Parco Europa Segrate="+GeoUtil.calculateDistance(45.526232328033686, 9.328173776388475, a)*100+" km");
    }

    @Test
    public void compareThresholdLevel(){
        System.out.println(ThresholdLevel.SUCCESS.compareTo(ThresholdLevel.DANGER));
    }

    @Test
    public void testStatus(){
        Threshold trh1=new Threshold(ThresholdLevel.SUCCESS,new Measurement("co2","1000", MeasurementType.PPM,MeasurementValueType.INT),ThresholdOperatorType.LT);
        Threshold trh2=new Threshold(ThresholdLevel.WARNING,new Measurement("co2","1000","5000",MeasurementType.PPM,MeasurementValueType.INT),ThresholdOperatorType.BETWEEN);
        Threshold trh3=new Threshold(ThresholdLevel.DANGER,new Measurement("co2","5000",MeasurementType.PPM,MeasurementValueType.INT),ThresholdOperatorType.GT);
        ArrayList<Threshold> thresholds=new ArrayList<>(Arrays.asList(trh1,trh2,trh3));
        TheaterAttribute attribute=new TheaterAttribute("attr01","Desc01",AttributeType.CO2,thresholds);

        EventAsset asset= new EventAsset("plc01",
                Status.SUCCESS,
                new Position(22d,23d),
                new Date(),
                attribute,
                new ArrayList<Measurement>(Arrays.asList(new Measurement("co2","30000", MeasurementType.PPM,MeasurementValueType.INT))));
        ThresholdLevel tsl=asset.determinaThresholdLevel("co2");
        Assertions.assertEquals(ThresholdLevel.DANGER, tsl);

        EventAsset asset2= new EventAsset("plc01",
                Status.SUCCESS,
                new Position(22d,23d),
                new Date(),
                attribute,
                new ArrayList<Measurement>(Arrays.asList(new Measurement("co2","3000", MeasurementType.PPM,MeasurementValueType.INT))));
        ThresholdLevel tsl2=asset2.determinaThresholdLevel("co2");
        Assertions.assertEquals(ThresholdLevel.WARNING, tsl2);

        EventAsset asset3= new EventAsset("plc01",
                Status.SUCCESS,
                new Position(22d,23d),
                new Date(),
                attribute,
                new ArrayList<Measurement>(Arrays.asList(new Measurement("co2","300", MeasurementType.PPM,MeasurementValueType.INT))));
        ThresholdLevel tsl3=asset3.determinaThresholdLevel("co2");
        Assertions.assertEquals(ThresholdLevel.SUCCESS, tsl3);
    }

    @Test
    public void testDistanceBetweenToPoints(){
        TheaterAsset ta1= new TheaterAsset();
        ta1.setPlacemarkId("ta1");
        ta1.setPosition(new Position(45.519322,9.358348));
        ta1.setMeasurementKey("co2");
        ta1.setThresholdLevel(ThresholdLevel.WARNING);
        ta1.setMeasurementType(MeasurementType.PPM);
        ta1.setMeasurementValueType(MeasurementValueType.FLOAT);
        ta1.setValue("4000");

        TheaterAsset ta2= new TheaterAsset();
        ta2.setPlacemarkId("ta2");
        ta2.setPosition(new Position(45.492113,9.284531));
        ta2.setMeasurementKey("co2");
        ta2.setThresholdLevel(ThresholdLevel.WARNING);
        ta2.setMeasurementType(MeasurementType.PPM);
        ta2.setMeasurementValueType(MeasurementValueType.FLOAT);
        ta2.setValue("4000");

        List<TheaterAsset> tal= new ArrayList<>();
        tal.add(ta1);
        tal.add(ta2);

     }

}
