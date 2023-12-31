package com.example.jdtwam2finals.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jdtwam2finals.Adapters.TransactionAdapter;
import com.example.jdtwam2finals.dao.DbCon;
import com.example.jdtwam2finals.dao.ExpenseTable;
import com.example.jdtwam2finals.dao.IncomeTable;
import com.example.jdtwam2finals.dao.UserTable;
import com.example.jdtwam2finals.dto.Expense;
import com.example.jdtwam2finals.dto.Income;
import com.example.jdtwam2finals.utils.MonthSetter;
import com.example.jdtwam2finals.utils.QueryBuilder;
import com.example.jdtwam2finals.dao.TransactionTable;
import com.example.jdtwam2finals.databinding.FragmentDashboardBinding;
import com.example.jdtwam2finals.dto.Transaction;
import com.example.jdtwam2finals.utils.MonetaryFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding b;
    private TransactionAdapter tAdapter;
    private Context context;
    private DbCon dbCon;
    private SharedPreferences sp;
    private TextView total_income, total_expense, total_balance;
    private LinearLayout balanceDisplay, incomeDisplay, expenseDisplay;
    private List<Transaction> t;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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
        b = FragmentDashboardBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbCon = DbCon.getInstance(context);
        sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);

        total_income = b.totalIncomeDisplay;
        total_expense = b.totalExpenseDisplay;
        total_balance = b.totalBalanceDisplay;

        balanceDisplay = b.balanceDisplay;
        incomeDisplay = b.incomeDisplay;
        expenseDisplay = b.expenseDisplay;

        new FetchTransactionsTask().execute();
    }

    public void displayClicked(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private class FetchTransactionsTask extends AsyncTask<Void, Void, Void> {
        private Double getIncome;
        private Double getExpense;
        private List<Transaction> transactions;

        @Override
        protected Void doInBackground(Void... voids) {
            int userId = sp.getInt("user", -1);
            // Fetch income amount
            QueryBuilder<Transaction> incomeQueryBuilder = new TransactionTable(dbCon.getReadableDatabase());
            Cursor incomeCursor = incomeQueryBuilder
                    .find()
                    .where(TransactionTable.COLUMN_USER_ID, "=", String.valueOf(userId))
                    .where(TransactionTable.COLUMN_TYPE, "=", "Income")
                    .relation(IncomeTable.TABLE_NAME, IncomeTable.COLUMN_TRANSACTION_ID, TransactionTable.TABLE_NAME, TransactionTable.COLUMN_TRANSACTION_ID)
                    .sum(IncomeTable.COLUMN_AMOUNT)
                    .exec();
            if (incomeCursor != null && incomeCursor.getCount() > 0) {
                incomeCursor.moveToFirst();
                getIncome = (double) incomeCursor.getLong(incomeCursor.getColumnIndexOrThrow("SUM(amount)"));
                incomeCursor.close();
            } else {
                getIncome = null;
            }

            QueryBuilder<Transaction> expenseQueryBuilder = new TransactionTable(dbCon.getReadableDatabase());
            Cursor expenseCursor = expenseQueryBuilder
                    .find()
                    .where(TransactionTable.COLUMN_USER_ID, "=", String.valueOf(userId))
                    .where(TransactionTable.COLUMN_TYPE, "=", "Expense")
                    .relation(ExpenseTable.TABLE_NAME, ExpenseTable.COLUMN_TRANSACTION_ID, TransactionTable.TABLE_NAME, TransactionTable.COLUMN_TRANSACTION_ID)
                    .sum(ExpenseTable.COLUMN_AMOUNT)
                    .exec();
            if (expenseCursor != null && expenseCursor.getCount() > 0) {
                expenseCursor.moveToFirst();
                getExpense = (double) expenseCursor.getLong(expenseCursor.getColumnIndexOrThrow("SUM(amount)"));
                expenseCursor.close();
            } else {
                getExpense = null;
            }

            transactions = new ArrayList<>();
            QueryBuilder<Transaction> query = new TransactionTable(dbCon.getReadableDatabase());

            Cursor cursor = query.find()
                    .where(UserTable.COLUMN_USER_ID, "=", String.valueOf(userId))
                    //.orderByDate(false, TransactionTable.COLUMN_DATE)
                    .orderBy(0)
                    .limitBy(5)
                    .exec();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    try {
                        @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(TransactionTable.COLUMN_TRANSACTION_ID));
                        @SuppressLint("Range") String month = cursor.getString(cursor.getColumnIndex(TransactionTable.COLUMN_MONTH));
                        @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex(TransactionTable.COLUMN_TYPE));
                        Date date = MonthSetter.parseDate((cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_DATE))));
                        Transaction t = new Transaction((int) id, type, date, month, userId);

                        if ("Expense".equals(t.getType())){
                            QueryBuilder<Expense> exp = new ExpenseTable(dbCon.getReadableDatabase());

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
                            QueryBuilder<Income> inc = new IncomeTable(dbCon.getReadableDatabase());

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
                        transactions.add(t);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
                cursor.close();
                Double balance = getIncome != null && getExpense != null ? getIncome - getExpense : null;
                String moneyPrefix = "Php ";
                total_expense.setText(moneyPrefix.concat((getExpense != null ?
                        MonetaryFormat.formatCurrencyWithTrim(getExpense)
                        : "0")));

                total_income.setText(moneyPrefix.concat(getIncome != null ?
                        MonetaryFormat.formatCurrencyWithTrim(getIncome)
                        : MonetaryFormat.formatCurrencyWithTrim(0)));

                total_balance.setText(moneyPrefix.concat((balance != null ?
                        MonetaryFormat.formatCurrencyWithTrim(balance)
                        : "0")));

                t = transactions;
                balanceDisplay.setOnClickListener(v -> displayClicked("Total Balance: " + balance));
                Double finalGetExpense = getExpense;
                expenseDisplay.setOnClickListener(v -> displayClicked("Total Expense: " + finalGetExpense));
                Double finalGetIncome = getIncome;
                incomeDisplay.setOnClickListener(v -> displayClicked("Total Income: " + finalGetIncome));

                b.dashboardLoading.setVisibility(View.GONE);
                tAdapter = new TransactionAdapter(context, t, true, null);
                b.recentTransactions.setLayoutManager(new LinearLayoutManager(context));
                b.recentTransactions.setAdapter(tAdapter);
                Log.d("money", getIncome.toString());

            }
            return null;
        }
    }

}