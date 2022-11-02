package com.example.reminderapp;

import static com.example.reminderapp.App.CHANNEL_1_ID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reminderapp.DataModel.Note;
import com.example.reminderapp.DataModel.NoteDAO;
import com.example.reminderapp.DataModel.NoteDatabase;
//import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.internal.ILocationSourceDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


//region TODO
// 1) notification information // ~
// 2) dynamic radius
// 3) edit notification
// 4) search button --> in reminders list // optional
// 5) add approval onComplete task
// 6) remove geofence once reminder complete.
//endregion


public class mainApp extends AppCompatActivity implements AddReminderDialog.ReminderAppListener {
    private static final String TAG = "MainApp Tag";
    private View addReminder, search;
    private TextView newR;
    RecyclerView remindersBox;
    private ArrayList testList;
    private ArrayList<Reminder> remindersList; //List of our reminders
    private Adapter.RecyclerViewClickListener listener;
    private View removeItem, saveNote, editReminder;
    NoteDAO noteDAO;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    int LOCATION_PERMISSION_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView locationSelector;
    private double longitude;
    private double latitude;
    private Button show_location;
    private GeofencingClient geofencingClient;
    private ArrayList<Geofence> geofenceList = new ArrayList<>();
    PendingIntent geofencePendingIntent;
    private NotificationManagerCompat notificationManager;
    private int geofenceCounter = 0;
    private LocationRequest GLOBAL_LOCATION_REQUEST;

    private ArrayList<Geofence> test = new ArrayList<>();
    private LocationCallback locationCallback;


    SQL sql;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String currentLocation;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        @SuppressLint("MissingPermission") Task<Location> lastLocation = fusedLocationClient.getLastLocation();
        EditText newLocation = findViewById(R.id.newLocation);
        //DEBUG BROADCAST
        IntentFilter intentFilter = new IntentFilter("com.example.reminderapp");
        GeofenceBroadcastReceiver broadcastReceiver = new GeofenceBroadcastReceiver();
        registerReceiver(broadcastReceiver, intentFilter);
        // DEBUG BROADCAST
        notificationManager = NotificationManagerCompat.from(this);


        //listen to the background location and constantly update values
        lastLocation.addOnSuccessListener(location -> {
            if (location == null) return;
            Toast.makeText(this, location.toString(), Toast.LENGTH_LONG).show();
            updateLocation(location);
            newLocation.setText(location.getLatitude() + "/" + location.getLongitude());
        });


        geofencingClient = LocationServices.getGeofencingClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) return;

                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    newLocation.setText(location.getLatitude() + "/" + location.getLongitude());
                }
            }

        };

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        updateLocationRequest(locationRequest);


        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());


        //Check if Permission granted
        if (ContextCompat.checkSelfPermission(mainApp.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mainApp.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(mainApp.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(mainApp.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }


        sql = new SQL(this);


        getSupportActionBar().hide();
        newR = findViewById(R.id.reminder_text);
        addReminder = findViewById(R.id.addReminder);
        remindersBox = findViewById(R.id.all_reminders); //our recycle view
        remindersList = new ArrayList<>();
        testList = new ArrayList();
        removeItem = findViewById(R.id.complete_reminder);
        editReminder = findViewById(R.id.edit_reminder);
        locationSelector = findViewById(R.id.select_loca);



        noteDAO = NoteDatabase.getDBInstance(this).noteDAO();


        //Can be called within REST API or Local database
        setReminderInto();
        setAdapter();

        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });


    }

    private void updateLocationRequest(LocationRequest locationRequest) {
        GLOBAL_LOCATION_REQUEST = locationRequest;
    }


    @SuppressLint("MissingPermission")
    private void addGeofences() {
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "GEO ADDED", Toast.LENGTH_SHORT).show();

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "GEO FAILED TO BE ADDED", Toast.LENGTH_SHORT).show();
                        // Failed to add geofences
                        // ...
                    }
                });
    }


    //update location
    private void updateLocation(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
//        Toast.makeText(getApplicationContext(), geofenceList.toString(), Toast.LENGTH_SHORT).show();
        return builder.build();

    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {

            return geofencePendingIntent;
        }
           Intent intent = new Intent();
        //DEBUG BROADCAST
        intent.setAction("com.example.reminderapp");
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
        //DEBUG BROADCAST
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return geofencePendingIntent;
    }


    //Request Permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(mainApp.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }

    }


    private void setAdapter() {
        setOnClickListener();
        Adapter adapter = new Adapter(testList, remindersList, listener, noteDAO.getAll());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        remindersBox.setLayoutManager(layoutManager);
        remindersBox.setItemAnimator(new DefaultItemAnimator());
        remindersBox.setAdapter(adapter);
    }

    private void setOnClickListener() {
        listener = new Adapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                ArrayList<Reminder> res = sql.getAllRecords();
                sql.delete(res.get(position).getId());
                remindersList.remove(position);
                setAdapter();
            }
        };
    }


    //Method must be called with Database
    private void setReminderInto() {
        ArrayList<Reminder> res = sql.getAllRecords();
        for (int i = 0; i < res.size(); i++) {
            remindersList.add(new Reminder(res.get(i).getId(), res.get(i).getFreeText(), res.get(i).getItem(), res.get(i).getLongtitue(), res.get(i).getLatitue()));
        }


    }


    public void goToSettings(View view) {
        Intent intent = new Intent(mainApp.this, Settings.class);
        startActivity(intent);
    }

    public void openDialog() {
        AddReminderDialog addReminderDialog = new AddReminderDialog();
        addReminderDialog.show(getSupportFragmentManager(), "Add Reminder");

    }

    public void openLocationDialog() {
        LocationDialog locationDialog = new LocationDialog();
        locationDialog.show(getSupportFragmentManager(), "Location");
    }


    //Once we applying data we also need to add to geofence the locations
    @Override
    public void applyTexts(Reminder nt) {
        remindersList.add(new Reminder(nt.getFreeText(), nt.getId(), nt.getItem(), nt.getLongtitue(), nt.getLatitue()));
        sql.insertData(nt.getFreeText(), nt.getItem(), nt.getLongtitue(), nt.getLatitue());
        TextView txtName = new TextView(mainApp.this);
        Toast.makeText(this, "Text:" + nt.getFreeText() + "id:" + nt.getId(), Toast.LENGTH_SHORT).show();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
        geofenceList.add(new Geofence.Builder()
                .setRequestId(nt.getFreeText())
                .setCircularRegion(nt.getLatitue(), nt.getLongtitue(), 200)
                .setExpirationDuration(10000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setLoiteringDelay(1000)
                .build()

        );
        addGeofences();
    }


    public void sign_out(View view) {
        FirebaseAuth.getInstance().signOut();
        //LoginManager.getInstance().logOut();
        Intent intent = new Intent(mainApp.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        Toast.makeText(this, "WE ARE IN 'start location updates'", Toast.LENGTH_SHORT).show();
        fusedLocationClient.requestLocationUpdates(GLOBAL_LOCATION_REQUEST,
                locationCallback,
                Looper.getMainLooper());
    }
}
