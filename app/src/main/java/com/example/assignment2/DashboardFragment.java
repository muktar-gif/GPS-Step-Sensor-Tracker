package com.example.assignment2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DashboardFragment extends Fragment {

    private SensorDataViewModel model;

    // Variables to register local receiver
    private BroadcastReceiver receiveSteps;
    private BroadcastReceiver receiveDistance;
    private BroadcastReceiver receiveLong;
    private BroadcastReceiver receiveLat;

    boolean locationEnabled;

    private final double KM_IN_MILES = 1.609;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        TextView stepsText = view.findViewById(R.id.stepsVal);
        TextView distanceText = view.findViewById(R.id.distanceVal);
        TextView longText = view.findViewById(R.id.longVal);
        TextView latText = view.findViewById(R.id.latVal);

        SharedPreferences sharedPref = requireContext().getSharedPreferences("settingsPrefs", Context.MODE_PRIVATE);

        // Gets saved location settings, false is default
        locationEnabled = sharedPref.getBoolean("locationSaved", false);

        // Gets saved units, first item is default
        String[] units = getResources().getStringArray(R.array.unit_list);
        String unitsPreferred = sharedPref.getString("unitsSaved", units[0]);

        ArrayList<StepData> loadStepData = Util.loadStepDataList(requireContext());
        ArrayList<LocationData> loadLocationData = Util.loadLocationDataList(requireContext());

        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = formatDate.format(new Date());

        StepData getStepData = null;
        for (StepData data : loadStepData) {
            if (data.getDate().equals(dateString)) {
                getStepData = data;
                break;
            }
        }

        LocationData getLocationData = null;
        for (LocationData data : loadLocationData) {
            if (data.getDate().equals(dateString)) {
                getLocationData = data;
                break;
            }
        }

        if (getStepData != null){

            String distance;
            if (Objects.equals(unitsPreferred, "Miles")) {
                distance = String.format(Locale.getDefault(), "%.4f", getStepData.getDistanceMiles()) + " mi";
            }
            else{
                distance = String.format(Locale.getDefault(), "%.4f", getStepData.getDistanceMiles() * KM_IN_MILES) + " km";
            }

            stepsText.setText(String.valueOf(getStepData.getSteps()));
            distanceText.setText(distance);
        }

        if (getLocationData != null) {

            if (!locationEnabled) {
                CardView locationCard = view.findViewById(R.id.locationCard);
                locationCard.setAlpha(0.3f);
                longText.setText("--");
                latText.setText("--");
            }
            else {
                longText.setText(String.format(Locale.getDefault(), "%.5f", getLocationData.getLongLoc()));
                latText.setText(String.format(Locale.getDefault(), "%.5f", getLocationData.getLatLoc()));
            }
        }

        model = new ViewModelProvider(requireActivity()).get(SensorDataViewModel.class);

        final Observer<Integer> stepObserver = newStep -> {
            if (newStep != null) {
                stepsText.setText(String.valueOf(newStep));
            } else {
                stepsText.setText("0");
            }
        };
        final Observer<Double> distanceObserver = newDistance -> {
            if (newDistance != null) {
                String distance;
                if (Objects.equals(unitsPreferred, "Miles")) {
                    distance = String.format(Locale.getDefault(), "%.4f", newDistance) + " mi";
                }
                else{
                    distance = String.format(Locale.getDefault(), "%.4f", newDistance * KM_IN_MILES) + " km";
                }

                distanceText.setText(distance);
            } else {
                distanceText.setText("0");
            }
        };
        final Observer<Double> longObserver = newLong -> {
            if (locationEnabled && newLong != null) {
                longText.setText(String.format(Locale.getDefault(), "%.5f", newLong));
            } else {
                longText.setText("--");
            }
        };
        final Observer<Double> latObserver = newLat -> {
            if (locationEnabled && newLat != null) {
                latText.setText(String.format(Locale.getDefault(), "%.5f", newLat));
            } else {
                latText.setText("--");
            }
        };

        model.getCurrentSteps().observe(getViewLifecycleOwner(), stepObserver);
        model.getCurrentDistance().observe(getViewLifecycleOwner(), distanceObserver);
        model.getCurrentLongLoc().observe(getViewLifecycleOwner(), longObserver);
        model.getCurrentLatLoc().observe(getViewLifecycleOwner(), latObserver);

        // Retrieves steps value after local broadcast
        receiveSteps = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    int steps = intent.getIntExtra("steps", 0);

                    model.setCurrentSteps(steps);
                }
            }
        };
        // Retrieves distance value after local broadcast
        receiveDistance = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    double distance = intent.getDoubleExtra("distance", 0);

                    model.setCurrentDistance(distance);
                }
            }
        };
        // Retrieves long value after local broadcast
        receiveLong = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    double longitude = intent.getDoubleExtra("longitude", 0);

                    model.setCurrentLongLoc(longitude);
                }
            }
        };
        // Retrieves lat value after local broadcast
        receiveLat = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    double latitude = intent.getDoubleExtra("latitude", 0);

                    model.setCurrentLatLoc(latitude);
                }
            }
        };

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Register local receivers
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiveSteps,
                new IntentFilter("stepsBroad"));
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiveDistance,
                new IntentFilter("distanceBroad"));
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiveLong,
                new IntentFilter("longBroad"));
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiveLat,
                new IntentFilter("latBroad"));
    }

    @Override
    public void onPause() {
        super.onPause();
        // Register local receivers
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiveSteps);
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiveDistance);
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiveLong);
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiveLat);
    }
}