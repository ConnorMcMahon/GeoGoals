package com.example.larkinmcmahon.geogoals;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by djflash on 8/4/15.
 */
public class GoalDelete extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_goal);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.activity_delete_container, new DeleteGoalFragment())
//                    .commit();
//        }
    }

    public void onClickConfirmDelete(View view) {
        Intent currentIntent = getIntent();
        if (currentIntent != null && currentIntent.hasExtra("dbid")) {
            GoalDatabaseHelper db = new GoalDatabaseHelper(getApplicationContext());
            int dbid = currentIntent.getIntExtra("dbID", 0);
            Goal currentGoal = db.getGoal(dbid);
            int deleteStatus = db.deleteGoal(currentGoal);
            Intent intent = new Intent(this, GoalList.class)
                    .putExtra("deleting", deleteStatus);
            startActivity(intent);
        }
    }
}
