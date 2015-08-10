package com.example.larkinmcmahon.geogoals;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class GoalDetailFragment extends Fragment{
    public static Goal goalSelected;

    public GoalDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_goal_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            if(intent.hasExtra("intentType")) {
                if (intent.getStringExtra("intentType").equals("SQLUpdate")) {
                    if (intent.getStringExtra("intentMsg").equals("Success")) {
                        Toast.makeText(getActivity(),
                                "Goal Updated Successfully", Toast.LENGTH_LONG).show();
                    } else if (intent.getStringExtra("intentMsg").equals("Failure")) {
                        Toast.makeText(getActivity(),
                                "Goal Not Updated", Toast.LENGTH_LONG).show();
                    }
                }
            }
            //if(intent.hasExtra("selectedGoal")) {
            //    Goal currentGoal = (Goal)intent.getSerializableExtra("selectedGoal");
//                ((TextView) rootView.findViewById(R.id.activity_goal_detail_title_text))
                        //.setText(currentGoal.getTitle());

//                goalSelected = currentGoal;
//                int idnum = currentGoal.getOverallID();
//                Log.d("GoalDetailFragmentIdVal", String.valueOf(idnum));
            //}
            if(intent.hasExtra("dbID")) {
                String projection[] = { GoalDatabaseHelper.KEY_GOALNAME };
                int dbid = intent.getIntExtra("dbID", 0);
                Cursor cursor = getActivity().getContentResolver().query(
                        Uri.withAppendedPath(GoalsProvider.CONTENT_URI,
                                            String.valueOf(dbid)),projection,null,null,null);
                if(cursor.moveToFirst()) {
                    String goalName = cursor.getString(0);
                    ((TextView) rootView.findViewById(R.id.fragment_goal_detail_title_text))
                        .setText(goalName);
                }
                cursor.close();


//                GoalDatabaseHelper db = new GoalDatabaseHelper(getActivity().getApplicationContext());
//                int dbid = intent.getIntExtra("dbID", 0);
//                goalSelected = db.getGoal(dbid);
//                ((TextView) rootView.findViewById(R.id.fragment_goal_detail_title_text))
//                    .setText(goalSelected.getTitle());
//
//                int idnum = goalSelected.getOverallID();
//                Log.d("GoalDetailFragmentIdVal", String.valueOf(idnum));
            }
        }

        return rootView;
    }

}
