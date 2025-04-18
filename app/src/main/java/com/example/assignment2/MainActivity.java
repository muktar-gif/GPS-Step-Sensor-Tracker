package com.example.assignment2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.assignment2.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarItemView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    DashboardFragment dashboardFrag = new DashboardFragment();
    HistoryFragment historyFrag = new HistoryFragment();
    SettingsFragment settingsFrag = new SettingsFragment();

    private SensorDataViewModel sensorModel;

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean healthPerm = Boolean.parseBoolean(null);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    healthPerm = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.FOREGROUND_SERVICE_HEALTH, false));
                }
                boolean activityPerm = Boolean.parseBoolean(null);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    activityPerm = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACTIVITY_RECOGNITION, false));
                }
                boolean fineLocationPerm = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false));
                boolean coarseLocationPerm = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false));

                // Required for TYPE_STEP_COUNTER
                boolean stepsPermissions = healthPerm && activityPerm;

                // Required for location
                boolean locationPermissions = fineLocationPerm || coarseLocationPerm;

                // Gets settings
                SharedPreferences sharedPref = this.getSharedPreferences("settingsPrefs", Context.MODE_PRIVATE);

                // Saves location permission to shared preferences for consistency
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("locationSaved", locationPermissions);
                editor.apply();

                if (stepsPermissions || locationPermissions) {
                    startService(new Intent(this, SensorService.class));
                } else {
                    Toast.makeText(this, "Health Permission/Location Permission is required for tracking", Toast.LENGTH_LONG).show();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNav = findViewById(R.id.bottomNavigationView);

        changeFrag(dashboardFrag);

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

        // Generates permissions based on build version
        List<String> permissionsList = getStrings();
        String[] permissions = permissionsList.toArray(new String[0]);
        requestPermissionLauncher.launch(permissions);


        sensorModel = new ViewModelProvider(this).get(SensorDataViewModel.class);

        final Observer<String> stepsObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newName) {
                // Update the UI, in this case, a TextView.
            }
        };

        sensorModel.getCurrentSteps().observe(this, stepsObserver);
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
}