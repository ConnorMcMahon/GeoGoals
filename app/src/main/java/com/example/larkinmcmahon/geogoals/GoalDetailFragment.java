package com.example.larkinmcmahon.geogoals;

import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class GoalDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {
    public static Goal goalSelected;
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
            GoalDatabaseHelper.KEY_CURRENTOCCURENCES,
            GoalDatabaseHelper.KEY_CATEGORY
    };

    //correlate with GOAL_DETAIL_COLUMNS
    private static final int COLUMN_GOALNAME = 1;
    private static final int COLUMN_OCCURRENCES = 2;
    private static final int COLUMN_TIMEFRAME = 3;
    private static final int COLUMN_COMMENTS = 4;
    private static final int COLUMN_STARTDATE = 5;
    private static final int COLUMN_ENDDATE = 7;
    private static final int COLUMN_CATEGORY = 10;

    private TextView mGoalNameTextView;
    private TextView mEditGoalComments;
    private TextView mEditGoalOccurrences;
    private TextView mEditGoalTimeframe;
    private TextView mEditGoalStartDate;
    private TextView mEditGoalEndDate;
    private TextView mEditGoalCategory;
    private ImageView mEditGoalCategoryImage;

    public GoalDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_goal_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            if(intent.hasExtra("intentType")) {
                if (intent.getStringExtra("intentType").equals("SQLUpdate")) {
                    if (intent.getStringExtra("intentMsg").equals("Success")) {
                        Toast.makeText(getActivity(),
                                "Goal Updated Successfully", Toast.LENGTH_LONG).show();
                    } else if (intent.getStringExtra("intentMsg").equals("Failure")) {
                        Toast.makeText(getActivity(),
                                "Goal Not Updated", Toast.LENGTH_LONG).show();
                    }
                }
            }
            if (intent.hasExtra("dbID")) {
                mDbID = intent.getIntExtra("dbID", -1);
            }
        }
        else {
            mDbID = getArguments().getInt("dbid");
        }
        //getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//        if((mDbID == -1 && getArguments() != null && getArguments().containsKey("dbid")) || (getArguments() != null && getArguments().containsKey("reload") && getArguments().getInt("reload") == 1)) {
          if( getArguments() != null && getArguments().containsKey("dbid")) {
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
        String mGoalStartDateText = cursor.getString(COLUMN_STARTDATE);
        String mGoalEndDateText = cursor.getString(COLUMN_ENDDATE);
        int mCategory = cursor.getInt(COLUMN_CATEGORY);

        mGoalNameTextView = (TextView) getActivity().findViewById(R.id.fragment_goal_detail_title_text);
        mEditGoalComments = ((TextView) getActivity().findViewById(R.id.fragment_goal_detail_comment_text));
        mEditGoalOccurrences = ((TextView) getActivity().findViewById(R.id.fragment_goal_detail_occurances_text));
        mEditGoalTimeframe = ((TextView) getActivity().findViewById(R.id.fragment_goal_detail_timeframe_text));
        mEditGoalStartDate = ((TextView) getActivity().findViewById(R.id.fragment_goal_detail_startdate_text));
        mEditGoalEndDate = ((TextView) getActivity().findViewById(R.id.fragment_goal_detail_enddate_text));
        mEditGoalCategory = ((TextView) getActivity().findViewById(R.id.fragment_goal_detail_category_text));
        mEditGoalCategoryImage = ((ImageView) getActivity().findViewById(R.id.fragment_goal_detail_category_image));

        mGoalNameTextView.setText(mGoalNameText);
        mEditGoalComments.setText(mGoalCommentsText);
        mEditGoalOccurrences.setText(mGoalOccurrencesText);
        mEditGoalTimeframe.setText(mGoalTimeframeText);
        mEditGoalStartDate.setText(mGoalStartDateText);
        mEditGoalEndDate.setText(mGoalEndDateText);
        mEditGoalCategory.setText("" + mCategory);

        switch(mCategory) {
            case 1:
                mEditGoalCategoryImage.setImageResource(R.mipmap.category_gym);
                break;
            case 2:
                mEditGoalCategoryImage.setImageResource(R.mipmap.category_school);
                break;
            case 3:
                mEditGoalCategoryImage.setImageResource(R.mipmap.category_other);
                break;
            default:
                mEditGoalCategoryImage.setImageResource(R.mipmap.ic_launcher);
        }

    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

}