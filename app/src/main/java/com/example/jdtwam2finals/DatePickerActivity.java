package com.example.jdtwam2finals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.jdtwam2finals.Adapters.CalendarAdapter;
import com.example.jdtwam2finals.databinding.ActivityDatePickerBinding;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

public class DatePickerActivity extends AppCompatActivity {

    private ActivityDatePickerBinding b;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private String selectedMonth;
    private ArrayList<String> daysInMonth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityDatePickerBinding.inflate(getLayoutInflater());
        selectedDate = LocalDate.parse(getIntent().getStringExtra("month_display"));
        selectedMonth = getIntent().getStringExtra("month_spinner");
        calendarRecyclerView = b.dateDisplay;
        setContentView(b.getRoot());
        setMonthView();

    }

    private void setMonthView() {
        daysInMonth = daysInMonthArray(selectedDate);
        Log.d("MonthDebug",daysInMonth.toString());
        if (daysInMonth.size() > 0) {
            CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth);
            b.monthTitle.setText(selectedMonth);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 7);
            calendarRecyclerView.setLayoutManager(layoutManager);
            calendarRecyclerView.setAdapter(calendarAdapter);
        }

    }

    private ArrayList<String> daysInMonthArray(LocalDate date)
    {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++)
        {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek)
            {
                daysInMonthArray.add("");
            }
            else
            {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return  daysInMonthArray;
    }
}