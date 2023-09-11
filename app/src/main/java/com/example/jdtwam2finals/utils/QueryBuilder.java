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
public interface QueryBuilder<T> {
    /**
     *
     * For assigning db context.
     *
     * Usage: database(getReadableDatabase() | getWriteableDatabase());
     * Parameter will depend on the use case.
     *
     * @param  db - readable database or writeable database
     * @return QueryBuilder instance
     */
    public QueryBuilder<T> database(SQLiteDatabase db);

    /**
     * For retrieving via primary key.
     *
     * @param id - Primary key
     * @return QueryBuilder instance.
     */
    public QueryBuilder<T> one(Integer id);

    /**
     * Sets the operator as SELECT
     *
     * @return QueryBuilder instance
     */
    public QueryBuilder<T> find();

    /**
     * Defines Relations attached to table
     *
     * @param foreignTableName - Foreign table to JOIN
     * @param foreignKey - Foreign key of foreign table to compare
     * @param refTableName - Origin table to ATTACH
     * @param refKey - Primary key of origin table for reference
     * @return QueryBuilder instance
     */
    public QueryBuilder<T> relation(String foreignTableName, String foreignKey, String refTableName, String refKey);

    /**
     * Sets the query as count
     *
     * NOTE: Do not use when using sum
     * @return QueryBuilder instance
     */
    public QueryBuilder<T> count();

    /**
     * Inserts new record
     *
     * @param obj - data model for insertion
     * @return primary key of inserted record
     */
    public long insert(T obj);

    /**
     * Sets up conditions for query
     *
     * @param columnName - Column for identifier
     * @param condition - Condition statement, (only arithmetic operators are supported)
     * @param identifier - Value target
     * @return QueryBuilder instance
     */
    public QueryBuilder<T> where(String columnName, String condition, String identifier);

    /**
     * Sets the order of table
     *
     * @param i - 1 for ASC, 0 for DESC
     * @return QueryBuilder instance
     */
    public QueryBuilder<T> orderBy(int i);

    /**
     * Sets the Operator to UPDATE
     *
     * @param id - primary key of record
     * @return QueryBuilder instance
     */
    public QueryBuilder<T> update(Integer id);

    /**
     * Limits the number or records to be retrieved
     *
     * @param num - amount of records to be retrieved
     * @return QueryBuilder instance
     */
    public QueryBuilder<T> limitBy(int num);

    /**
     * Sets the operator to  DELETE
     *
     * @return QueryBuilder instance
     */
    public QueryBuilder<T> delete();

    /**
     * Executes the SQL query for find() operator
     *
     * Usage:
     * QueryBuilder<T> query = new Table(db.getReadableDatabase());
     * Cursor cursor = query.find().one(id).exec();
     *
     * Whereas:
     * Table extends the abstract class that implements the interface
     *
     * @return cursor
     */
    public Cursor exec();

    /**
     * Execute the SQL query for delete() operator
     *
     * Usage:
     * QueryBuilder<T> query = new Table(db.getWriteableDatabase());
     * Cursor cursor = query.delete().one(id).execDelete();
     *
     * Whereas:
     * Table extends the abstract class that implements the interface
     */
    public void execDelete();

    /**
     * Returns the sum of specific field.
     *
     * NOTE: Only use this for mathematical operations
     * Do not use when using COUNT
     * @return QueryBuilder instance
     */
    public QueryBuilder<T> sum(String columnToSum);

}
