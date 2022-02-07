package it.ctinnovation.droolsbridge.model;


public class Fare {

    private Long nightSurcharge;
    private Long rideFare;

    public Fare() {
        nightSurcharge = 0L;
        rideFare = 0L;
    }

    public Fare(long nightSurcharge, long rideFare) {
        this.nightSurcharge = nightSurcharge;
        this.rideFare = rideFare;
    }

    public Long getNightSurcharge() {
        return nightSurcharge;
    }

    public void setNightSurcharge(Long nightSurcharge) {
        this.nightSurcharge = nightSurcharge;
    }

    public Long getRideFare() {
        return rideFare;
    }

    public void setRideFare(Long rideFare) {
        this.rideFare = rideFare;
    }

    public Long getTotalFare() {
        return nightSurcharge + rideFare;
    }
}
