package com.example.jdtwam2finals.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jdtwam2finals.*;
import com.example.jdtwam2finals.ViewHolders.TransactionViewHolder;
import com.example.jdtwam2finals.dao.DbCon;
import com.example.jdtwam2finals.dao.ExpenseTable;
import com.example.jdtwam2finals.dao.IncomeTable;
import com.example.jdtwam2finals.dao.TransactionTable;
import com.example.jdtwam2finals.dao.UserTable;
import com.example.jdtwam2finals.databinding.TransactionViewHolderBinding;
import com.example.jdtwam2finals.databinding.UpdateTransactionDialogBinding;
import com.example.jdtwam2finals.dto.Expense;
import com.example.jdtwam2finals.dto.Income;
import com.example.jdtwam2finals.dto.Transaction;
import com.example.jdtwam2finals.utils.Callback;
import com.example.jdtwam2finals.utils.QueryBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionViewHolder> {

    private Context context;
    private SharedPreferences sb;
    private List<Transaction> transactionList = new ArrayList<>();
    private TransactionViewHolderBinding tViewBinding;
    private UpdateTransactionDialogBinding updateB;
    private DbCon dbCon;
    private boolean isDashboard = false;
    private Callback cb;
    public TransactionAdapter(){}
    @SuppressLint("Range")
    public TransactionAdapter(Context context, List<Transaction> transactions, boolean isDashboard, Callback cb){
        this.sb = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        int userId = sb.getInt("user", -1);
        Log.d("adapter_custom", String.valueOf(userId));
        this.context = context;
        this.transactionList = transactions;
        this.isDashboard = isDashboard;
        this.cb = cb;
        dbCon = DbCon.getInstance(context);
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        tViewBinding = TransactionViewHolderBinding.inflate(LayoutInflater.from(context), parent, false);
        updateB = UpdateTransactionDialogBinding.inflate(LayoutInflater.from(context), parent, false);
        return new TransactionViewHolder(tViewBinding.getRoot(), tViewBinding, updateB, isDashboard, cb, context);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.bind(transaction, dbCon.getWritableDatabase());
    }

    @Override
    public int getItemCount() {
        if (transactionList != null) {
            return transactionList.size();
        } else {
            return 0;
        }
    }

}
