package com.example.assignment2;

import android.app.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
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
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class SensorService extends Service implements SensorEventListener {

    SensorManager sensorManager;
    FusedLocationProviderClient locationClient;
    LocationCallback locationCallback;

    private final double STEP_LENGTH_MILES = 0.0005;

    public SensorService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("my_channel", "Activity Tracker", NotificationManager.IMPORTANCE_DEFAULT);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }


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

        // Load steps sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor steps = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        // Register listener
        sensorManager.registerListener(this, steps, SensorManager.SENSOR_DELAY_NORMAL);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        boolean fineLocationPerm = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
        boolean coarseLocationPerm = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;

        if (fineLocationPerm || coarseLocationPerm) {

            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                    .setGranularity(Granularity.GRANULARITY_FINE)
                    .setMinUpdateDistanceMeters(2).build();

            locationCallback = new LocationCallback() {
                public void onLocationResult(@NonNull LocationResult locationResult) {

                    SharedPreferences sharedPref = getBaseContext().getSharedPreferences("settingsPrefs", Context.MODE_PRIVATE);
                    // Gets saved location settings, false is default
                    boolean locationEnabled = sharedPref.getBoolean("locationSaved", false);

                    if (!locationEnabled){
                        return;
                    }

                    Location location = locationResult.getLastLocation();

                    double longLoc;
                    double latLoc;

                    if (location != null) {
                        longLoc = location.getLongitude();
                        latLoc = location.getLatitude();
                    }
                    else {
                        return;
                    }

                    ArrayList<LocationData> loadData = Util.loadLocationDataList(getBaseContext());
                    SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String dateString = formatDate.format(new Date());

                    LocationData getData = null;
                    for (LocationData data : loadData) {
                        if (data.getDate().equals(dateString)) {
                            getData = data;
                            break;
                        }
                    }

                    if (getData == null){
                        getData = new LocationData(dateString, longLoc, latLoc);
                        loadData.add(getData);
                    }
                    else {
                        getData.setLongLoc(longLoc);
                        getData.setLatLoc(latLoc);
                    }

                    // Pass steps local broadcast
                    Intent longIntent = new Intent("longBroad");
                    longIntent.putExtra("longitude", getData.getLongLoc());

                    // Send step info to context
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(longIntent);

                    // Pass steps local broadcast
                    Intent latIntent = new Intent("latBroad");
                    latIntent.putExtra("latitude", getData.getLatLoc());

                    // Send step info to context
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(latIntent);

                    Util.saveLocationDataList(getBaseContext(), loadData);
                }
            };

            // Load location client
            locationClient = LocationServices.getFusedLocationProviderClient(this);
            locationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        if (locationClient != null){
            locationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {

            ArrayList<StepData> loadData = Util.loadStepDataList(getBaseContext());
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String dateString = formatDate.format(new Date());

            StepData getData = null;
            for (StepData data : loadData) {
                if (data.getDate().equals(dateString)) {
                    getData = data;
                    break;
                }
            }

            if (getData == null){
                getData = new StepData(dateString, 1, STEP_LENGTH_MILES);
                loadData.add(getData);
            }
            else {
                int dataSteps = getData.getSteps() + 1;
                getData.setSteps(dataSteps);
                getData.setDistanceMiles(dataSteps * STEP_LENGTH_MILES);
            }

            // Pass steps local broadcast
            Intent stepsIntent = new Intent("stepsBroad");
            stepsIntent.putExtra("steps", getData.getSteps());

            // Send step info to context
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(stepsIntent);

            // Pass distance local broadcast
            Intent distanceIntent = new Intent("distanceBroad");
            distanceIntent.putExtra("distance", getData.getDistanceMiles());

            // Send step info to context
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(distanceIntent);

            Util.saveStepDataList(getBaseContext(), loadData);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}