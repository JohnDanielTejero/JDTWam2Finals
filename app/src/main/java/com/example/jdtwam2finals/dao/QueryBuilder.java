package com.example.jdtwam2finals.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

public interface QueryBuilder<T> {
    public QueryBuilder<T> database(SQLiteDatabase db);
    public QueryBuilder<T> one(Integer id);
    public QueryBuilder<T> find();
    public QueryBuilder<T> relation(String foreignTableName, String foreignKey, String refTableName, String refKey);
    public QueryBuilder<T> count();
    public long insert(T obj);
    public QueryBuilder<T> where(String columnName, String condition, String identifier);
    public QueryBuilder<T> orderBy(int i);
    public QueryBuilder<T> update(Integer id);
    public QueryBuilder<T> delete();
    public Cursor exec();

}
