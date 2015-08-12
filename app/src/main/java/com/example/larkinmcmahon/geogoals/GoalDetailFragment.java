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
    };

    //correlate with GOAL_DETAIL_COLUMNS
    private static final int COLUMN_GOALNAME = 1;

    private TextView mGoalNameTextView;

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
        mGoalNameTextView = (TextView) rootView.findViewById(R.id.fragment_goal_detail_title_text);
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

        mGoalNameTextView.setText(mGoalNameText);

    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

}