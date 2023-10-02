package com.example.jdtwam2finals.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jdtwam2finals.ViewHolders.CalendarViewHolder;
import com.example.jdtwam2finals.databinding.DayCellBinding;
import com.example.jdtwam2finals.utils.Callback;

import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<String> daysOfMonth;
    private final Callback onItemListener;
    public CalendarAdapter(ArrayList<String> daysOfMonth, Callback onItemListener) {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        DayCellBinding binding = DayCellBinding.inflate(inflater, parent, false);
        return new CalendarViewHolder(binding, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        holder.bind(daysOfMonth.get(position));

    }

    @Override
    public int getItemCount()
    {
        return daysOfMonth.size();
    }

}