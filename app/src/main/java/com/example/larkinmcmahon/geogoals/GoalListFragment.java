package com.example.larkinmcmahon.geogoals;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ContextMenu.ContextMenuInfo;

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
    private static int contextMenuItemIDSelected;

    public GoalListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();

        mGoalStrings = new ArrayList<String>();

        mGoals = ((GoalList) mContext).getGoals();

        ArrayList<Goal> mListOfGoals = new ArrayList<Goal>();

        for(int i = 0; i < mGoals.size(); i++){
            Goal goal = mGoals.get(i);
            mGoalStrings.add(goal.getTitle());
            mListOfGoals.add(goal);
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

        checkForIncomingIntents();
        final View rootView = inflater.inflate(R.layout.fragment_goal_list, container, false);

        ListView goal_list = (ListView) rootView.findViewById(R.id.fragment_goal_listview);

        goal_list.addFooterView(mAddGoalButton);

//        goal_list.setAdapter(mGoalAdapter);
        goal_list.setAdapter(new MainCustomListAdapter(mContext, mListOfGoals));
        goal_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedGoalTitle = mGoalAdapter.getItem(position);
                Goal selectedGoal = new Goal();

                for (int i = 0; i < mGoals.size(); i++) {
                    Goal goal = mGoals.get(i);
                    if (goal.getTitle() == selectedGoalTitle) {
                        selectedGoal = goal;
                    }
                }

                if (selectedGoal != null) {
                    Intent intent = new Intent(getActivity(), GoalDetail.class)
                            //.putExtra("selectedGoal", (Parcelable) selectedGoal)
                            .putExtra("dbID", selectedGoal.getID());
                    startActivity(intent);
                } else {
                    Log.e(TAG,"Could not find selected Goal from Adapter");
                }
            }
        });
        registerForContextMenu(goal_list);
        return rootView;

    }

    public void checkForIncomingIntents() {
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("deleting")) {
            int deleteVal = intent.getIntExtra("deleting",-1);
            if(deleteVal == 1){
                Toast.makeText(getActivity(),
                        "Goal Deleted Successfully", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getActivity(),
                        "Goal Not Deleted", Toast.LENGTH_LONG).show();
            }
        }
    }
    public int getIdFromTitle(String title) {
        for(int a = 0; a < mGoals.size(); a++){
            Goal currentGoal = mGoals.get(a);
            if(currentGoal.getTitle() == title) {
                return currentGoal.getID();
            }
        }
        return -1;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.fragment_goal_listview) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(mGoalStrings.get(info.position));
            String[] menuItems = getResources().getStringArray(R.array.activity_main_context_menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        Intent intent;
        int dbid;
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.activity_main_context_menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = mGoalStrings.get(info.position);
        switch(menuItemName) {
            case "Edit":
                dbid = getIdFromTitle(listItemName);
                intent = new Intent(getActivity(), GoalEdit.class)
                        .putExtra("dbid", dbid);
                startActivity(intent);
                break;
            case "Delete":
                dbid = getIdFromTitle(listItemName);
                intent = new Intent(getActivity(), GoalDelete.class)
                        .putExtra("dbid", dbid);
                startActivity(intent);
                break;
        }
//        TextView text = (TextView)findViewById(R.id.footer);
//        text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));
        return true;
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
