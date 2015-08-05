package com.example.larkinmcmahon.geogoals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    //  table name
    private static final String TABLE_Goals = "goals";

    //  Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_GOALNAME = "name";
    private static final String KEY_OCCURANCES = "occurances";
    private static final String KEY_TIMEFRAME = "timeFrame";
    private static final String KEY_COMMENTS = "comments";

    public GoalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_GOALS_TABLE = "CREATE TABLE " + TABLE_Goals + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_GOALNAME + " TEXT,"
                + KEY_OCCURANCES + " TEXT,"
                + KEY_TIMEFRAME + " TEXT,"
                + KEY_COMMENTS + " TEXT" + ")";
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
    void addGoals(Goal goal) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, goal.getID());
        values.put(KEY_GOALNAME, goal.getTitle()); // Name
        values.put(KEY_OCCURANCES, goal.getOccurance());
        values.put(KEY_TIMEFRAME, goal.getTimeFrame());
        values.put(KEY_COMMENTS, goal.getComments());

        // Inserting Row
        db.insert(TABLE_Goals, null, values);
        db.close(); // Closing database connection
    }

    // Getting single Goal
    Goal getGoal(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_Goals,
                new String[] { KEY_ID, KEY_GOALNAME, KEY_OCCURANCES, KEY_TIMEFRAME, KEY_COMMENTS },
                KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Goal goal = new Goal(cursor.getInt(0),
                cursor.getString(1),
                null,
                null,
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getString(4)
        );
        return goal;
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
                Goal goal = new Goal(cursor.getInt(0),
                                     cursor.getString(1),
                                     null,
                                     null,
                                     cursor.getInt(2),
                                     cursor.getInt(3),
                                     cursor.getString(4)
                                     );
                int idnum = goal.getOverallID();
                Log.d("GOALDBHELPER",String.valueOf(idnum));
//                goal.setTitle(cursor.getString(1));
//                goal.setOccurance(cursor.getInt(2));
//                goal.setTimeFrame(cursor.getInt(3));
//                goal.setComments(cursor.getString(4));

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
    public void deleteGoal(Goal goal) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_Goals, KEY_ID + " = ?",
                new String[]{String.valueOf(goal.getID()) });
        db.close();
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
