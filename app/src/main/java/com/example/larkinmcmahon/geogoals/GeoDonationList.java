package com.example.larkinmcmahon.geogoals;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Created by djflash on 8/11/15.
 */
public class GeoDonationList  extends AppCompatActivity implements
        ResultCallback<Status> {
    private final String TAG = "GOAL_LIST";
    private GoalDatabaseHelper mDB;
    private boolean mTabletView = false;
    private GeoDonationListFragment mGoalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_donation_list);

        mGoalList = (GeoDonationListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void logSecurityException(SecurityException securityException) {

    }

    public void onResult(Status status){

    }
}
