package com.example.jdtwam2finals.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.jdtwam2finals.dto.Expense;
import com.example.jdtwam2finals.dto.Transaction;

public class ExpenseTable extends QueryBuilderImpl<Expense>{
    public static final String TABLE_NAME = "expense";
    public static final String COLUMN_EXPENSE_ID = "expense_id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_TRANSACTION_ID = "transaction_id";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_AMOUNT + " REAL NOT NULL,"
            + COLUMN_CATEGORY + " TEXT NOT NULL,"
            + COLUMN_NOTE + " TEXT NOT NULL,"
            + COLUMN_TRANSACTION_ID + " INTEGER UNIQUE,"
            + " FOREIGN KEY (" + COLUMN_TRANSACTION_ID + ") REFERENCES " + TransactionTable.TABLE_NAME + "(" + TransactionTable.COLUMN_TRANSACTION_ID + "))";

    public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public ExpenseTable(SQLiteDatabase db) {
        super(db);
    }

    public ExpenseTable() {}

    @Override
    public QueryBuilder<Expense> one(Integer id) {
        return this.where(COLUMN_EXPENSE_ID, "=", id.toString());
    }
    @Override
    public long insert(Expense obj) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_AMOUNT, obj.getAmount());
        v.put(COLUMN_NOTE, obj.getNote().toString());
        v.put(COLUMN_CATEGORY, obj.getCategory());
        v.put(COLUMN_TRANSACTION_ID, obj.getTransactionId().toString());
        return db.insert(TABLE_NAME, null, v);
    }

    @Override
    public QueryBuilder<Expense> update(Integer id) {
        super.operation = "UPDATE";
        return where(COLUMN_EXPENSE_ID, "=", id.toString());
    }
}
