package com.example.assignment2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ActivityResultLauncher<String[]> requestLocationLauncher;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        requestLocationLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean fineLocationPerm = Boolean.TRUE.equals(result.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false));
            boolean coarseLocationPerm = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false));

            // Required for location
            boolean locationPermissions = fineLocationPerm || coarseLocationPerm;

            // Gets settings
            SharedPreferences sharedPref = requireContext().getSharedPreferences("settingsPrefs", Context.MODE_PRIVATE);

            // Saves location permission to shared preferences for consistency
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("locationSaved", locationPermissions);
            editor.apply();
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Context context = getActivity();
        assert context != null;
        SharedPreferences sharedPref = context.getSharedPreferences("settingsPrefs", Context.MODE_PRIVATE);

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
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("locationSaved", isChecked);
                editor.apply();

                boolean fineLocationPerm = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;
                boolean coarseLocationPerm = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;

                if (!(fineLocationPerm || coarseLocationPerm)){
                    // Generates permissions based on build version
                    String[] permissions = {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    };
                    requestLocationLauncher.launch(permissions);
                }
            }
        });





        return view;
    }
}