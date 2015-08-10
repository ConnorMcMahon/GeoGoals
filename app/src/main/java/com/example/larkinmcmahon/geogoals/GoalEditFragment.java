package com.example.larkinmcmahon.geogoals;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

public class GoalEditFragment extends Fragment{
    Context mContext;
    private static int LOADER_ID = 2;
    public static int mDbID = -1;
    private static final String[] GOAL_DETAIL_COLUMNS = {
            GoalDatabaseHelper.KEY_ID,
            GoalDatabaseHelper.KEY_GOALNAME,
            GoalDatabaseHelper.KEY_OCCURANCES,
            GoalDatabaseHelper.KEY_TIMEFRAME,
            GoalDatabaseHelper.KEY_COMMENTS,
            GoalDatabaseHelper.KEY_STARTDATE,
            GoalDatabaseHelper.KEY_STARTTIME,
            GoalDatabaseHelper.KEY_ENDDATE,
            GoalDatabaseHelper.KEY_ENDTIME,
    };

    //correlate with GOAL_DETAIL_COLUMNS
    private static final int COLUMN_GOALNAME = 1;
    private static final int COLUMN_COMMENTS = 4;
    private static final int COLUMN_OCCURRENCES = 2;
    private static final int COLUMN_TIMEFRAME = 3;

    private TextView mEditGoalTitle;
    private TextView mEditGoalComments;
    private TextView mEditGoalOccurrences;
    private TextView mEditGoalTimeframe;

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

        mEditGoalTitle = ((TextView) getActivity().findViewById(R.id.goal_title_editbox));
        mEditGoalComments = ((TextView) getActivity().findViewById(R.id.goal_comments_editbox));
        mEditGoalOccurrences = ((TextView) getActivity().findViewById(R.id.goal_occurrences_editbox));
        mEditGoalTimeframe = ((TextView) getActivity().findViewById(R.id.goal_timeframe_editbox));

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

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if(mDbID == -1 && getArguments() != null && getArguments().containsKey("dbid")) {
            mDbID = getArguments().getInt("dbid");
        }
        if(mDbID != -1) {
            return new CursorLoader(getActivity(),
                    Uri.withAppendedPath(GoalsProvider.CONTENT_URI, String.valueOf(mDbID)),
                    GOAL_DETAIL_COLUMNS,
                    null,
                    null,
                    null);
        }
        else {
            return null;
        }
    }

    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursor.moveToFirst();
        String mGoalNameText = cursor.getString(COLUMN_GOALNAME);
        String mGoalCommentsText = cursor.getString(COLUMN_COMMENTS);
        String mGoalOccurrencesText = cursor.getString(COLUMN_OCCURRENCES);
        String mGoalTimeframeText = cursor.getString(COLUMN_TIMEFRAME);


        mEditGoalTitle.setText(mGoalNameText);
        mEditGoalComments.setText(mGoalCommentsText);
        mEditGoalTimeframe.setText(mGoalTimeframeText);
        mEditGoalOccurrences.setText(mGoalOccurrencesText);

    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

}
