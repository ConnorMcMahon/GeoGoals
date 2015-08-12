package com.example.larkinmcmahon.geogoals;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by djflash on 8/4/15.
 */
public class GoalDelete extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private int mDbID = -1;
    private static int LOADER_ID = 2;
    private Cursor mCursor;
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
            GoalDatabaseHelper.KEY_CURRENTOCCURENCES
    };

    //correlate with GOAL_DETAIL_COLUMNS
    private static final int COLUMN_CURRENTOCCURENCES = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_goal);

        Intent intent = getIntent();
        if(intent.hasExtra("dbid")) {
            mDbID = intent.getIntExtra("dbid",-1);
        }
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    public void onClickConfirmDelete(View view) {
        int currentOcc = mCursor.getInt(COLUMN_CURRENTOCCURENCES);

        String projection[] = {GoalDatabaseHelper.KEY_CURRENTOCCURENCES };
        ContentValues values = new ContentValues();
        values.put(GoalDatabaseHelper.KEY_CURRENTOCCURENCES, currentOcc - 1);
        int deleteStatus = getContentResolver().delete(
                Uri.withAppendedPath(GoalsProvider.CONTENT_URI,
                        String.valueOf(mDbID)), null, null);

        Intent intent = new Intent(this, GoalList.class)
                .putExtra("deleting", deleteStatus);
        startActivity(intent);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Intent intent = getIntent();
        if(mDbID == -1 && intent.hasExtra("dbid")) {
            mDbID = intent.getIntExtra("dbid",-1);
        }
        if(mDbID != -1) {
            return new CursorLoader(this,
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
        mCursor = cursor;
    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
