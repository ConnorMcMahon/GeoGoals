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
    private static final int COLUMN_CURRENTOCCURRENCES = 9;
    private static final int COLUMN_OCCURRENCES = 2;


    public static class ViewHolder {
        public final TextView headlineView;
        public final TextView occurrenceRatio;


        public ViewHolder(View view) {
            headlineView = (TextView) view.findViewById(R.id.title);

            occurrenceRatio = (TextView) view.findViewById(R.id.occurrence_ratio);

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

        viewHolder.headlineView.setText(goalTitle);
        viewHolder.occurrenceRatio.setText(currentOccurrences + "/" + targetOccurences);

    }
}