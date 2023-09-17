package com.example.jdtwam2finals.utils;

public interface InsertQueryBuilder<T> {
    /**
     * Inserts new record
     *
     * @param obj - data model for insertion
     * @return primary key of inserted record
     */
    public long insert(T obj);

}
