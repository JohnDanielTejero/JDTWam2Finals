package com.example.jdtwam2finals.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public interface SQLiteQueryService<T> {
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
    public SQLiteQueryService<T> database(SQLiteDatabase db);

    /**
     * For retrieving via primary key.
     *
     * @param id - Primary key
     * @return QueryBuilder instance.
     */
    public SQLiteQueryService<T> one(Integer id);
    /**
     * Defines Relations attached to table
     *
     * @param foreignTableName - Foreign table to JOIN
     * @param foreignKey - Foreign key of foreign table to compare
     * @param refTableName - Origin table to ATTACH
     * @param refKey - Primary key of origin table for reference
     * @return QueryBuilder instance
     */
    public SQLiteQueryService<T> relation(String foreignTableName, String foreignKey, String refTableName, String refKey);

    /**
     * Sets the query as count
     *
     * NOTE: Do not use when using sum
     * @return QueryBuilder instance
     */
    public SQLiteQueryService<T> count();

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
     * sets up condition for where clause (only declare after all conditions are called...)
     * @param dateAsString
     * @param column_date_selection
     * @return
     */
    public QueryBuilder<T> whereDate(String dateAsString, String column_date_selection);
    /**
     * Sets the order of table
     *
     * @param i - 1 for ASC, 0 for DESC
     * @return QueryBuilder instance
     */
    public SQLiteQueryService<T> orderBy(int i);
    /**
     * Sets the order of table by Date
     *
     * @param order - true for ASC, false for DESC
     * @return QueryBuilder instance
     */
    public SQLiteQueryService<T> orderByDate(boolean order, String dateColumn);
    /**
     * Limits the number or records to be retrieved
     *
     * @param num - amount of records to be retrieved
     * @return QueryBuilder instance
     */
    public SQLiteQueryService<T> limitBy(int num);

    /**
     * Offsets the records to be retrieved
     *
     * @param num - amount of records to be skipped
     * @return QueryBuilder instance
     */
    public SQLiteQueryService<T> offset(int num);

    /**
     * Returns the sum of specific field.
     *
     * NOTE: Only use this for mathematical operations
     * Do not use when using COUNT
     * @return QueryBuilder instance
     */
    public SQLiteQueryService<T> sum(String columnToSum);
    
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
     * query.delete().one(id).execDelete();
     *
     * Whereas:
     * Table extends the abstract class that implements the interface
     */
    public void execDelete();

    /**
     * Executes the SQL query for update operator:
     * Usage:
     * QueryBuilder<T> query = new Table(db.getWriteableDatabase());
     * query.update(id).setColVal(ColumnName, Value).execUpdate();
     */
    public void execUpdate();

}
