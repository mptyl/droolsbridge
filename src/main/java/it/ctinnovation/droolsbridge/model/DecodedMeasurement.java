package it.ctinnovation.droolsbridge.model;

import it.ctinnovation.droolsbridge.model.MeasurementType;
import it.ctinnovation.droolsbridge.model.MeasurementValueType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class DecodedMeasurement {
    String description;
    MeasurementType measurementType;
    MeasurementValueType measurementValueType;

    //region Accessors
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MeasurementType getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(MeasurementType measurementType) {
        this.measurementType = measurementType;
    }

    public MeasurementValueType getMeasurementValueType() {
        return measurementValueType;
    }

    public void setMeasurementValueType(MeasurementValueType measurementValueType) {
        this.measurementValueType = measurementValueType;
    }
    //endregion


}
