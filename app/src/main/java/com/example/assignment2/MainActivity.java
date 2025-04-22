package com.example.assignment2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    DashboardFragment dashboardFrag = new DashboardFragment();
    HistoryFragment historyFrag = new HistoryFragment();
    SettingsFragment settingsFrag = new SettingsFragment();

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean healthPerm = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    healthPerm = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.FOREGROUND_SERVICE_HEALTH, false));
                }
                boolean activityPerm = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    activityPerm = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACTIVITY_RECOGNITION, false));
                }
                boolean fineLocationPerm = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false));
                boolean coarseLocationPerm = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false));

                // Required for TYPE_STEP_COUNTER
                boolean stepsPermissions;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    stepsPermissions = healthPerm && activityPerm;
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    stepsPermissions = activityPerm;
                } else {
                    stepsPermissions = true;
                }

                // Required for location
                boolean locationPermissions = fineLocationPerm || coarseLocationPerm;

                SharedPreferences sharedPref = this.getSharedPreferences("settingsPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                if (!sharedPref.contains("locationSaved")) {

                    editor.putBoolean("locationSaved", true);
                    editor.apply();

                }
                 else if (!locationPermissions){
                     editor.putBoolean("locationSaved", false);
                     editor.apply();
                 }

                if (stepsPermissions) {
                    Context context = getApplicationContext();
                    Intent intent = new Intent(context, SensorService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent);
                    }
                    else {
                        context.startService(intent);
                    }
                    if (!locationPermissions){
                        Toast.makeText(this, "Location Permission is required for tracking", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Health Permission/Location Permission is required for tracking", Toast.LENGTH_LONG).show();
                }

                dashboardFrag = new DashboardFragment();
                changeFrag(dashboardFrag);
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        bottomNav = findViewById(R.id.bottomNavigationView);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.dashboard_nav) {
                changeFrag(new DashboardFragment());
            } else if (itemId == R.id.history_nav) {
                changeFrag(historyFrag);
            } else if (itemId == R.id.settings_nav) {
                changeFrag(settingsFrag);
            }

            return true;
        });

        removeOldData();

        // Generates permissions based on build version
        List<String> permissionsList = getStrings();
        String[] permissions = permissionsList.toArray(new String[0]);
        requestPermissionLauncher.launch(permissions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the foreground service
        Intent stopIntent = new Intent(this, SensorService.class);
        stopService(stopIntent);
    }

    @NonNull
    private static List<String> getStrings() {
        List<String> permissionsList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissionsList.add(Manifest.permission.FOREGROUND_SERVICE_HEALTH);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionsList.add(Manifest.permission.ACTIVITY_RECOGNITION);
        }

        // Location permissions are safe to request on all versions
        permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionsList;
    }

    private void changeFrag(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameContainer, fragment);
        fragmentTransaction.commit();
    }

    private void removeOldData(){
        ArrayList<StepData> loadStepData = Util.loadStepDataList(this);
        ArrayList<LocationData> loadLocationData = Util.loadLocationDataList(this);

        DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();

        // Gets date past 7 days
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        String pastDate = formatDate.format(calendar.getTime());


        loadStepData.removeIf(stepData ->
                stepData.getDate().compareTo(pastDate) < 0);

        loadLocationData.removeIf(locationData ->
                locationData.getDate().compareTo(pastDate) < 0);

        Util.saveStepDataList(this, loadStepData);
        Util.saveLocationDataList(this, loadLocationData);
    }

}