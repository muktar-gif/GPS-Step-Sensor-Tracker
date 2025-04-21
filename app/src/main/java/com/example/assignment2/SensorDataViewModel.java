package com.example.assignment2;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SensorDataViewModel extends ViewModel {

    private MutableLiveData<Integer> steps;
    private MutableLiveData<Double> distance;
    private MutableLiveData<Double> longLoc;
    private MutableLiveData<Double> latLoc;

    public MutableLiveData<Integer> getCurrentSteps() {
        if (steps == null) {
            steps = new MutableLiveData<>();
        }
        return steps;
    }

    public void setCurrentSteps(Integer value) {
        if (steps == null) {
            steps = new MutableLiveData<>();
        }
        steps.postValue(value);
    }

    public MutableLiveData<Double> getCurrentDistance() {
        if (distance == null) {
            distance = new MutableLiveData<>();
        }
        return distance;
    }

    public void setCurrentDistance(Double value) {
        if (distance == null) {
            distance = new MutableLiveData<>();
        }
        distance.postValue(value);
    }

    public MutableLiveData<Double> getCurrentLongLoc() {
        if (longLoc == null) {
            longLoc = new MutableLiveData<>();
        }
        return longLoc;
    }

    public void setCurrentLongLoc(Double value) {
        if (longLoc == null) {
            longLoc = new MutableLiveData<>();
        }
        longLoc.postValue(value);
    }

    public MutableLiveData<Double> getCurrentLatLoc() {
        if (latLoc == null) {
            latLoc = new MutableLiveData<>();
        }
        return latLoc;
    }

    public void setCurrentLatLoc(Double value) {
        if (latLoc == null) {
            latLoc = new MutableLiveData<>();
        }
        latLoc.postValue(value);
    }

}
