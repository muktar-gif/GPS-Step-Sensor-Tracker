package com.example.assignment2;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Util {

    public static void saveStepDataList(Context context, ArrayList<StepData> stepList) {
        SharedPreferences sharedPref = context.getSharedPreferences("sensorPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        Gson gson = new Gson();
        String json = gson.toJson(stepList);

        editor.putString("stepData", json);
        editor.apply();
    }

    public static ArrayList<StepData> loadStepDataList(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("sensorPrefs", Context.MODE_PRIVATE);
        String jsonData = sharedPref.getString("stepData", null);

        if (jsonData != null && !jsonData.isEmpty()) {
            Gson gson = new Gson();
            Type getType = new TypeToken<ArrayList<StepData>>() {}.getType();
            return gson.fromJson(jsonData, getType);
        }

        return new ArrayList<>();
    }

    public static void saveLocationDataList(Context context, ArrayList<LocationData> locationList) {
        SharedPreferences sharedPref = context.getSharedPreferences("sensorPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        Gson gson = new Gson();
        String json = gson.toJson(locationList);

        editor.putString("locationData", json);
        editor.apply();
    }

    public static ArrayList<LocationData> loadLocationDataList(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("sensorPrefs", Context.MODE_PRIVATE);
        String jsonData = sharedPref.getString("locationData", null);

        if (jsonData != null && !jsonData.isEmpty()) {
            Gson gson = new Gson();
            Type getType = new TypeToken<ArrayList<LocationData>>() {}.getType();
            return gson.fromJson(jsonData, getType);
        }

        return new ArrayList<>();
    }

}

