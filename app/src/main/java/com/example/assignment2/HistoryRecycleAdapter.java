package com.example.assignment2;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class HistoryRecycleAdapter extends RecyclerView.Adapter<HistoryRecycleAdapter.MyViewHolder> {

    private final Context context;
    private final ArrayList<HistoryData> data;

    private final double KM_IN_MILES = 1.609;

    public HistoryRecycleAdapter(Context context, ArrayList<HistoryData> data){
        this.context = context;
        this.data = data;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView dateLabel;
        private final TextView stepVal;
        private final TextView distanceVal;
        private final TextView longVal;
        private final TextView latVal;

        public MyViewHolder(final View view){
            super(view);
            dateLabel = view.findViewById(R.id.dateLabel);
            stepVal = view.findViewById(R.id.stepsVal);
            distanceVal = view.findViewById(R.id.distanceVal);
            longVal = view.findViewById(R.id.longVal);
            latVal = view.findViewById(R.id.latVal);
        }

    }

    @NonNull
    @Override
    public HistoryRecycleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_item, parent, false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryRecycleAdapter.MyViewHolder holder, int position) {

        String steps;
        if (data.get(position).getSteps() != -1) {
            steps = String.valueOf(data.get(position).getSteps());
        }
        else {
            steps = "--";
        }

        SharedPreferences sharedPref = context.getSharedPreferences("settingsPrefs", Context.MODE_PRIVATE);

        // Gets saved units, first item is default
        String[] units = context.getResources().getStringArray(R.array.unit_list);
        String unitsPreferred = sharedPref.getString("unitsSaved", units[0]);

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(data.get(position).getDate());
        } catch (ParseException ignored) {
        }

        String formattedDate = "";
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
            String monthDay = new SimpleDateFormat("MM/dd", Locale.ENGLISH).format(date);

            formattedDate = dayOfWeek + " - " + monthDay;
        }

        String distance;
        if (data.get(position).getDistanceMiles() != -1) {
            if (Objects.equals(unitsPreferred, "Miles")) {
                distance = String.format(Locale.getDefault(), "%.4f", data.get(position).getDistanceMiles()) + " mi";
            }
            else{
                distance = String.format(Locale.getDefault(), "%.4f", data.get(position).getDistanceMiles() * KM_IN_MILES) + " km";
            }
        }
        else {
            distance = "--";
        }

        String longLoc;
        if (data.get(position).getLongLoc() != null) {
            longLoc = String.format(Locale.getDefault(), "%.5f", data.get(position).getLongLoc());
        }
        else {
            longLoc = "--";
        }

        String latLoc;
        if (data.get(position).getLongLoc() != null) {
            latLoc = String.format(Locale.getDefault(), "%.5f", data.get(position).getLatLoc());
        }
        else {
            latLoc = "--";
        }

        holder.dateLabel.setText(formattedDate);
        holder.stepVal.setText(steps);
        holder.distanceVal.setText(distance);
        holder.longVal.setText(longLoc);
        holder.latVal.setText(latLoc);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}

