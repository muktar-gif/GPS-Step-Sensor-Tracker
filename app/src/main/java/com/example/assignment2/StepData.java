package com.example.assignment2;

public class StepData {
    private String date;
    private int steps;
    private double distanceMiles;

    public StepData(String date, int steps, double distanceMiles) {
        this.date = date;
        this.steps = steps;
        this.distanceMiles = distanceMiles;
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
}
