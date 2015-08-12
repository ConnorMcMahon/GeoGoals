package com.example.larkinmcmahon.geogoals;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class GeofenceService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private final String TAG = "GEOFENCE_SERVICE";
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;


    private final Binder mBinder = new GeofenceBinder();

    public GeofenceService() {
        super("GeofenceService");
    }

    public class GeofenceBinder extends Binder {
        GeofenceService getService() {
            return GeofenceService.this;
        }
    }

    public GoogleApiClient getClient(){
        return mGoogleApiClient;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: " + result.getErrorCode());
    }

    public Location getLastLocation(){
        return mLastLocation;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage;
            switch(geofencingEvent.getErrorCode()){
                case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                    errorMessage = "Too many Geofences produced.";
                case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                    errorMessage = "Geofences not available";
                case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    errorMessage = "Too many PendingIntents";
                default:
                    errorMessage = "Error in geofence event.";
            }
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.v(TAG, "Geofence triggered");
            GoalDatabaseHelper dbHelper = new GoalDatabaseHelper(getApplicationContext());

            ArrayList<Goal> goals = dbHelper.getAllGoals();
            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            List<Goal> triggeredGoals = new ArrayList<Goal>();

            for(int i = 0; i < triggeringGeofences.size(); i++){
                for(int j = 0; j < goals.size(); j++){
                    List<Integer> ids = goals.get(j).getIds();
                    for(int k = 0; k < ids.size(); k++){
                        Geofence fence = triggeringGeofences.get(i);
                        fence.getRequestId();
                        if(ids.get(k) == Integer.parseInt(fence.getRequestId())){
                            triggeredGoals.add(goals.get(j));
                            break;
                        }
                    }
                }
            }

            Log.d(TAG, ""+ triggeredGoals.size());

            for(int i = 0; i < triggeredGoals.size(); i++){
                Goal goal = triggeredGoals.get(i);
                Log.d(TAG, "ID: " + goal.getID());
                goal.incrementOccurrences();

                int dbid = goal.getID();

                String projection[] = {GoalDatabaseHelper.KEY_CURRENTOCCURENCES };
                ContentValues values = new ContentValues();
                values.put(GoalDatabaseHelper.KEY_CURRENTOCCURENCES, goal.getCurrentOccurences());
                int mUpdateGoalStatusInt = getContentResolver().update(
                        Uri.withAppendedPath(GoalsProvider.CONTENT_URI,
                                String.valueOf(dbid)),values,null,projection);

                //NOTIFICATION CODE
                // Prepare intent which is triggered if the notification is selected

                int requestID = (int) System.currentTimeMillis();
                Intent fenceDetectedIntent = new Intent(getApplicationContext(), GeoFenceDetected.class)
                        .putExtra("dbID", dbid);

                fenceDetectedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent pIntent = PendingIntent.getActivity(this, 1001, fenceDetectedIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Build notification
                Notification noti = new Notification.Builder(getApplicationContext())
                        .setContentTitle("Goal Location Detected")
                        .setContentText("If this is incorrect, please press the button below.").setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pIntent)
//                .addAction(R.mipmap.ic_launcher, "Call", pIntent)
                                //.addAction(R.mipmap.ic_launcher, "Incorrect", pIntent)
                        .build();

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                // hide the notification after its selected
                noti.flags |= Notification.FLAG_AUTO_CANCEL;

                notificationManager.notify(1, noti);

            }


            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            // Send notification and log the transition details.
            //sendNotification(geofenceTransitionDetails);
            Log.i(TAG, geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e(TAG, "Invalid transition type: " + geofenceTransition);
        }
    }


    @Override
    public Binder onBind(Intent intent) {
        return mBinder;
    }

    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Entered:";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Exited:";
            default:
                return "?:";
        }
    }
}
