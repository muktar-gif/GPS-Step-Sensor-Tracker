package com.example.assignment2;

public class LocationData {
    private String date;
    private double longLoc;
    private double latLoc;

    public LocationData(String date, double longLoc, double latLoc) {
        this.date = date;
        this.longLoc = longLoc;
        this.latLoc = latLoc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLongLoc() {
        return longLoc;
    }

    public void setLongLoc(double longLoc) {
        this.longLoc = longLoc;
    }

    public double getLatLoc() {
        return latLoc;
    }

    public void setLatLoc(double latLoc) {
        this.latLoc = latLoc;
    }
}
