package com.example.larkinmcmahon.geogoals;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

public class GoalEditFragment extends Fragment{

    public GoalEditFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_goal_edit, container, false);
        Intent intent = getActivity().getIntent();
//        if (intent != null && intent.hasExtra("passingGoal")) {
//            Goal currentGoal = (Goal)intent.getSerializableExtra("passingGoal");
//            ((TextView) rootView.findViewById(R.id.activity_goal_edit_title))
//                    .setText(currentGoal.getTitle());
//            int idnum = currentGoal.getOverallID();
//            Log.d("GOALDBHELPER", String.valueOf(idnum));
//        }
        if (intent != null && intent.hasExtra("dbid")) {
            GoalDatabaseHelper db = new GoalDatabaseHelper(getActivity().getApplicationContext());
            int dbid = intent.getIntExtra("dbID", 0);
            GoalEdit goalEdit = new GoalEdit();
            goalEdit.mCurrentGoal = db.getGoal(dbid);
//            Goal currentGoal = db.getGoal(dbid);
            ((TextView) rootView.findViewById(R.id.activity_goal_edit_title))
                    .setText(goalEdit.mCurrentGoal.getTitle());
        }
        return rootView;
    }
}
