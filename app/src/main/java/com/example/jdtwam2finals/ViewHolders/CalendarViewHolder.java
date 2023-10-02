package com.example.jdtwam2finals.ViewHolders;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jdtwam2finals.R;
import com.example.jdtwam2finals.databinding.DayCellBinding;
import com.example.jdtwam2finals.utils.Callback;

public class CalendarViewHolder extends RecyclerView.ViewHolder {

    public TextView textDate;
    private DayCellBinding b;

    public CalendarViewHolder(DayCellBinding binding) {
        super(binding.getRoot());
        this.b = binding;
        this.textDate = b.calendarDayCell;
        this.textDate.setOnClickListener(v -> {
            if (!this.textDate.getText().toString().isEmpty()){
                Log.d("test", "clicked");
            }
        });
    }

    public void bind(String text) {
        b.calendarDayCell.setText(text);
    }
}
