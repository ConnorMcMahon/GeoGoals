package com.example.larkinmcmahon.geogoals;

        import android.content.Context;
        import android.database.Cursor;
        import android.graphics.Picture;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.CursorAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

/**
 * Created by djflash on 8/7/15.
 */
public class GeoDonationListCursorAdapter extends CursorAdapter {
    private static final int COLUMN_CHARITY = 1;
    private static final int COLUMN_URL = 2;

    public static class ViewHolder {
        public final TextView charityName;
        public final ImageView charity_logo;

        public ViewHolder(View view) {
            charity_logo = (ImageView) view.findViewById(R.id.charity_logo);
            charityName = (TextView) view.findViewById(R.id.charity_name);
        }
    }

    public GeoDonationListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int layoutId = R.layout.list_geodonations_custom;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String charityTitle = cursor.getString(COLUMN_CHARITY);

        viewHolder.charityName.setText(charityTitle);
        switch(charityTitle) {
            case "American Heart Association":
                viewHolder.charity_logo.setImageResource(R.mipmap.americanheartassoc_logo);
                break;
            case "The V Foundation For Cancer Research":
                viewHolder.charity_logo.setImageResource(R.mipmap.thevfoundation_logo);
                break;
            case "Paralyzed Veterans of America":
                viewHolder.charity_logo.setImageResource(R.mipmap.paralyzedverteransofamerica_logo);
                break;
            case "Feeding America":
                viewHolder.charity_logo.setImageResource(R.mipmap.feedingamerica_logo);
                break;
            case "American National Red Cross":
                viewHolder.charity_logo.setImageResource(R.mipmap.americanredcross_logo);
                break;
            default:
                viewHolder.charity_logo.setImageResource(R.mipmap.ic_launcher);
                break;
        }
    }
}