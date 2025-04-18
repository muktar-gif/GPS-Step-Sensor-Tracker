package com.example.assignment2;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SensorDataViewModel extends ViewModel {

    private MutableLiveData<String> steps;
    private MutableLiveData<String> longLoc;
    private MutableLiveData<String> latLoc;

    // Getters
    public MutableLiveData<String> getCurrentSteps() {
        if (steps == null) {
            steps = new MutableLiveData<String>();
        }
        return steps;
    }

    public MutableLiveData<String> getCurrentLongLoc() {
        if (longLoc == null) {
            longLoc = new MutableLiveData<String>();
        }
        return longLoc;
    }

    public MutableLiveData<String> getCurrentLatLoc() {
        if (latLoc == null) {
            latLoc = new MutableLiveData<String>();
        }
        return latLoc;
    }

    // Setters
    public void setCurrentSteps(String value) {
        if (steps == null) {
            steps = new MutableLiveData<>();
        }
        steps.postValue(value); // or steps.setValue(value) if on the main thread
    }

    public void setCurrentLongLoc(String value) {
        if (longLoc == null) {
            longLoc = new MutableLiveData<>();
        }
        longLoc.postValue(value);
    }

    public void setCurrentLatLoc(String value) {
        if (latLoc == null) {
            latLoc = new MutableLiveData<>();
        }
        latLoc.postValue(value);
    }

}
