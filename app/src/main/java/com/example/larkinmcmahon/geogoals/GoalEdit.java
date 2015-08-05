package com.example.larkinmcmahon.geogoals;

import android.content.Intent;
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
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.goalEditContainer, new GoalEditFragment())
                    .commit();
        }
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

        EditText mEditTitle = (EditText)findViewById(R.id.activity_goal_edit_title);
        //TODO: make fields for other class objects and include them here

        mTitle = mEditTitle.getText().toString();

        GoalDatabaseHelper db = new GoalDatabaseHelper(getApplicationContext());
        int c = db.getGoalCount();
        Log.d(TAG,String.valueOf(c));

        Intent currentIntent = getIntent();
        if (currentIntent != null && currentIntent.hasExtra("dbid")) {
//            mCurrentGoal= (Goal)currentIntent.getSerializableExtra("passingGoal");

            //newGoal = new Goal(mId,mTitle,null,null,0,1,"comments");
            mCurrentGoal.setTitle(mTitle);
            //TODO: put other fields in this Goal creation

//            Goal test = db.getGoal(2);
            db.getAllGoals();
            mUpdateGoalStatusInt = db.updateGoal(mCurrentGoal);
        }

        if(mUpdateGoalStatusInt > 0){
            mUpdateGoalStatusString = "Success";
        }
        else {
            mUpdateGoalStatusString = "Failure";
        }

        Intent intent = new Intent(this, GoalDetail.class)
                .putExtra("dbID",currentIntent.getIntExtra("dbid",0))
                .putExtra("intentType","SQLUpdate")
                .putExtra("intentMsg",mUpdateGoalStatusString);
        startActivity(intent);
    }
}

