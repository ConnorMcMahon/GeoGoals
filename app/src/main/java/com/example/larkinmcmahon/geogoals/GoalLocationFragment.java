package com.example.larkinmcmahon.geogoals;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
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
 */
public class GoalLocationFragment extends Fragment implements GoogleMap.OnMarkerClickListener {
    private MapFragment fragment;
    private Context mContext;
    private GeofenceService mGeofenceService;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String TAG = "GOAL_LOCATION_FRAGMENT";
    private EditText mSearchBox;
    private View mRootView;
    private List<Marker> mMarkers;
    final private double SEARCH_DISTANCE = .3; //Around 30 km

    private ServiceConnection mConnection;

    @Override
    public boolean onMarkerClick(Marker marker){
        if(marker.getAlpha()==.5){
            LatLng coords = marker.getPosition();
            Goal goal = ((GoalLocation) mContext).getGoal();
            goal.addGeofence(coords, 50);
            marker.setAlpha((float) 1.0);
        } else {
            marker.remove();
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();
        mMarkers = new ArrayList<Marker>();

        mRootView = inflater.inflate(R.layout.fragment_goal_location, container, false);

        mSearchBox = (EditText) mRootView.findViewById(R.id.search);
        mSearchBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                    ((GoalLocation) mContext).getGoal().setTitle(mSearchBox.getText().toString());
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

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        FragmentManager manager = getFragmentManager();
        fragment = (MapFragment) manager.findFragmentById(R.id.map);
        if(fragment == null) {
            fragment = MapFragment.newInstance();
            manager.beginTransaction().replace(R.id.map, fragment).commit();
        }


        Intent intent = new Intent(mContext, GeofenceService.class);
        mMap = fragment.getMap();

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service){
                Log.i(TAG, "Connected to service.");
                GeofenceService.GeofenceBinder binder = (GeofenceService.GeofenceBinder) service;
                mGeofenceService = binder.getService();


                Location lastLocation = mGeofenceService.getLastLocation();
                LatLng coords = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

                CameraPosition camPos = new CameraPosition.Builder()
                        .target(coords)
                        .zoom(17)
                        .bearing(lastLocation.getBearing())
                        .build();

                CameraUpdate zoomIn = CameraUpdateFactory.newCameraPosition(camPos);
                mMap.moveCamera(zoomIn);
                setUpMap();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG, "Disconnected from service.");
            }
        };
        mContext.startService(intent);
        mContext.bindService(intent, mConnection, 0);


    }

    @Override
    public void onResume() {
        super.onResume();
        if(mMap ==null){
            mMap = fragment.getMap();
            setUpMap();
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mContext.unbindService(mConnection);
    }



    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng coords) {
                Goal goal = ((GoalLocation) mContext).getGoal();
                goal.addGeofence(coords, 50);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(coords)
                        .draggable(true));
                mMarkers.add(marker);
            }
        });

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
                    double longitude = lastLocation.getLongitude();
                    double latitude = lastLocation.getLatitude();

                    double lowerLeftLong = Math.max(-180, longitude - SEARCH_DISTANCE);
                    double lowerLeftLat = Math.max(-90, latitude - SEARCH_DISTANCE);
                    double upperRightLong = Math.min(90, longitude + SEARCH_DISTANCE);
                    double upperRightLat = Math.min(180, latitude + SEARCH_DISTANCE);
                    results = geocoder.getFromLocationName(toSearch, 5, lowerLeftLat, lowerLeftLong, upperRightLat, upperRightLong);

                    if (results.size() == 0) {
                        return false;
                    }
                }


            } catch (Exception e) {
                Log.e(TAG, "Something went wrong: ", e);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result){
            Log.v(TAG, "found " + results.size() + " locations");
            for(int i = 0; i < results.size(); i++){
                Address address = results.get(i);
                LatLng coords = new LatLng(address.getLatitude(), address.getLongitude());
                if(mMap != null){
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(coords)
                            .alpha((float) .5));
                    mMarkers.add(marker);
                }
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for(Marker marker : mMarkers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();

            int padding = 5; //padding from edge of map
            CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(update);
        }
    }
}
