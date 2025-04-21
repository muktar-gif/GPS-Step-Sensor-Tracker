package com.example.assignment2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        ArrayList<StepData> loadStepData = Util.loadStepDataList(requireContext());
        ArrayList<LocationData> loadLocationData = Util.loadLocationDataList(requireContext());

        ArrayList<HistoryData> historyList = new ArrayList<>();

        // List of dates to account for step data and location data dates that don't include data
        ArrayList<String> getDates = new ArrayList<>();
        for (StepData data : loadStepData) {
            if (!getDates.contains(data.getDate())) {
                getDates.add(data.getDate());
            }
        }
        for (LocationData data : loadLocationData) {
            if (!getDates.contains(data.getDate())) {
                getDates.add(data.getDate());
            }
        }

        for (String date : getDates) {

            StepData getStepData = null;
            for (StepData data : loadStepData) {
                if (data.getDate().equals(date)) {
                    getStepData = data;
                    break;
                }
            }

            LocationData getLocData = null;
            for (LocationData data : loadLocationData) {
                if (data.getDate().equals(date)) {
                    getLocData = data;
                    break;
                }
            }

            int steps = getStepData != null ? getStepData.getSteps() : -1;
            double distanceMiles = getStepData != null ? getStepData.getDistanceMiles() : -1;
            Double longLoc = getLocData != null ? getLocData.getLongLoc() : null;
            Double latLoc = getLocData != null ? getLocData.getLatLoc() : null;

            historyList.add(new HistoryData(date, steps, distanceMiles, longLoc, latLoc));
        }

        historyList.sort((a, b) -> b.getDate().compareTo(a.getDate()));

        RecyclerView historyRecyclerView = view.findViewById(R.id.historyList);

        HistoryRecycleAdapter adapter = new HistoryRecycleAdapter(view.getContext(), historyList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());

        historyRecyclerView.setLayoutManager(layoutManager);
        historyRecyclerView.setItemAnimator(new DefaultItemAnimator());
        historyRecyclerView.setAdapter(adapter);

        // Inflate the layout for this fragment
        return view;
    }
}