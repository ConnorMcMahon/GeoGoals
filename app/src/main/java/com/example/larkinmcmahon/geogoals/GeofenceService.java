package com.example.larkinmcmahon.geogoals;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;

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
        Log.i(TAG, "onStartCommand called");
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
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();

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

    protected String getGeofenceTransitionDetails(Context context, int geofenceTransition, List<Geofence> triggerGeofences) {
        return "";
    }

    @Override
    public Binder onBind(Intent intent) {
        return mBinder;
    }
}
