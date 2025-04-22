package com.example.assignment2;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Spinner;
import android.widget.Toast;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        SharedPreferences sharedPref = requireContext().getSharedPreferences("settingsPrefs", Context.MODE_PRIVATE);

        // Gets saved location settings, false is default
        boolean locationEnabled = sharedPref.getBoolean("locationSaved", false);

        // Gets saved units, first item is default
        String[] units = getResources().getStringArray(R.array.unit_list);
        String unitsPreferred = sharedPref.getString("unitsSaved", units[0]);

        // Updates unit preference spinner with list of option
        Spinner unitSpinner = view.findViewById(R.id.unitsDropdown);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.unit_list,
                R.layout.spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);

        // Updates selection with saved preference
        int index = adapter.getPosition(unitsPreferred);
        unitSpinner.setSelection(index);

        // Updates saved preference on change
        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("unitsSaved", adapterView.getItemAtPosition(i).toString());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Updates switch with saved preference
        SwitchCompat locationSwitch = view.findViewById(R.id.locationSwitch);
        locationSwitch.setChecked(locationEnabled);

        // Updates saved preference on change
        locationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            boolean healthPerm = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                healthPerm = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.FOREGROUND_SERVICE_HEALTH) ==
                        PackageManager.PERMISSION_GRANTED;
            }
            boolean activityPerm = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                activityPerm = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACTIVITY_RECOGNITION) ==
                        PackageManager.PERMISSION_GRANTED;
            }

            boolean fineLocationPerm = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED;
            boolean coarseLocationPerm = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED;

            // Required for TYPE_STEP_COUNTER
            boolean stepsPermissions;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                stepsPermissions = healthPerm && activityPerm;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                stepsPermissions = activityPerm;
            } else {
                stepsPermissions = true;
            }

            boolean locationPermissions = fineLocationPerm || coarseLocationPerm;

            if (!stepsPermissions){
                Toast.makeText(requireContext(), "Health and location permission is required for tracking, restart after", Toast.LENGTH_LONG).show();
                locationSwitch.setChecked(false);
            }
            else if (!locationPermissions){
                Toast.makeText(requireContext(), "Location permission is required for tracking, restart after", Toast.LENGTH_LONG).show();
                locationSwitch.setChecked(false);

            }
            else {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("locationSaved", isChecked);
                editor.apply();
            }
        });

        return view;
    }
}