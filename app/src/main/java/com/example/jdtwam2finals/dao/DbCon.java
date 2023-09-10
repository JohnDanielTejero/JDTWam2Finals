package com.example.jdtwam2finals.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbCon extends SQLiteOpenHelper {

    private static DbCon instance;
    private static final String DATABASE_NAME = "WalTrackDb";
    private static final int DATABASE_VERSION = 1;

    public DbCon(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DbCon(Context applicationContext) {
        super(applicationContext, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DbCon getInstance(Context context) {
        if (instance == null) {
            instance = new DbCon(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserTable.CREATE_TABLE);
        db.execSQL(TransactionTable.CREATE_TABLE);
        db.execSQL(IncomeTable.CREATE_TABLE);
        db.execSQL(ExpenseTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(IncomeTable.DELETE_TABLE);
        db.execSQL(ExpenseTable.DELETE_TABLE);
        db.execSQL(TransactionTable.DELETE_TABLE);
        db.execSQL(UserTable.DELETE_TABLE);
        onCreate(db);
    }
}
