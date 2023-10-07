package com.example.jdtwam2finals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.jdtwam2finals.Adapters.CalendarAdapter;
import com.example.jdtwam2finals.dao.DbCon;
import com.example.jdtwam2finals.databinding.ActivityDatePickerBinding;
import com.example.jdtwam2finals.databinding.TransactionPerDayDisplayBinding;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DatePickerActivity extends AppCompatActivity {

    private ActivityDatePickerBinding b;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private String selectedMonth;
    private String selectedYear;
    private ArrayList<String> daysInMonth;
    private DbCon dbCon;
    private TransactionPerDayDisplayBinding tpdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityDatePickerBinding.inflate(getLayoutInflater());
        tpdb = TransactionPerDayDisplayBinding.inflate(getLayoutInflater());
        selectedDate = LocalDate.parse(getIntent().getStringExtra("month_display"));
        selectedMonth = getIntent().getStringExtra("month_spinner");
        b.dismissDatePicker.setOnClickListener(v -> {
            finish();
        });

        dbCon = DbCon.getInstance(getApplicationContext());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        selectedYear = selectedDate.format((formatter));

        calendarRecyclerView = b.dateDisplay;
        setContentView(b.getRoot());
        setMonthView();
    }

    private void setMonthView() {
        daysInMonth = daysInMonthArray(selectedDate);
        Log.d("MonthDebug",daysInMonth.toString());
        if (daysInMonth.size() > 0) {
            CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, selectedYear.concat("-".concat(selectedMonth)), dbCon, this, tpdb);
            b.monthTitle.setText(selectedMonth);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 7) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
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