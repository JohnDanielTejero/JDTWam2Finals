package com.example.jdtwam2finals.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.jdtwam2finals.dto.User;
import com.example.jdtwam2finals.utils.QueryBuilder;
import com.example.jdtwam2finals.utils.QueryBuilderImpl;

public class UserTable extends QueryBuilderImpl<User> {
    public static final String TABLE_NAME = "user";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT UNIQUE NOT NULL,"
            + COLUMN_PASSWORD + " TEXT NOT NULL)";

    public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public UserTable() {
        super();
    }
    public UserTable(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public QueryBuilder<User> database(SQLiteDatabase db) {
        super.selectedTable = TABLE_NAME;
        return super.database(db);
    }

    @Override
    public QueryBuilder<User> one(Integer id) {
        return this.where(COLUMN_USER_ID, "=", id.toString());
    }

    @Override
    public long insert(User user) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_USERNAME, user.getUsername());
        v.put(COLUMN_PASSWORD, user.getPassword());
        return db.insert(TABLE_NAME, null, v);
    }

    @Override
    public QueryBuilder<User> update(Integer id) {
        super.operation = "UPDATE";
        return where(COLUMN_USER_ID, "=", id.toString());
    }


}

