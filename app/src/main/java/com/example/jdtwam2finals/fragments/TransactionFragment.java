package com.example.jdtwam2finals.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jdtwam2finals.Adapters.TransactionAdapter;
import com.example.jdtwam2finals.DatePickerActivity;
import com.example.jdtwam2finals.dao.DbCon;
import com.example.jdtwam2finals.dao.ExpenseTable;
import com.example.jdtwam2finals.dao.IncomeTable;
import com.example.jdtwam2finals.dao.TransactionTable;
import com.example.jdtwam2finals.dao.UserTable;
import com.example.jdtwam2finals.databinding.FragmentTransactionBinding;
import com.example.jdtwam2finals.dto.Expense;
import com.example.jdtwam2finals.dto.Income;
import com.example.jdtwam2finals.dto.Transaction;
import com.example.jdtwam2finals.utils.ExportDataToCsv;
import com.example.jdtwam2finals.utils.MonetaryFormat;
import com.example.jdtwam2finals.utils.MonthSetter;
import com.example.jdtwam2finals.utils.QueryBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransactionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionFragment extends Fragment {

    private FragmentTransactionBinding b;
    private TransactionAdapter tAdapter;
    private Context context;
    private RecyclerView transactionsDisplay;
    private DbCon dbCon;
    private SharedPreferences sp;
    private Integer currentMonth;
    private ImageButton prev, next;
    private TextView monthSpinner, expenses, income;
    private List<Transaction> transactionsList;
    private Button export;
    private LinearLayout incomeDisplay, expenseDisplay;
    private LocalDate selectedDate;
    private static final int ITEMS_PER_PAGE = 5;
    private Integer offset_to_apply = null;
    private Integer pagination = 0;
    private boolean isLastPage = false;
    private static final String[] MONTHS = {
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
    };

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TransactionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransactionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransactionFragment newInstance(String param1, String param2) {
        TransactionFragment fragment = new TransactionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = requireActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentTransactionBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Calendar calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH);
        dbCon = DbCon.getInstance(context);
        sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        next = b.next;
        prev = b.previous;
        monthSpinner = b.monthSpinner;
        transactionsDisplay = b.monthlyTransactions;
        expenses = b.monthlyExpenses;
        income = b.monthlyIncome;
        export = b.exportButton;
        incomeDisplay = b.incomeDisplay;
        expenseDisplay = b.expenseDisplay;
        selectedDate = LocalDate.now();
        offset_to_apply = 0;
        setMonthSpinner();

        prev.setOnClickListener(v -> {
            if (currentMonth == 0) {
                return;
            }
            selectedDate = selectedDate.minusMonths(1);
            --currentMonth;
            offset_to_apply = 0;
            transactionsList = null;
            pagination = 0;
            setMonthSpinner();
        });

        next.setOnClickListener(v -> {
            if(currentMonth == MONTHS.length - 1){
                return;
            }
            selectedDate = selectedDate.plusMonths(1);
            ++currentMonth;
            offset_to_apply = 0;
            pagination = 0;
            transactionsList = null;
            setMonthSpinner();
        });

        export.setOnClickListener(v -> {
            if (transactionsList.size() != 0){
                boolean exported = ExportDataToCsv.exportDatabaseToCsv(context);
                if (exported){
                    for(Transaction t: transactionsList){
                        deleteTransaction(t);
                    }
                    setMonthSpinner();
                }
            }
        });

        monthSpinner.setOnClickListener(v -> {
            if (selectedDate == null) {
                return;
            }
            Intent intent = new Intent(getActivity(), DatePickerActivity.class);
            intent.putExtra("month_display", selectedDate.toString());
            intent.putExtra("month_spinner", MONTHS[currentMonth]);
            startActivity(intent);
        });

        b.transactionNestedView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int maxScrollY = b.transactionNestedView.getChildAt(0).getMeasuredHeight() - b.transactionNestedView.getMeasuredHeight();
            int scrollY = b.transactionNestedView.getScrollY();
            int threshold = 100;

            if (transactionsList != null && transactionsList.size() >= ITEMS_PER_PAGE) {
                if (maxScrollY - scrollY <= threshold) {

                    if (!isLastPage) {
                        setMonthSpinner();
                    }
                }
            }
        });
    }

    private void setMonthSpinner() {
        monthSpinner.setText(MONTHS[currentMonth]);
        offset_to_apply = pagination * ITEMS_PER_PAGE;
        pagination++;
        isLastPage = false;
        Double getIncome = null;
        Double getExpense = null;
        ExecutorService e = Executors.newCachedThreadPool();

        List<Callable<Double>> tasks = new ArrayList<>();
        tasks.add(() -> e.submit(() -> {
            int userId = sp.getInt("user", -1);
            QueryBuilder<Transaction> queryBuilder = new TransactionTable();
            queryBuilder.database(dbCon.getReadableDatabase());
            Cursor cursor = queryBuilder
                    .find()
                    .where(TransactionTable.COLUMN_USER_ID, "=", String.valueOf(userId))
                    .where(TransactionTable.COLUMN_TYPE, "=",  "Income")
                    .where(TransactionTable.COLUMN_MONTH, "=", MONTHS[currentMonth])
                    .relation(IncomeTable.TABLE_NAME, IncomeTable.COLUMN_TRANSACTION_ID, TransactionTable.TABLE_NAME, TransactionTable.COLUMN_TRANSACTION_ID)
                    .sum(IncomeTable.COLUMN_AMOUNT)
                    .exec();
            if (cursor != null && cursor.getCount() > 0){
                cursor.moveToFirst();
                double sum = (double) cursor.getLong(cursor.getColumnIndexOrThrow("SUM(amount)"));
                cursor.close();
                return sum;
            }
            return null;
        }).get());
        tasks.add(() -> e.submit(() -> {
            int userId = sp.getInt("user", -1);
            QueryBuilder<Transaction> queryBuilder = new TransactionTable();
            queryBuilder.database(dbCon.getReadableDatabase());
            Cursor cursor = queryBuilder
                    .find()
                    .where(TransactionTable.COLUMN_USER_ID, "=", String.valueOf(userId))
                    .where(TransactionTable.COLUMN_TYPE, "=",  "Expense")
                    .where(TransactionTable.COLUMN_MONTH, "=", MONTHS[currentMonth])
                    .relation(ExpenseTable.TABLE_NAME, ExpenseTable.COLUMN_TRANSACTION_ID, TransactionTable.TABLE_NAME, TransactionTable.COLUMN_TRANSACTION_ID)
                    .sum(ExpenseTable.COLUMN_AMOUNT)
                    .exec();
            if (cursor != null && cursor.getCount() > 0){
                cursor.moveToFirst();
                double sum = (double) cursor.getLong(cursor.getColumnIndexOrThrow("SUM(amount)"));
                cursor.close();
                return sum;
            }
            return null;
        }).get());


        Future<List<?>> transactions = e.submit(() -> {
            int userId = sp.getInt("user", -1);
            List<Transaction> currentTransaction = new ArrayList<>();
            QueryBuilder<Transaction> query = new TransactionTable();
            query.database(dbCon.getReadableDatabase());
            Cursor cursor = query.find()
                    .where(UserTable.COLUMN_USER_ID, "=", String.valueOf(userId))
                    .where(TransactionTable.COLUMN_MONTH, "=", MONTHS[currentMonth])
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

                        if ("Expense".equals(t.getType())){
                            QueryBuilder<Expense> exp =new ExpenseTable();
                            exp.database(dbCon.getReadableDatabase());
                            Cursor expCur = exp.find()
                                    .where(ExpenseTable.COLUMN_TRANSACTION_ID, "=", String.valueOf(t.getTransactionId()))
                                    .exec();
                            if (expCur != null && expCur.getCount() > 0){
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
                            if (incCur != null && incCur.getCount() > 0){
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
                        }else{
                            throw new RuntimeException("This type does not exist");
                        }
                        currentTransaction.add(t);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
                cursor.close();
                return currentTransaction;
            }
            return null;
        });

        try {
            List<Future<Double>> results = e.invokeAll(tasks);
            getIncome = results.get(0).get();
            getExpense = results.get(1).get();
            if (transactionsList == null) {
               transactionsList = (List<Transaction>) transactions.get();
            }else{
                if (transactions.get().size() > 0){
                    transactionsList.addAll((List<Transaction>) transactions.get());
                }else{
                    isLastPage = true;
                }
            }
            e.shutdown();
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        String moneyPrefix = "Php ";
        expenses.setText(moneyPrefix.concat((getExpense != null ?
                MonetaryFormat.formatCurrencyWithTrim(getExpense)
                : "0")));

        income.setText(moneyPrefix.concat(getIncome != null ?
                MonetaryFormat.formatCurrencyWithTrim(getIncome)
                : MonetaryFormat.formatCurrencyWithTrim(0)));

        Double finalGetExpense = getExpense;
        expenseDisplay.setOnClickListener(v -> displayClicked("Total Expense for " + MONTHS[currentMonth] + ": " + finalGetExpense));

        Double finalGetIncome = getIncome;
        incomeDisplay.setOnClickListener(v -> displayClicked("Total Income for " + MONTHS[currentMonth] + ": "+ finalGetIncome));

        tAdapter = new TransactionAdapter(context, transactionsList, false, this::setMonthSpinner);
        transactionsDisplay.setLayoutManager(new LinearLayoutManager(context));
        transactionsDisplay.setAdapter(tAdapter);
    }
    public void deleteTransaction(Transaction t){
        if ("Expense".equals(t.getType())) {
            QueryBuilder<Expense> exp =new ExpenseTable();
            exp.database(dbCon.getWritableDatabase());
            exp.delete()
                    .where(ExpenseTable.COLUMN_EXPENSE_ID, "=", String.valueOf(t.getExpense().getExpenseId()))
                    .execDelete();
        } else if ("Income".equals(t.getType())) {
            QueryBuilder<Income> inc = new IncomeTable();
            inc.database(dbCon.getWritableDatabase());
            inc.delete()
                    .where(IncomeTable.COLUMN_INCOME_ID, "=", String.valueOf(t.getIncome().getIncomeId()))
                    .execDelete();

        }else{
            Log.d("ViewHolder", "Unrecognized Type");
        }

        QueryBuilder<Transaction> transactQuery =new TransactionTable();
        transactQuery.database(dbCon.getWritableDatabase());
        transactQuery
                .delete()
                .one(t.getTransactionId())
                .execDelete();
    }

    public void displayClicked(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}