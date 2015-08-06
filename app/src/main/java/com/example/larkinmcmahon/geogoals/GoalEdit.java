package com.example.larkinmcmahon.geogoals;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
        //getMenuInflater().inflate(R.menu.men, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    public void onClickSubmitGoal(View view) {
        String mTitle;
        List<LatLng> mLocation;
        List<Integer> mRadii;
        int mOccurrences;
        int mTimeFrame;
        String mComments;
        int mUpdateGoalStatusInt = 0;
        String mUpdateGoalStatusString = null;

        EditText mEditTitle = (EditText)findViewById(R.id.goal_title_editbox);
        //TODO: make fields for other class objects and include them here

        mTitle = mEditTitle.getText().toString();

        Intent currentIntent = getIntent();
        if (currentIntent != null && currentIntent.hasExtra("dbid")) {
            int dbid = currentIntent.getIntExtra("dbid",-1);
            String projection[] = {GoalDatabaseHelper.KEY_GOALNAME };
            ContentValues values = new ContentValues();
            values.put(GoalDatabaseHelper.KEY_GOALNAME, mTitle);
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

