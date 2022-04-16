package it.ctinnovation.droolsbridge.config;

import it.ctinnovation.droolsbridge.model.DecodedMeasurement;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

//@ConfigurationProperties(prefix="measurements")
public class MeasurementProperties {
    public Map<String, DecodedMeasurement> measurementMap = new HashMap<>();

    public Map<String, DecodedMeasurement> getMeasurementMap() {
        return measurementMap;
    }

    public void setMeasurementMap(Map<String, DecodedMeasurement> measurementMap) {
        this.measurementMap = measurementMap;
    }

    @Override
    public String toString() {
        return "MeasurementProperties{" +
                "measurementMap=" + measurementMap +
                '}';
    }
}
