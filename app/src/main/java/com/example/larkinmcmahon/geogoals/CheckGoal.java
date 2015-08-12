package com.example.larkinmcmahon.geogoals;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CheckGoal extends IntentService {

    public CheckGoal() {
        super("CheckGoal");
    }

    protected void onHandleIntent(Intent intent) {
        int id = intent.getIntExtra("dbid", -1);
        if(id != -1){
            GoalDatabaseHelper dbHelper = new GoalDatabaseHelper(getApplicationContext());
            Goal goal = dbHelper.getGoal(id);
            if(goal.getCurrentOccurences() >= goal.getOccurance()){
                //succeeded!
            } else {
                //failed!
            }

            AlarmManager mgr=
                    (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Intent newIntent = new Intent(getApplicationContext(), CheckGoal.class);
            newIntent.putExtra("dbid", id);
            PendingIntent alarm = PendingIntent.getService(getApplicationContext(), 0, newIntent, 0);

            Calendar today = Calendar.getInstance();

            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            today.add(Calendar.DATE, goal.getTimeFrame());

            mgr.set(AlarmManager.RTC_WAKEUP, today.getTimeInMillis(), alarm);

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }




}
