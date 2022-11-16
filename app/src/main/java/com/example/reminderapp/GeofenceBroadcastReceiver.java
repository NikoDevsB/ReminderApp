package com.example.reminderapp;

import static android.provider.Settings.System.getString;

import static com.example.reminderapp.App.CHANNEL_1_ID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import me.pushy.sdk.Pushy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GEOFENCE BROADCAST LOGS";

    SQL sql;

    // ...

    private NotificationManagerCompat notificationManager;
    private GeofencingEvent LocationClient;

    public void onReceive(Context context, Intent intent) {

        sql = new SQL(context);
        ArrayList<Reminder> res = sql.getAllRecords();
       // List<Geofence> ab = LocationClient.getTriggeringGeofences();

        Log.i(TAG, "onReceive: BROADCAST IS RECEIVED");

        String message = intent.getStringExtra("toastMessage");


        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            Log.i(TAG, "onReceive: GEOFENCE HAS ERROR");
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.i(TAG, "Transition Type: " + geofenceTransition);

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER
        ) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails, context);
            Log.i(TAG, "GEO TRAN RECEIVED" + geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.i(TAG, "onReceive: ERROR ON RECEIVER");
        }

    }


    private void sendNotification(String geofenceTransitionDetails, Context context) {
        Log.i(TAG, "sendNotification: " + geofenceTransitionDetails);


        // Attempt to extract the "title" property from the data payload, or fallback to app shortcut label
        String notificationTitle = "Reminder App";

        // Attempt to extract the "message" property from the data payload: {"message":"Hello World!"}
        String notificationText = geofenceTransitionDetails.toString();


        // Prepare a notification with vibration, sound and lights
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_baseline_local_grocery_store_24)
                .setContentTitle(notificationTitle)
                .setContentText(geofenceTransitionDetails)
                .setLights(Color.RED, 1000, 1000)
                .setVibrate(new long[]{0, 400, 250, 400})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));

        // Automatically configure a Notification Channel for devices running Android O+
        Pushy.setNotificationChannel(builder, context);

        // Get an instance of the NotificationManager service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        // Build the notification and display it
        notificationManager.notify(1, builder.build());


    }

    private String getGeofenceTransitionDetails(GeofenceBroadcastReceiver geofenceBroadcastReceiver, int geofenceTransition, List<Geofence> triggeringGeofences) {
        Log.i(TAG, "getGeofenceTransitionDetails: " + geofenceTransition);
        return triggeringGeofences.toString();
    }



}


