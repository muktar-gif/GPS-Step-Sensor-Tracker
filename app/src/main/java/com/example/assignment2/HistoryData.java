package com.example.assignment2;

public class HistoryData {
    private String date;
    private int steps;
    private double distanceMiles;
    private Double longLoc;
    private Double latLoc;

    HistoryData(String date, int steps, double distanceMiles, Double longLoc, Double latLoc){
        this.date = date;
        this.steps = steps;
        this.distanceMiles = distanceMiles;
        this.longLoc = longLoc;
        this.latLoc = latLoc;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public double getDistanceMiles() {
        return distanceMiles;
    }

    public void setDistanceMiles(double distanceMiles) {
        this.distanceMiles = distanceMiles;
    }

    public Double getLongLoc() {
        return longLoc;
    }

    public void setLongLoc(Double longLoc) {
        this.longLoc = longLoc;
    }

    public Double getLatLoc() {
        return latLoc;
    }

    public void setLatLoc(Double latLoc) {
        this.latLoc = latLoc;
    }
}
