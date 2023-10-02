package com.example.jdtwam2finals.ViewHolders;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jdtwam2finals.R;
import com.example.jdtwam2finals.dao.ExpenseTable;
import com.example.jdtwam2finals.dao.IncomeTable;
import com.example.jdtwam2finals.dao.TransactionTable;
import com.example.jdtwam2finals.databinding.TransactionViewHolderBinding;
import com.example.jdtwam2finals.dto.Expense;
import com.example.jdtwam2finals.dto.Income;
import com.example.jdtwam2finals.dto.Transaction;
import com.example.jdtwam2finals.utils.Callback;
import com.example.jdtwam2finals.utils.MonetaryFormat;
import com.example.jdtwam2finals.utils.QueryBuilder;

import java.text.SimpleDateFormat;

public class TransactionViewHolder extends RecyclerView.ViewHolder {

    private ImageButton deleteButton, editButton;
    private ImageView icon;
    private TextView type, note, category, date, amount;
    private TransactionViewHolderBinding b;
    private Callback cb;
    private Context context;
    private boolean isDashboard;
    public TransactionViewHolder(@NonNull View itemView, TransactionViewHolderBinding binding, boolean isDashboard, Callback cb, Context context) {
        super(itemView);
        b = binding;
        this.context = context;
        deleteButton = b.deleteButton;
        icon = b.transactionIcon;
        type = b.holderTransactionType;
        note = b.holderTransactionNote;
        category = b.holderTransactionCategory;
        date = b.holderTransactionDate;
        amount = b.holderAmount;
        editButton = b.editButton;

        this.isDashboard = isDashboard;
        this.cb = cb;
    }

    public void bind(Transaction transaction, SQLiteDatabase db) {

        type.setText(transaction.getType());
        date.setText(new SimpleDateFormat("YY/MM/dd").format(transaction.getDate()));
        if ("Expense".equals(transaction.getType())) {
            icon.setImageResource(R.drawable.price_price_tag_cost_svgrepo_com);
            note.setText(transaction.getExpense().getNote());
            category.setText(transaction.getExpense().getCategory());
            amount.setText("Php " + MonetaryFormat.formatCurrencyWithTrim(transaction.getExpense().getAmount()));

        } else if ("Income".equals(transaction.getType())) {
            icon.setImageResource(R.drawable.wallet_to_save_dollars_svgrepo_com);
            category.setVisibility(View.GONE);
            note.setText(transaction.getIncome().getNote());
            amount.setText("Php " + MonetaryFormat.formatCurrencyWithTrim(transaction.getIncome().getAmount()));

        }else{
            Log.d("ViewHolder", "Unrecognized Type");
        }

        if (!isDashboard) {
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.GONE);

            deleteButton.setOnClickListener(v -> deleteTransaction(transaction, db));
            editButton.setOnClickListener(v-> {

            });

        } else {
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }

    }

    public void deleteTransaction(Transaction t, SQLiteDatabase db){
        if ("Expense".equals(t.getType())) {
          QueryBuilder<Expense> exp = new ExpenseTable();
          exp.database(db);
          exp.delete()
                  .where(ExpenseTable.COLUMN_EXPENSE_ID, "=", String.valueOf(t.getExpense().getExpenseId()))
                  .execDelete();
        } else if ("Income".equals(t.getType())) {
            QueryBuilder<Income> inc = new IncomeTable();
            inc.database(db);
            inc.delete()
                    .where(IncomeTable.COLUMN_INCOME_ID, "=", String.valueOf(t.getIncome().getIncomeId()))
                    .execDelete();

        }else{
            Log.d("ViewHolder", "Unrecognized Type");
        }

        QueryBuilder<Transaction> transactQuery = new TransactionTable(db);
        transactQuery
                .delete()
                .one(t.getTransactionId())
                .execDelete();

        Toast.makeText(context, "Transaction successfully deleted!", Toast.LENGTH_SHORT).show();
        if (cb != null){
            cb.execute();
        }
    }

}
