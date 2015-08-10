package com.example.larkinmcmahon.geogoals;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.DatePicker;
import android.widget.EditText;

/**
 * Created by djflash on 8/4/15.
 */
public class GoalAdd extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_add);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_goal_add_container, new GoalAddFragment())
                    .commit();
        }
    }

    public void onAddSubmit() {
        EditText titleValue = (EditText)findViewById(R.id.goal_add_title);
        EditText occuranceValue = (EditText)findViewById(R.id.goal_add_occurances);
        EditText timeFrameValue = (EditText)findViewById(R.id.goal_add_timeframe);
        EditText commentsValue = (EditText)findViewById(R.id.goal_add_comments);
//        DatePicker startDateValue = (DatePicker)findViewById(R.id.goal_add_start_date);
//        DatePicker endDateValue = (DatePicker)findViewById(R.id.goal_add_end);
        //DatePicker startDateValue = (DatePicker)findViewById(R.id.goal_add_start_date);;

    }
}
