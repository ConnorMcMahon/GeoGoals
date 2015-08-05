package com.example.larkinmcmahon.geogoals;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GoalEditFragment extends Fragment{
    Context mContext;

    public GoalEditFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();

        View rootView = inflater.inflate(R.layout.fragment_goal_edit, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
            GoalEdit goalEdit = (GoalEdit) mContext;
            goalEdit.mCurrentGoal = db.getGoal(dbid);
//            Goal currentGoal = db.getGoal(dbid);
            ((TextView) getActivity().findViewById(R.id.goal_title_editbox))
                    .setText(goalEdit.mCurrentGoal.getTitle());
            ((TextView) getActivity().findViewById(R.id.goal_comments_editbox))
                    .setText(goalEdit.mCurrentGoal.getComments());
            ((TextView) getActivity().findViewById(R.id.goal_occurrences_editbox))
                    .setText(String.valueOf(goalEdit.mCurrentGoal.getOccurance()));
            ((TextView) getActivity().findViewById(R.id.goal_timeframe_editbox))
                    .setText(String.valueOf(goalEdit.mCurrentGoal.getTimeFrame()));

        }
    }
}
