package com.example.larkinmcmahon.geogoals;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by djflash on 8/7/15.
 */
public class GoalListCursorAdapter extends CursorAdapter {
    private static final int COLUMN_GOALNAME = 1;
    private static final int COLUMN_STARTDATE = 5;
    private static final int COLUMN_STARTTIME = 6;

    public static class ViewHolder {
        public final TextView headlineView;
        public final TextView reporterNameView;
        public final TextView reportedDateView;

        public ViewHolder(View view) {
            headlineView = (TextView) view.findViewById(R.id.title);
            reporterNameView = (TextView) view.findViewById(R.id.reporter);
            reportedDateView = (TextView) view.findViewById(R.id.date);
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
        String startDate = cursor.getString(COLUMN_STARTDATE);
        String endDate = cursor.getString(COLUMN_STARTTIME);

        viewHolder.headlineView.setText(goalTitle);
        viewHolder.reporterNameView.setText(startDate);
        viewHolder.reportedDateView.setText(endDate);

    }
}