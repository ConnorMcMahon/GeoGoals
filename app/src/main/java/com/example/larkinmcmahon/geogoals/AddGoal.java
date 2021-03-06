package com.example.larkinmcmahon.geogoals;

import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class AddGoal extends ActionBarActivity {
    private ViewPager mViewPager;
    private AddGoalPagerAdapter mAddGoalPagerAdapter;
    private Context mContext = this;
    private String TAG = "ADD_GOAL";
    private Goal mGoal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);
        // Create a tab listener that is called when the user changes tabs.

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mAddGoalPagerAdapter = new AddGoalPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAddGoalPagerAdapter);

        String[] tabNames = getResources().getStringArray(R.array.add_goal_tabs);

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // When the tab is selected, switch to the
                // corresponding page in the ViewPager.
                mViewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft){

            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft){
                mViewPager.setCurrentItem(tab.getPosition());
            }
        };

        for(int i = 0; i < tabNames.length; i++){
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(tabNames[i])
                            .setTabListener(tabListener));
        }

        mGoal = new Goal("");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_goal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                return true;
            case R.id.submit_goal:
                onClickSubmitGoal();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public class AddGoalPagerAdapter extends FragmentPagerAdapter {
        public List<String> mFragments;
        public SparseArray<Fragment> mRegisteredFragments;

        public AddGoalPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragments = new ArrayList<String>();
            mRegisteredFragments = new SparseArray<Fragment>();
            mFragments.add(GoalEditFragment.class.getName());
            mFragments.add(GoalLocationFragment.class.getName());
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment =  Fragment.instantiate(mContext, mFragments.get(i));
            fragment.setRetainInstance(true);
            return fragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            mRegisteredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mRegisteredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        public Fragment getRegisteredFragment(int position) {
            return mRegisteredFragments.get(position);
        }

    }

    public void onClickSubmitGoal(){
        Intent intent = new Intent();
        FragmentManager fm = getSupportFragmentManager();
        GoalEditFragment details = (GoalEditFragment) mAddGoalPagerAdapter.getRegisteredFragment(0);
        if(details == null || details.getOccurences() == -1 || details.getTimeFrame()==-1 || details.getTitle() == ""){
            Toast errorMessage = Toast.makeText(mContext, "Required Details missing", Toast.LENGTH_SHORT);
            errorMessage.show();
            return;
        }

        mGoal.setTimeFrame(details.getTimeFrame());
        mGoal.setComments(details.getComment());
        mGoal.setTitle(details.getTitle());
        mGoal.setOccurance(details.getOccurences());
        mGoal.setEndDate(details.getEndDate());
        mGoal.setStartDate(details.getStartDate());
        mGoal.setCategory(details.getCategory());

        GoalLocationFragment locationData = (GoalLocationFragment) mAddGoalPagerAdapter.getRegisteredFragment(1);
        if(locationData == null || locationData.getLocations().size() == 0){
            Toast errorMessage = Toast.makeText(mContext, "Requires at least one location", Toast.LENGTH_SHORT);
            errorMessage.show();
            return;
        }

        List<LatLng> locations = locationData.getLocations();
        List<Integer> radii = locationData.getRadii();

        for(int i = 0; i < locations.size(); i++){
            mGoal.addGeofence(locations.get(i), radii.get(i));
        }
        intent.putExtra("goal", mGoal); //eric comment - wasn't compiling
        setResult(RESULT_OK, intent);
        finish();
        Log.v(TAG, "Sending goal back to GoalList");
    }

    public void updateImageToGym(View view) {
        ImageView img = (ImageView) findViewById(R.id.goal_edit_imageView);
        img.setImageResource(R.mipmap.category_gym);
    }

    public void updateImageToSchool(View view) {
        ImageView img = (ImageView) findViewById(R.id.goal_edit_imageView);
        img.setImageResource(R.mipmap.category_school);
    }

    public void updateImageToOther(View view) {
        ImageView img = (ImageView) findViewById(R.id.goal_edit_imageView);
        img.setImageResource(R.mipmap.category_other);
    }

    public Goal getGoal(){
        return mGoal;
    }


}
