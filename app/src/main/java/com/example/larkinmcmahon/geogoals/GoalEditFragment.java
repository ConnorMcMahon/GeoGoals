package com.example.larkinmcmahon.geogoals;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GoalEditFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    Context mContext;
    private static int LOADER_ID = 2;
    public static SimpleDateFormat dbFormat = new SimpleDateFormat("MM/dd/yyyy");
    public int mDbID = -1;
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
    private static final int COLUMN_STARTDATE = 5;
    private static final int COLUMN_ENDDATE = 7;


    private TextView mEditGoalTitle;
    private TextView mEditGoalComments;
    private TextView mEditGoalOccurrences;
    private TextView mEditGoalTimeframe;
    private TextView mEditGoalStartDate;
    private TextView mEditGoalEndDate;

    public GoalEditFragment() {
    }

    public static GoalEditFragment newInstance(){
        GoalEditFragment fragment = new GoalEditFragment();
        fragment.getLoaderManager().initLoader(LOADER_ID, null, fragment);
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



        Intent intent = getActivity().getIntent();
        if(intent.hasExtra("dbid")){
            mDbID = intent.getIntExtra("dbid",-1);
        }



        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
        mEditGoalStartDate = ((TextView) getActivity().findViewById(R.id.goal_startdate_edittext));
        mEditGoalEndDate = ((TextView) getActivity().findViewById(R.id.goal_enddate_edittext));

        setUpDateDialogs();


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

    public String getStartDate(){
        String startDate = ((TextView) getActivity().findViewById(R.id.goal_startdate_edittext)).getText().toString();
        if(startDate == null){
            return "";
        }
        return startDate;
    }

    public String getEndDate(){
        String endDate = ((TextView) getActivity().findViewById(R.id.goal_enddate_edittext)).getText().toString();
        if(endDate == null) {
            return "";
        }
        return endDate;
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
        //mContext = getActivity();
        cursor.moveToFirst();
        String mGoalNameText = cursor.getString(COLUMN_GOALNAME);
        String mGoalCommentsText = cursor.getString(COLUMN_COMMENTS);
        String mGoalOccurrencesText = cursor.getString(COLUMN_OCCURRENCES);
        String mGoalTimeframeText = cursor.getString(COLUMN_TIMEFRAME);
        String mGoalStartDateText = cursor.getString(COLUMN_STARTDATE);
        String mGoalEndDateText = cursor.getString(COLUMN_ENDDATE);

        Date startDate;
        Date endDate;
        try {
            startDate = dbFormat.parse(mGoalStartDateText);
            endDate = dbFormat.parse(mGoalEndDateText);
        } catch(ParseException e){
            e.printStackTrace();
            startDate = new Date();
            endDate = new Date();
        }


        mEditGoalTitle = ((TextView) getActivity().findViewById(R.id.goal_title_editbox));
        mEditGoalComments = ((TextView) getActivity().findViewById(R.id.goal_comments_editbox));
        mEditGoalOccurrences = ((TextView) getActivity().findViewById(R.id.goal_occurrences_editbox));
        mEditGoalTimeframe = ((TextView) getActivity().findViewById(R.id.goal_timeframe_editbox));
        mEditGoalStartDate = ((TextView) getActivity().findViewById(R.id.goal_startdate_edittext));
        mEditGoalEndDate = ((TextView) getActivity().findViewById(R.id.goal_enddate_edittext));

        setUpDateDialogs();

        mEditGoalTitle.setText(mGoalNameText);
        mEditGoalComments.setText(mGoalCommentsText);
        mEditGoalTimeframe.setText(mGoalTimeframeText);
        mEditGoalOccurrences.setText(mGoalOccurrencesText);
        mEditGoalStartDate.setText(mGoalStartDateText);
        mEditGoalEndDate.setText(mGoalEndDateText);

    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void setUpDateDialogs(){
        //setup date dialogs
        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog startDateDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mEditGoalStartDate.setText(dbFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        final DatePickerDialog endDateDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mEditGoalEndDate.setText(dbFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        mEditGoalStartDate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String dateString = mEditGoalStartDate.getText().toString();
                Calendar date = Calendar.getInstance();
                try{
                    date.setTime(dbFormat.parse(dateString));
                } catch(ParseException e){
                    e.printStackTrace();
                }

                startDateDialog.updateDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH), Calendar.DAY_OF_MONTH);
                startDateDialog.show();
            }
        });

        mEditGoalEndDate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String dateString = mEditGoalEndDate.getText().toString();
                Calendar date = Calendar.getInstance();
                try{
                    date.setTime(dbFormat.parse(dateString));
                } catch(ParseException e){
                    e.printStackTrace();
                }

                endDateDialog.updateDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH), Calendar.DAY_OF_MONTH);
                endDateDialog.show();
            }
        });
    }

}
