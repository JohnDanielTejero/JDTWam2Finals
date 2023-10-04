package com.example.jdtwam2finals.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jdtwam2finals.ViewHolders.CalendarViewHolder;
import com.example.jdtwam2finals.dao.DbCon;
import com.example.jdtwam2finals.databinding.DayCellBinding;
import com.example.jdtwam2finals.databinding.TransactionPerDayDisplayBinding;

import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<String> daysOfMonth;
    private final String monthAndYear;
    private DbCon dbCon;
    private TransactionPerDayDisplayBinding b;
    private Context context;

    public CalendarAdapter(ArrayList<String> daysOfMonth, String monthAndYear, DbCon dbCon, Context context, TransactionPerDayDisplayBinding b) {
        this.daysOfMonth = daysOfMonth;
        this.dbCon = dbCon;
        this.monthAndYear = monthAndYear;
        this.context = context;
        this.b = b;

    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        DayCellBinding binding = DayCellBinding.inflate(inflater, parent, false);

        return new CalendarViewHolder(binding, monthAndYear, dbCon, context, b);
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