package com.example.larkinmcmahon.geogoals;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by djflash on 8/7/15.
 */
public class GoalListCursorAdapter extends CursorAdapter {
    private static final int COLUMN_GOALNAME = 1;
    private static final int COLUMN_CURRENTOCCURRENCES = 9;
    private static final int COLUMN_OCCURRENCES = 2;
    private static final int COLUMN_COMMENTS = 4;
    private static final int COLUMN_STARTDATE = 5;
    private static final int COLUMN_TIMEFRAME = 3;


    public static class ViewHolder {
        public final TextView headlineView;
        public final TextView occurrenceRatio;
        public final TextView comment;
        public final TextView deadline;


        public ViewHolder(View view) {
            headlineView = (TextView) view.findViewById(R.id.title);

            occurrenceRatio = (TextView) view.findViewById(R.id.occurrence_ratio);
            comment = (TextView) view.findViewById(R.id.itemview_comment);
            deadline = (TextView) view.findViewById(R.id.itemview_deadline);

        }
    }

    public GoalListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int layoutId = R.layout.list_custom_main;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String goalTitle = cursor.getString(COLUMN_GOALNAME);
        String currentOccurrences = cursor.getString(COLUMN_CURRENTOCCURRENCES);
        String targetOccurences = cursor.getString(COLUMN_OCCURRENCES);
        String comment = cursor.getString(COLUMN_COMMENTS);
        int timeframe = cursor.getInt(COLUMN_TIMEFRAME);
        String dateString = cursor.getString(COLUMN_STARTDATE);

        int deadline = calculateDeadline(dateString, timeframe);

        viewHolder.headlineView.setText(goalTitle);
        viewHolder.occurrenceRatio.setText(currentOccurrences + "/" + targetOccurences + " times!");
        viewHolder.comment.setText(comment);
        viewHolder.deadline.setText(timeframe + " days left");


    }

    public static int calculateDeadline(String dateString, int timeframe){
        Calendar currentDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        try{
            startDate.setTime(GoalEditFragment.dbFormat.parse(dateString));
        } catch (ParseException e){
            e.printStackTrace();
        }

        startDate.add(Calendar.DATE, timeframe);
        while(startDate.compareTo(currentDate) <= 0) {
            startDate.add(Calendar.DATE, timeframe);
        }

        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);

        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MILLISECOND, 0);

        return (int) TimeUnit.MILLISECONDS.toDays(
                Math.abs(currentDate.getTimeInMillis() - startDate.getTimeInMillis()));


    }
}