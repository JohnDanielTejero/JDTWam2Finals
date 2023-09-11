package com.example.jdtwam2finals.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.jdtwam2finals.dto.Transaction;
import com.example.jdtwam2finals.utils.QueryBuilder;
import com.example.jdtwam2finals.utils.QueryBuilderImpl;

public class TransactionTable extends QueryBuilderImpl<Transaction> {
    public static final String TABLE_NAME = "transactions";
    public static final String COLUMN_TRANSACTION_ID = "transaction_id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_USER_ID = "user_id";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TYPE + " TEXT NOT NULL,"
            + COLUMN_DATE + " TEXT NOT NULL,"
            + COLUMN_MONTH + " TEXT NOT NULL,"
            + COLUMN_USER_ID + " INTEGER,"
            + " FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + UserTable.TABLE_NAME + "(" + UserTable.COLUMN_USER_ID + "))";

    public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    public TransactionTable() {
        super();
    }
    public TransactionTable(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public QueryBuilder<Transaction> database(SQLiteDatabase db) {
        super.selectedTable = TABLE_NAME;
        return super.database(db);
    }
    @Override
    public QueryBuilder<Transaction> one(Integer id) {
        return this.where(COLUMN_TRANSACTION_ID, "=", id.toString());
    }
    @Override
    public long insert(Transaction obj) {
        Log.d("month", obj.getMonth());
        ContentValues v = new ContentValues();
        v.put(COLUMN_TYPE, obj.getType());
        v.put(COLUMN_DATE, obj.getDate().toString());
        v.put(COLUMN_MONTH, obj.getMonth());
        v.put(COLUMN_USER_ID, obj.getUserId().toString());
        return db.insert(TABLE_NAME, null, v);
    }
    @Override
    public QueryBuilder<Transaction> update(Integer id) {
        super.operation = "UPDATE";
        return where(COLUMN_TRANSACTION_ID, "=", id.toString());
    }
}
