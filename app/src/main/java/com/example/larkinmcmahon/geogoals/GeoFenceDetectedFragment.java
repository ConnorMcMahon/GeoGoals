package com.example.larkinmcmahon.geogoals;

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
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by djflash on 8/11/15.
 */
public class GeoFenceDetectedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private int mDbID = -1;
    private static int LOADER_ID = 2;
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

    private TextView mTitleText;

    public GeoFenceDetectedFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_geofence_detected, container, false);

        Intent intent = getActivity().getIntent();
        if(intent.hasExtra("dbID")) {
            mDbID = intent.getIntExtra("dbID",-1);
        }

        mTitleText = (TextView) rootView.findViewById(R.id.geofence_detected_text);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if(mDbID == -1 && getArguments() != null && getArguments().containsKey("dbID")) {
            mDbID = getArguments().getInt("dbID");
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

        mTitleText.setText("We've detected you entered a location for for '"+mGoalNameText+".' Please either confirm or deny this is true.");
    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

}
