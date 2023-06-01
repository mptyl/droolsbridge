package it.ctinnovation.droolsbridge.model;

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
