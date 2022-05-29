package it.ctinnovation.droolsbridge.util;

import it.ctinnovation.droolsbridge.config.MeasurementProperties;
import it.ctinnovation.droolsbridge.model.EventAsset;
import it.ctinnovation.droolsbridge.model.Measurement;
import it.ctinnovation.droolsbridge.model.Threshold;

public class MeasurementMapper{
    public static void remapMeasurement(Object o, MeasurementProperties p){
        EventAsset a = (EventAsset) o;
        for (Measurement m:a.getPayload()){
            mapProperties(p, m);
        }
        for(Threshold t:a.getAttribute().getThresholds()){
            Measurement m = t.getMeasurement();
            mapProperties(p, m);
        }
    }

    private static void mapProperties(MeasurementProperties p, Measurement m) {
        String key= m.getKey();
        m.setDescription(p.getMeasurementMap().get(key).getDescription());
        m.setMeasurementType(p.getMeasurementMap().get(key).getMeasurementType());
        m.setMeasurementValueType(p.getMeasurementMap().get(key).getMeasurementValueType());
    }
}
