package com.example.jdtwam2finals.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Interface for Query Builder
 *
 * Note: Abstraction for SQL queries using builder design pattern.
 * Assign T as your Model
 * @param <T> - Model
 */
public interface QueryBuilder<T> extends DeleteQueryBuilder<T>, InsertQueryBuilder<T>, SelectQueryBuilder<T>, UpdateQueryBuilder<T> {

}
