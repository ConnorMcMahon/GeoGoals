package com.example.larkinmcmahon.geogoals;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by djflash on 8/4/15.
 */
public class GoalDeleteFragment extends Fragment {
    private Context mContext;

    public GoalDeleteFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_delete_goal, container, false);
        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra("dbid")) {
            GoalDatabaseHelper db = new GoalDatabaseHelper(getActivity().getApplicationContext());
            int dbid = intent.getIntExtra("dbID", 0);
            Goal currentGoal = db.getGoal(dbid);
            ((TextView) rootView.findViewById(R.id.activity_goal_delete_title_text))
                    .setText("Are you sure you want to delete your " + currentGoal.getTitle() + " goal?");
        }
        return rootView;
    }
}
