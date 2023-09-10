package com.example.jdtwam2finals.dao;

public class ExpenseTable {
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
}
