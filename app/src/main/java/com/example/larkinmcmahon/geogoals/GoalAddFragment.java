package com.example.larkinmcmahon.geogoals;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by djflash on 8/4/15.
 */
public class GoalAddFragment extends Fragment {

    public GoalAddFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_goal_add, container, false);

        return rootView;
    }
}
