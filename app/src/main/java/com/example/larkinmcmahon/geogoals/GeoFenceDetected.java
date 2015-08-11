package com.example.larkinmcmahon.geogoals;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * Created by djflash on 8/11/15.
 */
public class GeoFenceDetected extends AppCompatActivity/*FragmentActivity*/ implements LoaderManager.LoaderCallbacks<Cursor>{
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
        setContentView(R.layout.activity_geofence_detected);

        Intent intent = getIntent();
        if(intent.hasExtra("dbID")) {
            mDbID = intent.getIntExtra("dbID",-1);
        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    public void incorrectlyDetected(View view) {

        int currentOcc = mCursor.getInt(COLUMN_CURRENTOCCURENCES);

        String projection[] = {GoalDatabaseHelper.KEY_CURRENTOCCURENCES };
        ContentValues values = new ContentValues();
        values.put(GoalDatabaseHelper.KEY_CURRENTOCCURENCES, currentOcc - 1);
        int updateResult = getContentResolver().update(
                Uri.withAppendedPath(GoalsProvider.CONTENT_URI,
                        String.valueOf(mDbID)),values,null,projection);

        if(updateResult > 0) {
            Toast.makeText(this,
                    "Goal Fixed Successfully", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this,
                    "Goal Not Fixed. Please try again.", Toast.LENGTH_LONG).show();
        }
    }


    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
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
