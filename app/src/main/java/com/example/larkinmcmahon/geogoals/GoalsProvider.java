package com.example.larkinmcmahon.geogoals;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

public class GoalsProvider extends ContentProvider {

    private GoalDatabaseHelper mDB;

    private static final String AUTHORITY = "com.example.larkinmcmahon.geogoals";
    public static final int GOALS = 100;
    public static final int GOALS_ID = 110;
    public static final int LOCATIONS = 200;
    public static final int LOCATIONS_ID = 210;

    private static final String GOALS_BASE_PATH = "goals";
    private static final String LOCATION_BASE_PATH = "location";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + GOALS_BASE_PATH);
    public static final Uri LOCATION_URI = Uri.parse("content://" + AUTHORITY
            + "/" + LOCATION_BASE_PATH);

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/goals";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/goals";
    public static final String LOCATION_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/location";
    public static final String LOCATION_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/location";


    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, GOALS_BASE_PATH, GOALS);
        sURIMatcher.addURI(AUTHORITY, GOALS_BASE_PATH + "/#", GOALS_ID);
        sURIMatcher.addURI(AUTHORITY, LOCATION_BASE_PATH, LOCATIONS);
        sURIMatcher.addURI(AUTHORITY, LOCATION_BASE_PATH + "/#", LOCATIONS_ID);
    }

    @Override
    public boolean onCreate() {
        mDB = new GoalDatabaseHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case GOALS_ID:
                queryBuilder.setTables(GoalDatabaseHelper.TABLE_Goals);
                queryBuilder.appendWhere(GoalDatabaseHelper.KEY_ID + "="
                        + uri.getLastPathSegment());
                break;
            case GOALS:
                queryBuilder.setTables(GoalDatabaseHelper.TABLE_Goals);
                break;
            case LOCATIONS:
                queryBuilder.setTables(GoalDatabaseHelper.TABLE_LOCATIONS);
                break;
            case LOCATIONS_ID:
                queryBuilder.setTables(GoalDatabaseHelper.TABLE_LOCATIONS);
                queryBuilder.appendWhere(GoalDatabaseHelper.KEY_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
                //TODO: put cases in for location table
        }

        Cursor cursor = queryBuilder.query(mDB.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        int rowsAffected = 0;
        switch (uriType) {
            case GOALS:
                rowsAffected = sqlDB.delete(GoalDatabaseHelper.TABLE_Goals,
                        selection, selectionArgs);
                break;
            case GOALS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = sqlDB.delete(GoalDatabaseHelper.TABLE_Goals,
                            GoalDatabaseHelper.KEY_ID + "=" + id, null);
                } else {
                    rowsAffected = sqlDB.delete(GoalDatabaseHelper.TABLE_Goals,
                            selection + " and " + GoalDatabaseHelper.KEY_ID + "=" + id,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public String getType(Uri uri) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case GOALS:
                return CONTENT_TYPE;
            case GOALS_ID:
                return CONTENT_ITEM_TYPE;
            case LOCATIONS:
                return LOCATION_TYPE;
            case LOCATIONS_ID:
                return LOCATION_ITEM_TYPE;
            default:
                return null;
        }
    }

    public Uri addGoal(Uri uri, Goal goal) {
        int uriType = sURIMatcher.match(uri);
        if (uriType != GOALS) {
            throw new IllegalArgumentException("Invalid URI for insert");
        }
//        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        long newID = mDB.addGoals(goal);
        if (newID > 0) {
            Uri newUri = ContentUris.withAppendedId(uri, newID);
            getContext().getContentResolver().notifyChange(uri, null);
            return newUri;
        } else {
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        if(method.equals("addGoals")) {
            Goal goal = extras.getParcelable("goal");
            Uri returnedUri = addGoal(CONTENT_URI, goal);
            Bundle returnBundle = new Bundle();
            returnBundle.putParcelable("returnedUri", (Parcelable)returnedUri);
        }
        return null;
    }
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        long newID;
        if (uriType != GOALS && uriType != LOCATIONS) {
            throw new IllegalArgumentException("Invalid URI for insert");
        }
        else if(uriType == GOALS) {
            SQLiteDatabase sqlDB = mDB.getWritableDatabase();
            newID = sqlDB
                    .insert(GoalDatabaseHelper.TABLE_Goals, null, values);
        }
        else if(uriType == LOCATIONS) {
            SQLiteDatabase sqlDB = mDB.getWritableDatabase();
            newID = sqlDB
                    .insert(GoalDatabaseHelper.TABLE_LOCATIONS, null, values);
        }
        else {
            throw new SQLException("Failed to insert row into " + uri);
        }
        Uri newUri = ContentUris.withAppendedId(uri, newID);
        getContext().getContentResolver().notifyChange(uri, null);
        return newUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();

        int rowsAffected;

        switch (uriType) {
            case GOALS_ID:
                String id = uri.getLastPathSegment();
                StringBuilder modSelection = new StringBuilder(GoalDatabaseHelper.KEY_ID
                        + "=" + id);

                if (!TextUtils.isEmpty(selection)) {
                    modSelection.append(" AND " + selection);
                }

                rowsAffected = sqlDB.update(GoalDatabaseHelper.TABLE_Goals,
                        values, modSelection.toString(), null);
                break;
            case GOALS:
                rowsAffected = sqlDB.update(GoalDatabaseHelper.TABLE_Goals,
                        values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }
}