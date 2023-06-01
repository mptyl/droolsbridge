package it.ctinnovation.droolsbridge.config;

import it.ctinnovation.droolsbridge.model.DecodedMeasurement;

import java.util.HashMap;
import java.util.Map;

/**
 * Mappa key-value relativa alle misure che permette di associare a una key definita come stringa da un utente:
 * - una descrizione della misura, come label
 * - un tipo misura (es VOLT, AMPERE, ecc.)
 * - un tipo valore misura (es. INT, FLOAT, ecc.)
 *
 * Questa mappatura permette di razionalizzare e standardizzare la gestione delle misure ai fini del calcolo
 * delle rules
 */
public class MeasurementProperties {
    public Map<String, DecodedMeasurement> measurementMap = new HashMap<>();

    //region Accessors
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
    //endregion
}
