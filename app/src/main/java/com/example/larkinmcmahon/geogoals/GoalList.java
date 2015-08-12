package com.example.larkinmcmahon.geogoals;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;


import java.net.URI;
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
    private ArrayList<Goal> mGoals;
    private GoalDatabaseHelper mDB;
    private boolean mTabletView = false;
    private boolean detailViewShowing = true;

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

        mDB = new GoalDatabaseHelper(getApplicationContext());


        if(mDB.getAllGoals().size() == 0) {
            List<LatLng> latlns = new ArrayList<>();
            latlns.add(new LatLng(10, 15));

            List<Integer> ints = new ArrayList<Integer>();
            ints.add(3);
            Goal newGoal = new Goal("testing", latlns, ints, new ArrayList<Integer>(), 0, 1, "comment","01-01-15","8:00","02-02-15","10:00");

            ContentValues values = new ContentValues();
            values.put(GoalDatabaseHelper.KEY_ID,newGoal.getID());
            values.put(GoalDatabaseHelper.KEY_GOALNAME,newGoal.getTitle());
            values.put(GoalDatabaseHelper.KEY_OCCURANCES,newGoal.getOccurance());
            values.put(GoalDatabaseHelper.KEY_TIMEFRAME, newGoal.getTimeFrame());
            values.put(GoalDatabaseHelper.KEY_COMMENTS,newGoal.getComments());
            values.put(GoalDatabaseHelper.KEY_STARTDATE,newGoal.getStartDate());
            values.put(GoalDatabaseHelper.KEY_ENDDATE,newGoal.getEndDate());
            values.put(GoalDatabaseHelper.KEY_STARTTIME,newGoal.getStartTime());
            values.put(GoalDatabaseHelper.KEY_ENDTIME,newGoal.getEndTime());

//            values.put(GoalDatabaseHelper.KEY_LAT),
            Uri insertVal = getContentResolver().insert(GoalsProvider.CONTENT_URI,values);

            ArrayList<ContentValues> locationInformation = new ArrayList<ContentValues>();
            ContentValues a = new ContentValues();
            a.put(GoalDatabaseHelper.KEY_COORID,newGoal.getID());
            a.put(GoalDatabaseHelper.KEY_LAT, 10);
            a.put(GoalDatabaseHelper.KEY_LONG, 20);
            a.put(GoalDatabaseHelper.KEY_RADII, 50);

            ContentValues b = new ContentValues();
            a.put(GoalDatabaseHelper.KEY_COORID,newGoal.getID());
            b.put(GoalDatabaseHelper.KEY_LAT, 10);
            b.put(GoalDatabaseHelper.KEY_LONG, 20);
            b.put(GoalDatabaseHelper.KEY_RADII, 50);

            locationInformation.add(a);
            locationInformation.add(b);
            for(int i = 0; i < locationInformation.size(); i++) {
                getContentResolver().insert(GoalsProvider.LOCATION_URI,locationInformation.get(i));
            }

            Goal newGoal2 = new Goal("testing2", latlns, ints,new ArrayList<Integer>(), 0, 1, "comment","01-01-15","8:00","02-02-15","10:00");

            ContentValues values2 = new ContentValues();
            values2.put(GoalDatabaseHelper.KEY_ID,newGoal2.getID());
            values2.put(GoalDatabaseHelper.KEY_GOALNAME,newGoal2.getTitle());
            values2.put(GoalDatabaseHelper.KEY_OCCURANCES,newGoal2.getOccurance());
            values2.put(GoalDatabaseHelper.KEY_TIMEFRAME, newGoal2.getTimeFrame());
            values2.put(GoalDatabaseHelper.KEY_COMMENTS,newGoal2.getComments());
            values2.put(GoalDatabaseHelper.KEY_STARTDATE,newGoal2.getStartDate());
            values2.put(GoalDatabaseHelper.KEY_ENDDATE,newGoal2.getEndDate());
            values2.put(GoalDatabaseHelper.KEY_STARTTIME,newGoal2.getStartTime());
            values2.put(GoalDatabaseHelper.KEY_ENDTIME,newGoal2.getEndTime());

//            values.put(GoalDatabaseHelper.KEY_LAT),
            Uri insertVal2 = getContentResolver().insert(GoalsProvider.CONTENT_URI,values2);

            ArrayList<ContentValues> locationInformation1 = new ArrayList<ContentValues>();
            ContentValues a1 = new ContentValues();
            a1.put(GoalDatabaseHelper.KEY_COORID,newGoal.getID());
            a1.put(GoalDatabaseHelper.KEY_LAT, 10);
            a1.put(GoalDatabaseHelper.KEY_LONG, 20);
            a1.put(GoalDatabaseHelper.KEY_RADII, 50);

            locationInformation.add(a1);

            for(int i = 0; i < locationInformation1.size(); i++) {
                getContentResolver().insert(GoalsProvider.LOCATION_URI,locationInformation1.get(i));
            }
        }

        Goal.setCurrentID(mDB.getGoalCount());


        mGeofenceList = new ArrayList<Geofence>();


        setContentView(R.layout.activity_goal_list);

        mGoalList = (GoalListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        if(getSupportFragmentManager().findFragmentById(R.id.fragment_goal_detail) != null) {
            mTabletView = true;
        }
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
        if(detailViewShowing) {
            getMenuInflater().inflate(R.menu.menu_goal_detail, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.menu_add_goal, menu);
        }
        return true;
    }

    public void addGoalButtonClick(){
        Intent intent = new Intent(this, AddGoal.class);
        int requestCode = 1;
        this.startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        GoalListFragment listFragment = (GoalListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_goal_listview);
        boolean goalSelected = listFragment.mGoalSelected;
        MenuItem item = menu.findItem(R.id.action_editGoal);
        if(item != null) {
            if (goalSelected) {
                item.setEnabled(true);
//                item.getIcon().setAlpha(255);
                //item.setVisible(true);
            } else {
                item.setEnabled(false);
                // item.setVisible(false);
            }
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(mTabletView) {
            if (id == R.id.action_editGoal) {
                GoalDetailFragment detailFragment = (GoalDetailFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_goal_detail);

                int selectedID = detailFragment.mDbID;
                Bundle args = new Bundle();
                args.putInt("dbid", selectedID);

                GoalEditFragment newFragment = new GoalEditFragment();
                newFragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_goal_detail, newFragment, "GOALEDITFRAGMENT")
                        .commit();
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.fragment_goal_edit,newFragment,"editTag")
//                        .commit();
                detailViewShowing = !detailViewShowing;
                invalidateOptionsMenu();
            }
            else if(id == R.id.submit_goal) {
                FragmentManager fm = getSupportFragmentManager();
//                GoalEditFragment details = (GoalEditFragment) fm.findFragmentById(R.id.fragment_goal_edit);
                GoalEditFragment details = (GoalEditFragment) fm.findFragmentByTag("GOALEDITFRAGMENT");

                if (details == null || details.getOccurences() == -1 || details.getTimeFrame() == -1 || details.getTitle() == "") {
                    Toast errorMessage = Toast.makeText(this, "Required Details missing", Toast.LENGTH_SHORT);
                    errorMessage.show();
                    return false;
                }


                int mUpdateGoalStatusInt = -1;
                int dbid = -1;
                String mUpdateGoalStatusString;
                if (details.getArguments() != null) {
                    dbid = details.getArguments().getInt("dbid");
//                Intent currentIntent = getIntent();
//                if (currentIntent != null && currentIntent.hasExtra("dbid")) {
//                    int dbid = currentIntent.getIntExtra("dbid",-1);
                    String projection[] = {GoalDatabaseHelper.KEY_GOALNAME, GoalDatabaseHelper.KEY_COMMENTS,
                            GoalDatabaseHelper.KEY_TIMEFRAME, GoalDatabaseHelper.KEY_OCCURANCES, GoalDatabaseHelper.KEY_STARTDATE,
                            GoalDatabaseHelper.KEY_ENDDATE};

                    ContentValues values = new ContentValues();
                    values.put(GoalDatabaseHelper.KEY_GOALNAME, details.getTitle());
                    values.put(GoalDatabaseHelper.KEY_COMMENTS, details.getComment());
                    values.put(GoalDatabaseHelper.KEY_TIMEFRAME, details.getTimeFrame());
                    values.put(GoalDatabaseHelper.KEY_OCCURANCES, details.getOccurences());
                    values.put(GoalDatabaseHelper.KEY_STARTDATE, details.getStartDate());
                    values.put(GoalDatabaseHelper.KEY_ENDDATE, details.getEndDate());

                    mUpdateGoalStatusInt = getContentResolver().update(
                            Uri.withAppendedPath(GoalsProvider.CONTENT_URI,
                                    String.valueOf(dbid)), values, null, projection);
                }

                if(mUpdateGoalStatusInt > 0){
                    mUpdateGoalStatusString = "Success";
                }
                else {
                    mUpdateGoalStatusString = "Failure";
                }

                detailViewShowing = !detailViewShowing;
                invalidateOptionsMenu();

                Bundle args = new Bundle();
                args.putInt("dbid",dbid);

                GoalDetailFragment newFragment = new GoalDetailFragment();
                newFragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_goal_detail, newFragment, "GOALDETAILFRAGMENT")
                        .commit();
            }
        }
        else {
            if (id == R.id.action_settings) {
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            Goal goal = data.getParcelableExtra("goal");
            Log.v(TAG, "Goal returned: " + goal.getTitle());
            addGoal(goal);
//            mGoalList.updateListView(mGoals);
//            mGoals.clear();
        } else {
            Log.e(TAG, "Error in activity result");
        }

    }

    public List<Goal> getGoals() {
        return mGoals;
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

    /*
        Updates the private variable mGoals with the new goal, as well as adding all geofences associated
        with the new goal to the list of geofences
     */
    public void addGoal(Goal goal){

        int id = goal.getID();

        ContentValues values = new ContentValues();
        values.put(GoalDatabaseHelper.KEY_ID,id);
        values.put(GoalDatabaseHelper.KEY_GOALNAME,goal.getTitle());
        values.put(GoalDatabaseHelper.KEY_OCCURANCES,goal.getOccurance());
        values.put(GoalDatabaseHelper.KEY_TIMEFRAME, goal.getTimeFrame());
        values.put(GoalDatabaseHelper.KEY_COMMENTS,goal.getComments());
        values.put(GoalDatabaseHelper.KEY_STARTDATE,goal.getStartDate());
        values.put(GoalDatabaseHelper.KEY_ENDDATE,goal.getEndDate());
        values.put(GoalDatabaseHelper.KEY_STARTTIME,goal.getStartTime());
        values.put(GoalDatabaseHelper.KEY_ENDTIME, goal.getEndTime());
        values.put(GoalDatabaseHelper.KEY_CURRENTOCCURENCES, goal.getCurrentOccurences());

//            values.put(GoalDatabaseHelper.KEY_LAT),
        Uri insertVal = getContentResolver().insert(GoalsProvider.CONTENT_URI, values);


        List<LatLng> coords = goal.getLocations();
        List<Integer> radii = goal.getRadii();
        List<Integer> ids = goal.getIds();


        ArrayList<ContentValues> locationInformation = new ArrayList<ContentValues>();






        for(int i = 0; i < coords.size(); i++){
            LatLng coord = coords.get(i);

            ContentValues locationValue = new ContentValues();
            locationValue.put(GoalDatabaseHelper.KEY_COORID, id);
            locationValue.put(GoalDatabaseHelper.KEY_LAT, coord.latitude);
            locationValue.put(GoalDatabaseHelper.KEY_LONG, coord.longitude);
            locationValue.put(GoalDatabaseHelper.KEY_RADII, radii.get(i));

            locationInformation.add(locationValue);

            mGeofenceList.add(new Geofence.Builder()
                            .setRequestId(String.valueOf(ids.get(i)))
                            .setCircularRegion(coord.latitude, coord.longitude, radii.get(i))
                                    //sets expiration date for 1 week
                            .setExpirationDuration(1000 * 3600 * 24 * 7)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                            .setLoiteringDelay(10000) //10 seconds for testing purposes
                            .build()
            );

        }

        for(int i = 0; i < locationInformation.size(); i++) {
            getContentResolver().insert(GoalsProvider.LOCATION_URI, locationInformation.get(i));
        }

        Toast toast = Toast.makeText(this, ("" + mDB.getAllGoals().size()), Toast.LENGTH_SHORT);
        toast.show();

        //sends the geofence list to the listeners
        updateGeofences();

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
