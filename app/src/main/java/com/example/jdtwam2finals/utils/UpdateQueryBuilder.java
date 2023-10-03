package com.example.jdtwam2finals.utils;

public interface UpdateQueryBuilder<T> extends SQLiteQueryService{
    /**
     * Sets the Operator to UPDATE
     *
     * @param id - primary key of record
     * @return QueryBuilder instance
     */
    public UpdateQueryBuilder<T> update(Integer id);
    /**
     * Sets up the column to be updated:
     *
     * @param column - Column for identifier
     * @param newVal - new value
     * @return QueryBuilder instance
     */
    public UpdateQueryBuilder<T> setNewColVal(String column, String newVal);
}
