/*
 * Copyright (c) 2015. Tyler McCraw
 */

package com.assignment.weatherprovider.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.assignment.weatherprovider.R;
import com.assignment.weatherprovider.adapter.WeatherAdapter;
import com.assignment.weatherprovider.model.Day;
import com.assignment.weatherprovider.rest.TenDayForecastHandler;

import java.util.ArrayList;

public class TenDayForecastFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private WeatherAdapter mRecyclerAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Day> days;
    // Movie parcelable key for saving instance state
    private static final String SAVED_DAYS = "SAVED_DAYS";
    private View mCoordinatorLayoutView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EditText mCityName;
    private ImageView mSearch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //Set up the xml layout
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = rootView.findViewById(R.id.rv);
        mSearch = rootView.findViewById(R.id.search);
        mCityName = rootView.findViewById(R.id.location_search);
        mCoordinatorLayoutView = rootView.findViewById(R.id.tendayforecast_coordinator_layout);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // Set up initial adapter (until we retrieve our data) so there is no skipping the layout
        mRecyclerView.setAdapter(new WeatherAdapter(getActivity(), new ArrayList<Day>()));


        // Attempt to restore weather data from savedInstanceState
        if (savedInstanceState != null) {
            days = savedInstanceState.getParcelableArrayList(SAVED_DAYS);
            if (mRecyclerAdapter == null) {
                initializeAdapter();
            } else {
                mRecyclerAdapter.notifyDataSetChanged();
            }
        }
        // If we couldn't retrieve days from a saved instance state
        if (days == null || days.size() == 0) {
            initializeData();
        }

        mSwipeRefreshLayout = rootView.findViewById(R.id.tendayforecast_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initializeData();
                initializeAdapter();
                mRecyclerView.refreshDrawableState();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swiperefresh);

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = mCityName.getText().toString().trim();

                if (cityName != null && cityName.length() > 4) {
                    loadData(cityName);
                } else {
                    Toast.makeText(getActivity(), "Please enter the city name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    private void initializeData() {
        if (days == null) {
            days = new ArrayList<>();
        }

        String cityName = mCityName.getText().toString().trim();

        if (cityName != null && cityName.length() > 4) {
            loadData(cityName);
        }

    }

    private void loadData(@NonNull String cityName) {
        @SuppressLint("StaticFieldLeak")
        TenDayForecastHandler tenDayForecastHandler = new TenDayForecastHandler(getActivity().getApplicationContext()) {
            @Override
            protected void onPostExecute(ArrayList<Day> result) {
                if (result != null && !result.isEmpty()) {
                    days.clear();
                    // It is required to call addAll because this causes the
                    // recycleradapter to realize that there is new data and to refresh the view
                    days.addAll(result);
                }else{
                    Toast.makeText(getActivity(), "No data found!!!", Toast.LENGTH_SHORT).show();
                    days.clear();
                }
                if (mRecyclerAdapter == null) {
                    initializeAdapter();
                } else {
                    mRecyclerAdapter.notifyDataSetChanged();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        };
        tenDayForecastHandler.execute(cityName);
    }

    private void initializeAdapter() {
        mRecyclerAdapter = new WeatherAdapter(getActivity(), days);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (days != null) {
            savedInstanceState.putParcelableArrayList(SAVED_DAYS, days);
        }
        super.onSaveInstanceState(savedInstanceState);
    }
}