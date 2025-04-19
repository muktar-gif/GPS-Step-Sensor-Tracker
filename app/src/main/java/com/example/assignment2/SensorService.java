package com.example.assignment2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;



public class SensorService extends Service implements SensorEventListener {

    private FusedLocationProviderClient locationClient;
    private LocationCallback locationCallback;
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "my_channel", // Must match the channel ID in the builder
                    "Activity Tracker",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }


    public SensorService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, "my_channel")
                .setContentTitle("Activity Tracker Running")
                .setContentText("This service is running in the foreground")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();

        // Start service based on version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH);
        } else {
            startForeground(1, notification);
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Load steps sensor
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor steps = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        // Register listener
        sensorManager.registerListener(this, steps, SensorManager.SENSOR_DELAY_NORMAL);

        SharedPreferences sharedPref = getBaseContext().getSharedPreferences("settingsPrefs", Context.MODE_PRIVATE);

        // Gets saved location settings, false is default
        boolean locationEnabled = sharedPref.getBoolean("locationSaved", false);
        boolean fineLocationPerm = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
        boolean coarseLocationPerm = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;

        if (locationEnabled && (fineLocationPerm || coarseLocationPerm)) {

            // Load location client
            locationClient = LocationServices.getFusedLocationProviderClient(this);
            locationCallback = new LocationCallback() {
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    for (Location location : locationResult.getLocations()) {
                        Log.d("Location", String.valueOf(location));
                    }
                }
            };

            com.google.android.gms.location.LocationRequest locationRequest =
                    com.google.android.gms.location.LocationRequest.create()
                            .setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(5000); // 5 seconds

            locationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int totalSteps = (int) sensorEvent.values[0];
            int stepsSinceStart = totalSteps - 0; // initialStepCount is saved on service start
            Log.d("StepService", "Steps taken: " + stepsSinceStart);

            // If counter is less than stored then
            // Add counter to stored - meaning the device/counter was rebooted
            // Else
            // Set stored equal to counter - device has not been reset and you can one to one account for steps

            // Calculate distance based on data using the average stride length
            // Use miles or km based on stored shared preference

        }

        //Track steps if health given
        //Track location if location permission given

        //Calculate distance

        //Pass to live data


        // Figure out how to store data for last 7 days -- make front end once figured out
        // Json if the date/id does not exists create an empty one and add that to the json
        // If it does exist add to it
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}