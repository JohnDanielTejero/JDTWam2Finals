package com.example.jdtwam2finals.ViewHolders;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jdtwam2finals.Adapters.TransactionAdapter;
import com.example.jdtwam2finals.MainActivity;
import com.example.jdtwam2finals.R;
import com.example.jdtwam2finals.dao.DbCon;
import com.example.jdtwam2finals.dao.ExpenseTable;
import com.example.jdtwam2finals.dao.IncomeTable;
import com.example.jdtwam2finals.dao.TransactionTable;
import com.example.jdtwam2finals.dao.UserTable;
import com.example.jdtwam2finals.databinding.DayCellBinding;
import com.example.jdtwam2finals.databinding.TransactionPerDayDisplayBinding;
import com.example.jdtwam2finals.dto.Expense;
import com.example.jdtwam2finals.dto.Income;
import com.example.jdtwam2finals.dto.Transaction;
import com.example.jdtwam2finals.dto.User;
import com.example.jdtwam2finals.utils.MonthSetter;
import com.example.jdtwam2finals.utils.QueryBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CalendarViewHolder extends RecyclerView.ViewHolder {

    public TextView textDate;
    private DayCellBinding b;
    private String monthAndYear;
    private String dateStringFormat;
    private String month;
    private  DbCon dbCon;
    private TransactionPerDayDisplayBinding tbpd;
    private List<Transaction> transactions;
    private Context context;
    private Dialog dialog;
    private SharedPreferences sp;
    private Integer offset_to_apply = null;
    private static final int ITEMS_PER_PAGE = 6;
    private Integer pagination = 0;
    private boolean isLastPage = false;
    private TransactionAdapter tAdapter;

    public CalendarViewHolder(DayCellBinding binding, String monthAndYear, DbCon dbCon, Context context, TransactionPerDayDisplayBinding tpbd) {
        super(binding.getRoot());
        this.b = binding;
        this.monthAndYear = monthAndYear;
        this.month = monthAndYear.split("-")[1];
        this.dbCon = dbCon;
        this.context = context;
        this.tbpd = tpbd;
        sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        this.textDate = b.calendarDayCell;
    }

    public void bind(String text) {
        b.calendarDayCell.setText(text);
        this.textDate.setOnClickListener(v -> {
            tAdapter = null;
            if (!this.textDate.getText().toString().isEmpty()){
                Integer day = Integer.parseInt(text);
                if (day > 9){
                    dateStringFormat = monthAndYear.concat("-".concat(text));
                }else{
                    dateStringFormat = monthAndYear.concat("-0".concat(text));
                }

                if (tbpd.getRoot().getParent() != null) {
                    ((ViewGroup) tbpd.getRoot().getParent()).removeView(tbpd.getRoot());
                }

                transactions = null;
                dialog = new Dialog(context);
                pagination = 0;
                isLastPage = false;
                offset_to_apply = 0;
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(tbpd.getRoot());

                setData();
                tbpd.dismissDialogTransactionDay.setOnClickListener(view -> dialog.dismiss());

                tAdapter = new TransactionAdapter(context, transactions, true, this::retrieveData);
                tbpd.transactionDateList.setLayoutManager(new LinearLayoutManager(context));
                tbpd.transactionDateList.setAdapter(tAdapter);

                SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MMMM-dd", Locale.US);

                Date parseDate = null;
                String formattedDate = "";
                try {
                    parseDate = inputFormatter.parse(dateStringFormat);
                    formattedDate = new SimpleDateFormat("YYYY/MM/dd").format(parseDate);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                tbpd.transactionDate.setText(formattedDate);
                tbpd.transactionDateList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        int maxScrollY = recyclerView.computeVerticalScrollRange() - recyclerView.getHeight();
                        int scrollY = recyclerView.computeVerticalScrollOffset();
                        int threshold = 0;
                        if (transactions != null && transactions.size() >= ITEMS_PER_PAGE) {
                            if (maxScrollY - scrollY <= threshold) {
                                if (!isLastPage) {
                                    setData();
                                    tAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });

                dialog.show();
                Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.pop_up_animation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);
            }
        });
    }

    public void setData(){
        List<Transaction> currentTransactions = retrieveData();
        if (transactions == null){
            transactions = currentTransactions;
        }else{
            transactions.addAll(currentTransactions);
        }

        if(transactions.size() != pagination * ITEMS_PER_PAGE && !isLastPage){
            setData();
        }

    }
    public List<Transaction> retrieveData() {
        offset_to_apply = pagination * ITEMS_PER_PAGE;
        pagination++;
        int userId = sp.getInt("user", -1);
        List<Transaction> currentTransaction = new ArrayList<>();
        QueryBuilder<Transaction> query = new TransactionTable();
        query.database(dbCon.getReadableDatabase());
        Cursor cursor = query.find()
                .where(UserTable.COLUMN_USER_ID, "=", String.valueOf(userId))
                .where(TransactionTable.COLUMN_MONTH, "=", month)
                //.whereDate(dateStringFormat, TransactionTable.COLUMN_DATE) - not working
                .orderByDate(false, TransactionTable.COLUMN_DATE)
                .limitBy(ITEMS_PER_PAGE)
                .offset(offset_to_apply)
                .exec();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_TRANSACTION_ID));
                    String month = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_MONTH));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_TYPE));
                    Date date = MonthSetter.parseDate((cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_DATE))));
                    Transaction t = new Transaction((int) id, type, date, month, userId);

                    if ("Expense".equals(t.getType())) {
                        QueryBuilder<Expense> exp = new ExpenseTable();
                        exp.database(dbCon.getReadableDatabase());
                        Cursor expCur = exp.find()
                                .where(ExpenseTable.COLUMN_TRANSACTION_ID, "=", String.valueOf(t.getTransactionId()))
                                .exec();
                        if (expCur != null && expCur.getCount() > 0) {
                            expCur.moveToFirst();
                            Expense expense = new Expense(
                                    expCur.getInt(expCur.getColumnIndexOrThrow(ExpenseTable.COLUMN_EXPENSE_ID)),
                                    expCur.getDouble(expCur.getColumnIndexOrThrow(ExpenseTable.COLUMN_AMOUNT)),
                                    expCur.getString(expCur.getColumnIndexOrThrow(ExpenseTable.COLUMN_NOTE)),
                                    expCur.getString(expCur.getColumnIndexOrThrow(ExpenseTable.COLUMN_CATEGORY)),
                                    (int) expCur.getLong(expCur.getColumnIndexOrThrow(ExpenseTable.COLUMN_TRANSACTION_ID))
                            );
                            t.setExpense(expense);
                            expCur.close();
                        }

                    } else if ("Income".equals(t.getType())) {
                        QueryBuilder<Income> inc = new IncomeTable();
                        inc.database(dbCon.getReadableDatabase());
                        Cursor incCur = inc.find()
                                .where(IncomeTable.COLUMN_TRANSACTION_ID, "=", String.valueOf(t.getTransactionId()))
                                .exec();
                        if (incCur != null && incCur.getCount() > 0) {
                            incCur.moveToFirst();
                            Income income = new Income(
                                    incCur.getInt(incCur.getColumnIndexOrThrow(IncomeTable.COLUMN_INCOME_ID)),
                                    incCur.getDouble(incCur.getColumnIndexOrThrow(IncomeTable.COLUMN_AMOUNT)),
                                    incCur.getString(incCur.getColumnIndexOrThrow(IncomeTable.COLUMN_NOTE)),
                                    (int) incCur.getLong(incCur.getColumnIndexOrThrow(IncomeTable.COLUMN_TRANSACTION_ID))
                            );
                            t.setIncome(income);
                            incCur.close();
                        }
                    } else {
                        throw new RuntimeException("This type does not exist");
                    }
                    currentTransaction.add(t);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
            cursor.close();

            if (currentTransaction.size() == 0 ){
                isLastPage = true;
            }
            List<Transaction> filteredTransaction = new ArrayList<>();
            SimpleDateFormat outputFormatter = new SimpleDateFormat("yyyy-MMMM-dd", Locale.US);

             for (Transaction transaction :currentTransaction){
                String tDate = outputFormatter.format(transaction.getDate());
                Log.d("sqlQuery", String.format("tDate: %s, toCompare: %s", tDate, dateStringFormat));
                 Log.d("sqlQuery", tDate.equals(dateStringFormat)? "YES" : "NO");
                if (tDate.equals(dateStringFormat)){
                    filteredTransaction.add(transaction);
                }
             }
            Log.d("sqlQuery", String.format("filtered: %s", filteredTransaction.toString()));

            return filteredTransaction;
        }
        return null;
    }
}
