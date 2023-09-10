package com.example.jdtwam2finals.dao;

public class TransactionTable {
    public static final String TABLE_NAME = "transactions";
    public static final String COLUMN_TRANSACTION_ID = "transaction_id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_USER_ID = "user_id";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TYPE + " TEXT NOT NULL,"
            + COLUMN_DATE + " TEXT NOT NULL,"
            + COLUMN_USER_ID + " INTEGER,"
            + " FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + UserTable.TABLE_NAME + "(" + UserTable.COLUMN_USER_ID + "))";

    public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
