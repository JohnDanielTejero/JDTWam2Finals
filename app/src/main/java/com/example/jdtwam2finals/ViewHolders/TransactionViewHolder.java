package com.example.jdtwam2finals.ViewHolders;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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
import com.example.jdtwam2finals.databinding.UpdateTransactionDialogBinding;
import com.example.jdtwam2finals.dto.Expense;
import com.example.jdtwam2finals.dto.Income;
import com.example.jdtwam2finals.dto.Transaction;
import com.example.jdtwam2finals.utils.Callback;
import com.example.jdtwam2finals.utils.MonetaryFormat;
import com.example.jdtwam2finals.utils.MonthSetter;
import com.example.jdtwam2finals.utils.QueryBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TransactionViewHolder extends RecyclerView.ViewHolder {

    private ImageButton deleteButton, editButton;
    private ImageView icon;
    private TextView type, note, category, date, amount;
    private TransactionViewHolderBinding b;
    private UpdateTransactionDialogBinding updateB;
    private Callback cb;
    private Context context;
    private boolean isDashboard;
    private DatePickerDialog datePickerDialog;
    private Boolean dialogOpened = false;
    private Button dateButton;

    public TransactionViewHolder(@NonNull View itemView, TransactionViewHolderBinding binding,
                                 UpdateTransactionDialogBinding ub, boolean isDashboard,
                                 Callback cb, Context context) {
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
        this.updateB = ub;
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
            deleteButton.setVisibility(View.VISIBLE);

            deleteButton.setOnClickListener(v -> deleteTransaction(transaction, db));
            editButton.setOnClickListener(v -> updateTransaction(transaction, db));

        } else {
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateTransaction(Transaction t, SQLiteDatabase db) {
        if (updateB.getRoot().getParent() != null) {
            ((ViewGroup) updateB.getRoot().getParent()).removeView(updateB.getRoot());
        }
        if (!dialogOpened) {
            Dialog dialog = new Dialog(context);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(updateB.getRoot());

            ImageButton imgButton = updateB.dismissDialogUpdate;
            imgButton.setOnClickListener(v -> {
                dialog.dismiss();
                dialogOpened = false;
            });
            dateButton = updateB.datePickerButton;
            initDatePicker();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(t.getDate());

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            updateB.datePickerButton.setText(makeDateString(day, month, year));
            if (t.getType().equals("Income")){
                updateB.updateAmount.setText(t.getIncome().getAmount().toString());
                updateB.updateNote.setText(t.getIncome().getNote());
                updateB.categoryContainerUpdate.setVisibility(View.GONE);
            } else if (t.getType().equals("Expense")) {
                updateB.updateAmount.setText(t.getExpense().getAmount().toString());
                updateB.updateNote.setText(t.getExpense().getNote());
                updateB.updateCategory.setText(t.getExpense().getNote());
                updateB.categoryContainerUpdate.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(context, "invalid type", Toast.LENGTH_SHORT).show();
            }

            dateButton.setOnClickListener(v-> openDatePicker());

            updateB.updateSubmit.setOnClickListener(v-> {
                boolean amountIsSubmittable = true;
                boolean noteIsSubmittable = true;
                boolean categoryIsSubmittable = true;
                String regex = "[-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?";

                if (updateB.updateAmount.getText().toString().isEmpty()){
                    updateB.updateAmount.setError("Field is required!");
                    amountIsSubmittable = false;
                }

                if(updateB.updateNote.getText().toString().isEmpty()){
                    updateB.updateNote.setError("Field is required!");
                    noteIsSubmittable = false;
                }

                if (updateB.updateCategory.getText().toString().isEmpty()){
                    updateB.updateCategory.setError("Field is required!");
                    categoryIsSubmittable = false;
                }

                if (!updateB.updateAmount.getText().toString().matches(regex)){
                    updateB.updateAmount.setError("Field should be in correct format.");
                    amountIsSubmittable = false;
                }

                if(t.getType() == "Income"){
                    categoryIsSubmittable = true;
                }
                if (amountIsSubmittable && noteIsSubmittable && categoryIsSubmittable){
                    QueryBuilder<Transaction> tb = new TransactionTable();
                    tb.database(db);
                    String inputDateStr = updateB.datePickerButton.getText().toString();
                    Log.d("datepicker", updateB.datePickerButton.getText().toString());
                    SimpleDateFormat inputDateFormat = new SimpleDateFormat("MMM d yyyy", Locale.ENGLISH);
                    //SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
                    SimpleDateFormat isoDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.US);

                    try {
                        Date updatedDate = inputDateFormat.parse(inputDateStr);
                        String monthName = monthFormat.format(updatedDate);
                        String isoDateStr = isoDateFormat.format(updatedDate);
                        tb.update(t.getTransactionId())
                                .setNewColVal(TransactionTable.COLUMN_DATE, isoDateStr)
                                .setNewColVal(TransactionTable.COLUMN_MONTH, monthName)
                                .execUpdate();
                    } catch (ParseException e) {
                        Log.d("datepicker", e.getLocalizedMessage());
                        throw new RuntimeException(e);
                    }

                    if (t.getType().equals("Income")){
                        QueryBuilder<Income> q = new IncomeTable();
                        q.database(db);

                        q.update(t.getIncome().getIncomeId())
                                .setNewColVal(IncomeTable.COLUMN_AMOUNT, updateB.updateAmount.getText().toString())
                                .setNewColVal(IncomeTable.COLUMN_NOTE, updateB.updateNote.getText().toString())
                                .execUpdate();


                    }else if (t.getType().equals("Expense")){
                        QueryBuilder<Expense> q = new ExpenseTable();
                        q.database(db);
                        q.update(t.getExpense().getExpenseId())
                                .setNewColVal(ExpenseTable.COLUMN_AMOUNT, updateB.updateAmount.getText().toString())
                                .setNewColVal(ExpenseTable.COLUMN_NOTE, updateB.updateNote.getText().toString())
                                .setNewColVal(ExpenseTable.COLUMN_CATEGORY, updateB.updateCategory.getText().toString())
                                .execUpdate();
                        cb.execute();
                        dialog.dismiss();
                    }else{
                        Toast.makeText(context, "Type does not exist!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else{
                    Toast.makeText(context, "Field is incorrect or is in incorrect format!", Toast.LENGTH_SHORT).show();
                    return;
                }
            });

            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.pop_up_animation;
            dialog.getWindow().setGravity(Gravity.BOTTOM);

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

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = makeDateString(day, month, year);
            dateButton.setText(date);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        Calendar minDateCalendar = Calendar.getInstance();
        minDateCalendar.set(Calendar.YEAR, year);
        minDateCalendar.set(Calendar.MONTH, Calendar.JANUARY);
        minDateCalendar.set(Calendar.DAY_OF_MONTH, 1);

        Calendar maxDateCalendar = Calendar.getInstance();
        maxDateCalendar.set(Calendar.YEAR, year);
        maxDateCalendar.set(Calendar.MONTH, Calendar.DECEMBER);
        maxDateCalendar.set(Calendar.DAY_OF_MONTH, 31);

        int style = android.R.style.Theme_Holo_Dialog;

        datePickerDialog = new DatePickerDialog(context, style, dateSetListener, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(minDateCalendar.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxDateCalendar.getTimeInMillis());
    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        return "JAN";
    }

    public void openDatePicker() {
        datePickerDialog.show();
    }
}
