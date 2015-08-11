package com.example.larkinmcmahon.geogoals;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by djflash on 8/11/15.
 */
public class GeoDonationListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private Context mContext;
    private List<Goal> mGoals;
    private String TAG = "GOAL_LIST_FRAGMENT";
    private GeoDonationListCursorAdapter mGoalListAdapter;
    private static int LOADER_ID = 2;

    private static final String[] GOAL_DETAIL_COLUMNS = {
            GoalDatabaseHelper.KEY_ID,
            GoalDatabaseHelper.KEY_CHARITY,
            GoalDatabaseHelper.KEY_URL
    };

    private static final int COLUMN_CharityURL = 2;

    public GeoDonationListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();

        final View rootView = inflater.inflate(R.layout.fragment_goal_donation_list, container, false);

        ListView donation_list = (ListView) rootView.findViewById(R.id.fragment_goal_donation_listview);


        mGoalListAdapter = new GeoDonationListCursorAdapter(getActivity(),null,0);
        donation_list.setAdapter(mGoalListAdapter);
        donation_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                String charity_url = cursor.getString(COLUMN_CharityURL);

                Intent intent = new Intent(getActivity(), DonationWebViewActivity.class)
                    .putExtra("url", charity_url);
                startActivity(intent);
            }
        });
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
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }



    @Override
    public void onResume() {
        super.onResume();
        // mGoals = ((GoalList) mContext).getGoals();
        //  updateListView(mGoals);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Intent intent = getActivity().getIntent();
        return new CursorLoader(getActivity(),
                GoalsProvider.DONATIONS_URI,
                GOAL_DETAIL_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mGoalListAdapter.swapCursor(data);
        //TODO: Add in saveInstanceState methods to save selected Goal on app rebuilds - see Sunshine example

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mGoalListAdapter.swapCursor(null);
    }
}