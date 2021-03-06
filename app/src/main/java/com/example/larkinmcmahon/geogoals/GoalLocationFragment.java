package com.example.larkinmcmahon.geogoals;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by connor on 7/30/15.
 * Cleaned and commented on 8/12/15
 */
public class GoalLocationFragment extends Fragment implements GoogleMap.OnMarkerClickListener {
    //constants
    private final static String TAG = "GOAL_LOCATION_FRAGMENT";
    private final static int RADIUS = 1000*5; //5 km, should probably eventually be smaller
    private final static double SEARCH_DISTANCE = .3; //Around 30 km

    //private variables
    private SupportMapFragment fragment;
    private Context mContext;
    private GeofenceService mGeofenceService;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private EditText mSearchBox;
    private List<Marker> mMarkers;
    private List<LatLng> mCoords;
    private ServiceConnection mConnection;

    @Override
    public boolean onMarkerClick(Marker marker){
        //if tentative location, make an actual geofence location
        if(marker.getAlpha()==.5){
            LatLng coords = marker.getPosition();
            Goal goal = ((AddGoal) mContext).getGoal();
            marker.setAlpha((float) 1.0);
            goal.addGeofence(coords, RADIUS);
            //add to list storage
            mCoords.add(coords);
            mMarkers.add(marker);
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //instantiate important variables
        mContext = getActivity();
        mMarkers = new ArrayList<>();
        mCoords = new ArrayList<>();


        View mRootView = inflater.inflate(R.layout.fragment_goal_location, container, false);

        //hide keyboard on focus change
        mSearchBox = (EditText) mRootView.findViewById(R.id.search);
        mSearchBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                    ((AddGoal) mContext).getGoal().setTitle(mSearchBox.getText().toString());
                }

            }
        });


        mSearchBox.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.v(TAG, "finished typing search query");

                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchBox.getWindowToken(), 0);

                for(int i = mMarkers.size() - 1; i >= 0; i--){
                    if (mMarkers.get(i).getAlpha() == .5){
                        mMarkers.get(i).remove();
                    }
                }

                new SearchClicked(mSearchBox.getText().toString()).execute();
                mSearchBox.setText("", TextView.BufferType.EDITABLE);

                return true;
            }
        });

        return mRootView;

    }

    public List<LatLng> getLocations() {
        return mCoords;
    }

    public List<Integer> getRadii(){
        List<Integer> radii = new ArrayList<>();
        for(int i = 0; i < mCoords.size(); i++){
            radii.add(RADIUS);
        }
        return radii;
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        fragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);

        if(fragment == null) {
            //create new version of the map fragment and replace whatever is currently there
            fragment = SupportMapFragment.newInstance();
            getFragmentManager().beginTransaction().replace(R.id.map, fragment).commit();
        }

        //start geofence service
        Intent intent = new Intent(getActivity().getApplicationContext(), GeofenceService.class);
        mContext.startService(intent);

        //connect to the service
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service){
                Log.i(TAG, "Connected to service.");

                //bind the service
                GeofenceService.GeofenceBinder binder = (GeofenceService.GeofenceBinder) service;
                mGeofenceService = binder.getService();

                //check location
                Location lastLocation = mGeofenceService.getLastLocation();

                if (lastLocation != null) {
                    //last location exists, so move to coord object for use
                    LatLng coords = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

                    //create camera position and camera update to move map to current location
                    CameraPosition camPos = new CameraPosition.Builder()
                            .target(coords)
                            .zoom(17)
                            .bearing(lastLocation.getBearing())
                            .build();
                    CameraUpdate zoomIn = CameraUpdateFactory.newCameraPosition(camPos);

                    //get the map and zoom to current location
                    mMap = fragment.getMap();
                    mMap.moveCamera(zoomIn);

                    //add click listener
                    setUpMap();
                } else {
                    //Could not properly get location services

                    //display toast to aler the user of this
                    Toast noLocationToast = Toast.makeText(getActivity(), "Couldn't connect to location services!", Toast.LENGTH_LONG);
                    noLocationToast.show();

                    //redirect to goallist activity
                    Intent backToHome = new Intent(getActivity(), GoalList.class);
                    startActivity(backToHome);
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG, "Disconnected from service.");
            }
        };

        //bind the service and the connection
        mContext.bindService(intent, mConnection, 0);


    }

    @Override
    public void onResume() {
        super.onResume();

        if(mMap == null){
            //get map from the fragment and set up click listeners
            mMap = fragment.getMap();
            setUpMap();
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }



    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //add click listener
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng coords) {
                Goal goal = ((AddGoal) mContext).getGoal();
                goal.addGeofence(coords, RADIUS);
                mCoords.add(coords);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(coords)
                        .draggable(true));
                mMarkers.add(marker);
            }
        });

        mMap.setOnMarkerClickListener(this);

    }

    private class SearchClicked extends AsyncTask<Void, Void, Boolean> {
        private String toSearch;
        private List<Address> results;

        public SearchClicked(String toSearch) {
            this.toSearch = toSearch;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {

                if(mGeofenceService != null){
                    Geocoder geocoder = new Geocoder(mContext, Locale.US);
                    Location lastLocation = mGeofenceService.getLastLocation();

                    if(lastLocation != null){
                        double longitude = lastLocation.getLongitude();
                        double latitude = lastLocation.getLatitude();


                        double lowerLeftLong = Math.max(-180, longitude - SEARCH_DISTANCE);
                        double lowerLeftLat = Math.max(-90, latitude - SEARCH_DISTANCE);
                        double upperRightLong = Math.min(90, longitude + SEARCH_DISTANCE);
                        double upperRightLat = Math.min(180, latitude + SEARCH_DISTANCE);
                        results = geocoder.getFromLocationName(toSearch, 5, lowerLeftLat, lowerLeftLong, upperRightLat, upperRightLong);
                    } else {
                        results = geocoder.getFromLocationName(toSearch, 5);
                    }

                    if (results.size() == 0) {
                        return false;
                    }
                } else {
                    //display toast to aler the user of this
                    Toast noLocationToast = Toast.makeText(getActivity(), "Couldn't connect to location services!", Toast.LENGTH_LONG);
                    noLocationToast.show();

                    //redirect to goallist activity
                    Intent backToHome = new Intent(getActivity(), GoalList.class);
                    startActivity(backToHome);
                }


            } catch (Exception e) {
                Log.e(TAG, "Something went wrong: ", e);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(results == null){
                return;
            }
            Log.v(TAG, "found " + results.size() + " locations");
            //Add markers for each of the results
            for(int i = 0; i < results.size(); i++){
                Address address = results.get(i);
                LatLng coords = new LatLng(address.getLatitude(), address.getLongitude());
                //add transluscent markers to indicate these locations
                if(mMap != null){
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(coords)
                            .alpha((float) .5));
                    mMarkers.add(marker);
                }
            }

            //create a bound around the markers and zoom to that level
            if (mMarkers.size() > 0) {
                //create a bounds for the markers and add all of the markers
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for(Marker marker : mMarkers) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();

                int padding = 20; //padding from edge of map

                //update the map view
                CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(update);
            }

        }
    }
}
