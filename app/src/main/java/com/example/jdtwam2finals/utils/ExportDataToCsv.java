package com.example.jdtwam2finals.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.jdtwam2finals.dao.DbCon;
import com.example.jdtwam2finals.dao.ExpenseTable;
import com.example.jdtwam2finals.dao.IncomeTable;
import com.example.jdtwam2finals.dao.TransactionTable;
import com.example.jdtwam2finals.dao.UserTable;
import com.example.jdtwam2finals.dto.Expense;
import com.example.jdtwam2finals.dto.Income;
import com.example.jdtwam2finals.dto.Transaction;
import com.example.jdtwam2finals.dto.User;
import com.example.jdtwam2finals.utils.QueryBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Utility class for Csv Exporting
 *
 */
public class ExportDataToCsv {
    private static final String TAG = "CsvExporter";

    /**
     * Method for exporting CSV.
     *
     * @param context - Context of the app
     * @return boolean
     */
    public static boolean exportDatabaseToCsv(Context context) {
        // Create or open your SQLite database
        DbCon dbHelper = DbCon.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;
        SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        int id = sp.getInt("user", -1);

        // Define the SQL query to fetch data
        QueryBuilder<User> userQuery = new UserTable(db);
        Cursor forUser = userQuery.find()
                .one(id)
                .exec();

        if (forUser != null && forUser.getCount() > 0){
            forUser.moveToFirst();
            user = new User(
                    forUser.getInt(forUser.getColumnIndexOrThrow(UserTable.COLUMN_USER_ID)),
                    forUser.getString(forUser.getColumnIndexOrThrow(UserTable.COLUMN_USERNAME)),
                    null
            );
        }
        forUser.close();
        if (user!= null){
            QueryBuilder<Transaction> transactionQuery = new TransactionTable(db);
            Cursor forTransactions = transactionQuery.find()
                    .where(TransactionTable.COLUMN_USER_ID, "=", String.valueOf(id))
                    .exec();
            if (forTransactions != null) {
                List<Transaction> transactions = new ArrayList<>();
                while (forTransactions.moveToNext()) {
                    try {
                        @SuppressLint("Range") long tId = forTransactions.getLong(forTransactions.getColumnIndex(TransactionTable.COLUMN_TRANSACTION_ID));
                        @SuppressLint("Range") String month = forTransactions.getString(forTransactions.getColumnIndex(TransactionTable.COLUMN_MONTH));
                        @SuppressLint("Range") String type = forTransactions.getString(forTransactions.getColumnIndex(TransactionTable.COLUMN_TYPE));
                        Date date = MonthSetter.parseDate((forTransactions.getString(forTransactions.getColumnIndexOrThrow(TransactionTable.COLUMN_DATE))));
                        Transaction t = new Transaction((int) tId, type, date, month, id);

                        if ("Expense".equals(t.getType())){
                            QueryBuilder<Expense> exp = new ExpenseTable(db);
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
                            QueryBuilder<Income> inc = new IncomeTable(db);
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
                forTransactions.close();
                user.setTransactions(transactions);
            }
        }

        // Specify the destination file path on external storage
        File externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = user.getUsername().concat("_transaction.csv");
        File destinationFile = new File(externalStorageDir, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFile))) {
            // Write header
            writer.write("userId,username,transactionId,type,date,month,incomeAmount,incomeNote,expenseAmount,expenseCategory,expenseNote");
            writer.newLine();

            for (Transaction transaction : user.getTransactions()) {
                // Write combined user, transaction, income, and expense data
                writer.write(
                        user.getUserId() + "," +
                                user.getUsername() + "," +
                                transaction.getTransactionId() + "," +
                                transaction.getType() + "," +
                                transaction.getDate() + "," +
                                transaction.getMonth() + ","
                );

                Income income = transaction.getIncome();
                if (income != null) {
                    writer.write(
                            income.getAmount() + "," +
                                    income.getNote() + ",,,"
                    );
                } else {
                    writer.write(",,");
                }

                Expense expense = transaction.getExpense();
                if (expense != null) {
                    writer.write(
                            expense.getAmount() + "," +
                                    expense.getCategory() + "," +
                                    expense.getNote()
                    );
                } else {
                    writer.write(",,,");
                }

                writer.newLine();
            }

            Toast.makeText(context, "CSV file exported to Downloads directory.", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Export failed. Check permissions.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
