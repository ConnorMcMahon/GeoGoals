package com.example.larkinmcmahon.geogoals;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.location.Geofence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class GoalListFragment extends Fragment {
    private Context mContext;
    private List<Goal> mGoals;
    private List<String> mGoalStrings;
    private ArrayAdapter<String> mGoalAdapter;
    private Button mAddGoalButton;
    private String TAG = "GOAL_LIST_FRAGMENT";

    public GoalListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();

        mGoalStrings = new ArrayList<String>();

        mGoals = ((GoalList) mContext).getGoals();

        for(int i = 0; i < mGoals.size(); i++){
            Goal goal = mGoals.get(i);
            mGoalStrings.add(goal.getTitle());
        }

        mGoalAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_goal,
                R.id.list_item_goal_textview,
                mGoalStrings
        );

        mAddGoalButton = new Button(getActivity());

        mAddGoalButton.setText(R.string.new_goal_button);
        mAddGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GoalList) getActivity()).addGoalButtonClick();
            }
        });


        View rootView = inflater.inflate(R.layout.fragment_goal_list, container, false);

        ListView goal_list = (ListView) rootView.findViewById(R.id.fragment_goal_listview);


        goal_list.addFooterView(mAddGoalButton);

        goal_list.setAdapter(mGoalAdapter);
        goal_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedGoalTitle = mGoalAdapter.getItem(position);
                Goal selectedGoal = new Goal();

                for(int i = 0; i < mGoals.size(); i++){
                    Goal goal = mGoals.get(i);
                    if(goal.getTitle() == selectedGoalTitle) {
                        selectedGoal = goal;
                    }
                }

                if(selectedGoal != null){
                    Intent intent = new Intent(getActivity(), GoalDetail.class)
                            .putExtra("selectedGoal", (Parcelable) selectedGoal)
                            .putExtra("dbID", selectedGoal.getID());
                    startActivity(intent);
                }
                else{
                    Log.e(TAG,"Could not find selected Goal from Adapter");
                }
            }
        });
        return rootView;

    }
    @Override
    public void onResume(){
        super.onResume();
        mGoals = ((GoalList) mContext).getGoals();
        updateListView(mGoals);
    }

    public void updateListView(List<Goal> newGoals){
        Log.i(TAG, "updating List View");
        for(int i = 0; i < mGoalStrings.size(); i++){
            mGoalStrings.remove(0);
        }
        for(int a = 0; a < newGoals.size(); a++){
            mGoalStrings.add(newGoals.get(a).getTitle());
        }
        mGoalAdapter.notifyDataSetChanged();
    }
}
