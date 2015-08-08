package com.example.larkinmcmahon.geogoals;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

public class GoalEditFragment extends Fragment{
    Context mContext;

    public GoalEditFragment() {
    }

    public static GoalEditFragment newInstance(){
        GoalEditFragment fragment = new GoalEditFragment();
        return fragment;
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        View rootView = inflater.inflate(R.layout.activity_goal_edit, container, false);
//        Intent intent = getActivity().getIntent();
////        if (intent != null && intent.hasExtra("passingGoal")) {
////            Goal currentGoal = (Goal)intent.getSerializableExtra("passingGoal");
////            ((TextView) rootView.findViewById(R.id.activity_goal_edit_title))
////                    .setText(currentGoal.getTitle());
////            int idnum = currentGoal.getOverallID();
////            Log.d("GOALDBHELPER", String.valueOf(idnum));
////        }
//        if (intent != null && intent.hasExtra("dbid")) {
//            GoalDatabaseHelper db = new GoalDatabaseHelper(getActivity().getApplicationContext());
//            int dbid = intent.getIntExtra("dbID", 0);
//            GoalEdit goalEdit = new GoalEdit();
//            goalEdit.mCurrentGoal = db.getGoal(dbid);
////            Goal currentGoal = db.getGoal(dbid);
//            ((TextView) rootView.findViewById(R.id.activity_goal_edit_title))
//                    .setText(goalEdit.mCurrentGoal.getTitle());
//        }
//        return rootView;
//    }
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

        if (intent != null && intent.hasExtra("dbid")) {
            String projection[] = { GoalDatabaseHelper.KEY_GOALNAME,
                                    GoalDatabaseHelper.KEY_COMMENTS,
                                    GoalDatabaseHelper.KEY_OCCURANCES,
                                    GoalDatabaseHelper.KEY_TIMEFRAME};
            int dbid = intent.getIntExtra("dbID", 0);
            Cursor cursor = getActivity().getContentResolver().query(
                    Uri.withAppendedPath(GoalsProvider.CONTENT_URI,
                            String.valueOf(dbid)),projection,null,null,null);
            if(cursor.moveToFirst()) {
                String mGoalName = cursor.getString(0);
                String mComments = cursor.getString(1);
                String mOccurances = cursor.getString(2);
                String mTimeFrame = cursor.getString(3);

                ((TextView) getActivity().findViewById(R.id.goal_title_editbox))
                        .setText(mGoalName);
                ((TextView) getActivity().findViewById(R.id.goal_comments_editbox))
                        .setText(mComments);
                ((TextView) getActivity().findViewById(R.id.goal_occurrences_editbox))
                        .setText(String.valueOf(mOccurances));
                ((TextView) getActivity().findViewById(R.id.goal_timeframe_editbox))
                        .setText(String.valueOf(mTimeFrame));
            }




//            GoalDatabaseHelper db = new GoalDatabaseHelper(getActivity().getApplicationContext());
//            int dbid = intent.getIntExtra("dbID", 0);
//            GoalEdit goalEdit = (GoalEdit) mContext;
//            goalEdit.mCurrentGoal = db.getGoal(dbid);
////            Goal currentGoal = db.getGoal(dbid);
//            ((TextView) getActivity().findViewById(R.id.goal_title_editbox))
//                    .setText(goalEdit.mCurrentGoal.getTitle());
//            ((TextView) getActivity().findViewById(R.id.goal_comments_editbox))
//                    .setText(goalEdit.mCurrentGoal.getComments());
//            ((TextView) getActivity().findViewById(R.id.goal_occurrences_editbox))
//                    .setText(String.valueOf(goalEdit.mCurrentGoal.getOccurance()));
//            ((TextView) getActivity().findViewById(R.id.goal_timeframe_editbox))
//                    .setText(String.valueOf(goalEdit.mCurrentGoal.getTimeFrame()));

        }
    }

    public String getTitle(){
        String title = ((TextView) getActivity().findViewById(R.id.goal_title_editbox)).getText().toString();
        if(title == null){
            return "";
        }
        return title;
    }

    public String getComment(){
        String comment = ((TextView) getActivity().findViewById(R.id.goal_comments_editbox)).getText().toString();
        if(comment == null){
            return "";
        }
        return comment;
    }

    public int getOccurences(){
        String occurrences = ((TextView) getActivity().findViewById(R.id.goal_occurrences_editbox)).getText().toString();
        if(occurrences == null){
            return -1;
        }
        return Integer.parseInt(occurrences);
    }

    public int getTimeFrame(){
        String timeframe = ((TextView) getActivity().findViewById(R.id.goal_timeframe_editbox)).getText().toString();
        if(timeframe == null){
            return -1;
        }
        return Integer.parseInt(timeframe);
    }
}
