package com.example.larkinmcmahon.geogoals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by djflash on 8/2/15.
 */
public class GoalDatabaseHelper extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "goalsManagaer";

    //  table names
    public static final String TABLE_Goals = "goals";
    public static final String TABLE_LOCATIONS = "locationInformation";

    //  Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_GOALNAME = "name";
    public static final String KEY_OCCURANCES = "occurances";
    public static final String KEY_TIMEFRAME = "timeFrame";
    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_STARTDATE = "startDate";
    public static final String KEY_ENDDATE = "endDate";
    public static final String KEY_STARTTIME = "startTime";
    public static final String KEY_ENDTIME = "endTime";

    public static final String KEY_COORID = "coorID";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LONG = "long";
    public static final String KEY_RADII = "radii";

    public GoalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_GOALS_TABLE = "CREATE TABLE " + TABLE_Goals + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_GOALNAME + " TEXT,"
                + KEY_OCCURANCES + " TEXT,"
                + KEY_TIMEFRAME + " TEXT,"
                + KEY_COMMENTS + " TEXT,"
                + KEY_STARTDATE + " TEXT,"
                + KEY_ENDDATE + " TEXT,"
                + KEY_STARTTIME + " TEXT,"
                + KEY_ENDTIME + " TEXT" + ")";

        String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_COORID + " INTEGER,"
                + KEY_LAT + " REAL,"
                + KEY_LONG + " REAL,"
                + KEY_RADII + " INTEGER" + ")";

        db.execSQL(CREATE_LOCATIONS_TABLE);
        db.execSQL(CREATE_GOALS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Goals);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new Goals
    long addGoals(Goal goal) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues goalValues = new ContentValues();
        ContentValues locationValues = new ContentValues();
        List<LatLng> locations = goal.getLocations();
        List<Integer> radii = goal.getRadii();
        int id = goal.getID();

        //goals table
        goalValues.put(KEY_ID, id);
        goalValues.put(KEY_GOALNAME, goal.getTitle()); // Name
        goalValues.put(KEY_OCCURANCES, goal.getOccurance());
        goalValues.put(KEY_TIMEFRAME, goal.getTimeFrame());
        goalValues.put(KEY_COMMENTS, goal.getComments());
        goalValues.put(KEY_STARTDATE, goal.getStartDate());
        goalValues.put(KEY_ENDDATE, goal.getEndDate());
        goalValues.put(KEY_STARTTIME, goal.getStartTime());
        goalValues.put(KEY_ENDTIME, goal.getEndTime());

        // Inserting Row
        long insertVal = db.insert(TABLE_Goals, null, goalValues);

        //location table
        for(int i = 0; i < locations.size(); i++){
            locationValues.put(KEY_COORID, id);
            locationValues.put(KEY_LAT, locations.get(i).latitude);
            locationValues.put(KEY_LONG, locations.get(i).longitude);
            locationValues.put(KEY_RADII, radii.get(i));

            // Inserting Row
            long insertLocationVal = db.insert(TABLE_LOCATIONS, null, locationValues);
        }

        db.close(); // Closing database connection
        return insertVal;
    }

    // Getting single Goal
    Goal getGoal(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_Goals,
                new String[] { KEY_ID, KEY_GOALNAME, KEY_OCCURANCES, KEY_TIMEFRAME, KEY_COMMENTS, KEY_STARTDATE, KEY_ENDDATE, KEY_STARTTIME, KEY_ENDTIME },
                KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        List<Object> locationInformation = getGoalLocationInfo(db, id);
        Goal goal = new Goal(
                cursor.getInt(0),
                cursor.getString(1),
                (List<LatLng>)locationInformation.get(0),
                (List<Integer>)locationInformation.get(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8)
        );
        return goal;
    }

    private List<Object> getGoalLocationInfo(SQLiteDatabase db, int id) {
        List<Object> returnLists = new ArrayList<Object>();
        List<LatLng> locations = new ArrayList<LatLng>();
        List<Integer> radii = new ArrayList<Integer>();

        Cursor cursor = db.query(TABLE_LOCATIONS,
                new String[]{KEY_COORID, KEY_LAT, KEY_LONG, KEY_RADII},
                KEY_COORID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                double lat = cursor.getDouble(1);
                double lon = cursor.getDouble(2);
                Integer radius = cursor.getInt(3);
                LatLng latlng = new LatLng(lat, lon);

                locations.add(latlng);
                radii.add(radius);
            } while (cursor.moveToNext());
        }
        returnLists.add(locations);
        returnLists.add(radii);
        return returnLists;
    }

    // Getting All Goals
    public ArrayList<Goal> getAllGoals() {
        ArrayList<Goal> goalList = new ArrayList<Goal>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_Goals;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                List<Object> locationInformation = getGoalLocationInfo(db, cursor.getPosition());
                Goal goal = new Goal(
                        cursor.getInt(0),
                        cursor.getString(1),
                        (List<LatLng>)locationInformation.get(0),
                        (List<Integer>)locationInformation.get(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8)
                );


//                        cursor.getString(1),
//                        null,
//                        null,
//                        cursor.getInt(2),
//                        cursor.getInt(3),
//                        cursor.getString(4)
//                );
//
                // Adding goal to list
                goalList.add(goal);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        db.close();
        // return goal list
        return goalList;
    }

    // Updating single Goal
    public int updateGoal(Goal goal) {
        SQLiteDatabase db = this.getWritableDatabase();
        int goalID = goal.getID();
        String strFilter = KEY_ID + " = " + goalID;

        ContentValues values = new ContentValues();
        values.put(KEY_GOALNAME, goal.getTitle());
        values.put(KEY_OCCURANCES, goal.getOccurance());
        values.put(KEY_TIMEFRAME, goal.getTimeFrame());
        values.put(KEY_COMMENTS, goal.getComments());

        // updating row
        int updateStatus = db.update(TABLE_Goals, values, KEY_ID + " = ?",
                new String[] { String.valueOf(goalID) });

        return updateStatus;
    }

    // Deleting single Goal
    public int deleteGoal(Goal goal) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleteStatus = db.delete(TABLE_Goals, KEY_ID + " = ?",
                new String[] { String.valueOf(goal.getID()) });
        db.close();
        return deleteStatus;
    }


    // Getting Goal Count
    public int getGoalCount() {
        String countQuery = "SELECT  * FROM " + TABLE_Goals;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }
}
