package com.example.larkinmcmahon.geogoals;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;


public class GoalEdit extends AppCompatActivity{
    private final String TAG = "GOAL_EDIT";
    public static Goal mCurrentGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_edit);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.goalEditContainer, new GoalEditFragment())
//                    .commit();
//        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_goal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                return true;
            case R.id.submit_goal:
                onClickSubmitGoal();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void onClickSubmitGoal() {

        FragmentManager fm = getSupportFragmentManager();
        GoalEditFragment details = (GoalEditFragment) fm.findFragmentById(R.id.fragment_goal_edit);

        if(details == null || details.getOccurences() == -1 || details.getTimeFrame()==-1 || details.getTitle() == ""){
            Toast errorMessage = Toast.makeText(this, "Required Details missing", Toast.LENGTH_SHORT);
            errorMessage.show();
            return;
        }


        int mUpdateGoalStatusInt = -1;
        String mUpdateGoalStatusString;

        Intent currentIntent = getIntent();
        if (currentIntent != null && currentIntent.hasExtra("dbid")) {
            int dbid = currentIntent.getIntExtra("dbid",-1);
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
                            String.valueOf(dbid)),values,null,projection);
        }

        if(mUpdateGoalStatusInt > 0){
            mUpdateGoalStatusString = "Success";
        }
        else {
            mUpdateGoalStatusString = "Failure";
        }

        Intent intent = new Intent(this, GoalDetail.class)
                .putExtra("dbID",currentIntent.getIntExtra("dbid",0))
                .putExtra("intentType", "SQLUpdate")
                .putExtra("intentMsg",mUpdateGoalStatusString);
        startActivity(intent);
    }
}

