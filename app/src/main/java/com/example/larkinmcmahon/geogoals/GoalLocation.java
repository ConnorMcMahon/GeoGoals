package com.example.larkinmcmahon.geogoals;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Attr;


public class GoalLocation extends Activity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String TAG = "GOAL_LOCATION";

    private EditText mTitleText;
    private Goal mGoal;

    private ServiceConnection mConnection;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoal = new Goal("Geofence");



        setContentView(R.layout.activity_goal_location);

    }

    public Goal getGoal(){
        return mGoal;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }




    public void onClickSubmitGoal(View v){
        Intent intent = new Intent();
        //intent.putExtra("goal", mGoal); //eric comment - wasn't compiling
        setResult(RESULT_OK, intent);
        finish();
        Log.v(TAG, "Sending goal back to GoalList");
    }
}
