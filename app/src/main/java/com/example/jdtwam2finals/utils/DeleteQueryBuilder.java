package com.example.jdtwam2finals.utils;

public interface DeleteQueryBuilder<T> extends SQLiteQueryService{
    /**
     * Sets the operator to  DELETE
     *
     * @return QueryBuilder instance
     */
    public DeleteQueryBuilder<T> delete();
}
