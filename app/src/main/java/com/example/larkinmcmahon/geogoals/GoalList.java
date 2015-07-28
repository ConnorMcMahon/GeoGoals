package com.example.larkinmcmahon.geogoals;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;


import java.util.ArrayList;
import java.util.List;


public class GoalList extends AppCompatActivity implements
         ResultCallback<Status> {
    private final String TAG = "GOAL_LIST";
    private PendingIntent mGeofencePendingIntent;
    private ArrayList<Geofence> mGeofenceList;
    private boolean mGeofencesAdded;
    private int mGeoFenceId;
    private GeofenceService mGeofenceService;
    private GoalListFragment mGoalList;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            Log.i(TAG, "Connected to service.");
            GeofenceService.GeofenceBinder binder = (GeofenceService.GeofenceBinder) service;
            mGeofenceService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Disconnected from service.");
        }
    };

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        builder.addGeofences(mGeofenceList);

        return builder.build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeoFenceId = 1;

        mGeofencePendingIntent = null;

        mGeofenceList = new ArrayList<Geofence>();

        setContentView(R.layout.activity_goal_list);

        mGoalList = (GoalListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);


    }


    @Override
    protected void onResume(){
        super.onResume();
        Intent intent = new Intent(this, GeofenceService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_goal_list, menu);
        return true;
    }

    public void addGoal(){
        Intent intent = new Intent(this, GoalLocation.class);
        int requestCode = 1;
        startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        double latitude = data.getDoubleExtra("latitude", -200.0);
        double longitude = data.getDoubleExtra("longitude", -200.0);
        int radius = data.getIntExtra("radius", -1);
        if(latitude == -200.0 || longitude == -200.0 || radius == -1) {
            Log.e(TAG, "invalid form data");
            return;
        }
        addGeofence(latitude, longitude, radius);
        mGoalList.updateListView(mGeofenceList);
        updateGeofences();
    }

    public List<Geofence> getGeofences() {
        return mGeofenceList;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, GeofenceService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
    }

    public void addGeofence(double lat, double lon, int radius){
        mGeofenceList.add(new Geofence.Builder()
                        .setRequestId(String.valueOf(mGeoFenceId++))
                        .setCircularRegion(lat, lon, radius)
                                //sets expiration date for 1 week
                        .setExpirationDuration(1000 * 3600 * 24 * 7)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                                | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build()
        );
    }



    public void updateGeofences() {
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGeofenceService.getClient(),
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
            mGeofenceList.clear();
        } catch (SecurityException securityException) {
            logSecurityException(securityException);
        }

    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    public void onResult(Status status){
        if(status.isSuccess()){
            mGeofencesAdded = !mGeofencesAdded;
        } else {
            Log.e(TAG, "Error occured in adding geofences");
        }
    }
}
