package com.example.jdtwam2finals.utils;

import android.database.Cursor;

public interface SelectQueryBuilder<T> extends SQLiteQueryService{
    /**
     * Sets the operator as SELECT
     *
     * @return QueryBuilder instance
     */
    public SelectQueryBuilder<T> find();

}
