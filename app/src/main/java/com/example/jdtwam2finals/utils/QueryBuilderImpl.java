package com.example.jdtwam2finals.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.jdtwam2finals.dto.Expense;
import com.example.jdtwam2finals.utils.QueryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class QueryBuilderImpl<T> implements QueryBuilder<T> {
    protected SQLiteDatabase db;
    public static final Integer ASC = 1;
    public static final Integer DESC = 0;
    protected Map<String, String> conditions = new HashMap<>();
    protected Map<String, String> table_joins = new HashMap<>();
    protected Map<String, String> column_value_update = new HashMap<>();
    protected String column_date;
    protected Boolean orderByDateAsc = null;
    protected String selectedTable;
    protected String operation;
    protected Integer limitBy;
    protected String date_comparator;
    private Boolean isCount = false;
    private String sumOfColumn = null;
    private static QueryBuilder instance = null;
    private String orderBy = "1";
    private boolean orderByAsc = false;
    private String column_date_selection;
    private Integer offset = null;
    private static final String WHERE_CLAUSE = " WHERE ";
    private static final String SELECT_ALL_COLUMN = " * ";
    private static final String LIMIT_CLAUSE = " LIMIT ";
    private static final String FROM_CLAUSE = " FROM ";
    private static final String COUNT_CLAUSE = " COUNT";
    private static final String SUM_CLAUSE = " SUM";
    private static final String OFFSET_CLAUSE =  " OFFSET ";

    public QueryBuilderImpl() {

    }

    public QueryBuilderImpl(SQLiteDatabase db) {
        this.database(db);
    }

    public static synchronized QueryBuilder<?> getAndSetInstance(QueryBuilder<?> setInstance){
        if (instance == null){
            instance = setInstance;
        }
        return instance;
    }
    @Override
    public QueryBuilder<T> database(SQLiteDatabase db) {
        this.db = db;
        return this;
    }
    @Override
    public QueryBuilder<T> delete() {
        this.operation = "DELETE";
        return this;
    }

    @Override
    public QueryBuilder<T> find() {
        this.operation = "SELECT";
        return this;
    }

    @Override
    public QueryBuilder<T> orderBy(int i) {
        if (i == ASC){
            this.orderByAsc = true;
        }else if (i == DESC){
            this.orderByAsc = false;
        }
        return this;
    }

    @Override
    public QueryBuilder<T> limitBy(int num) {
        this.limitBy = num;
        return this;
    }

    @Override
    public SQLiteQueryService offset(int num) {
        this.offset = num;
        return this;
    }

    @Override
    public QueryBuilder<T> relation(String foreignTableName, String foreignKey, String refTableName, String refKey) {
        this.table_joins.put(foreignTableName + "." + foreignKey, refTableName + "." + refKey);
        return this;
    }
    @Override
    public QueryBuilder<T> where(String columnName, String condition, String identifier) {
        StringBuilder sb = new StringBuilder();
        sb.append(columnName);
        sb.append(condition);
        this.conditions.put(sb.toString(), identifier);
        return this;
    }

    @Override
    public QueryBuilder whereDate(String dateAsString, String column_date_selection) {
        this.date_comparator = dateAsString;
        this.column_date_selection = column_date_selection;
        return this;
    }

    @Override
    public QueryBuilder<T> count() {
        this.isCount = true;
        return this;
    }

    @Override
    public UpdateQueryBuilder<T> setNewColVal(String column, String newVal) {
        column_value_update.put(column, newVal);
        return this;
    }

    @Override
    public void execUpdate() {
        try{
            StringBuilder query = new StringBuilder(this.operation);
            query.append(" " + this.selectedTable + " SET ");
            ArrayList<Object> values = new ArrayList<>();
            if (!column_value_update.isEmpty()) {
                boolean isFirst = true;
                for (Map.Entry<String, String> entry : column_value_update.entrySet()) {
                    if (!isFirst) {
                        query.append(", "); // Add a comma for multiple updates
                    } else {
                        isFirst = false;
                    }
                    query.append(entry.getKey()).append(" = ?");
                    values.add(entry.getValue());
                }
            } else {
                throw new IllegalStateException("No columns to update.");
            }

            if (this.conditions.size() != 0){
                String[] formatCondition = formatWhereCondition().split(":");
                String[] conditionArray = formatCondition[1].split(",");
                values.addAll(Arrays.asList(conditionArray));
                query.append(formatCondition[0].toString());
                //Log.d("sqlQuery", query.toString());
            }


            Log.d("sqlQuery", query.toString());
            db.execSQL(query.toString(), values.toArray());
            instance = null;
            //"UPDATE table_name SET column1 = 'new_value' WHERE condition";
        }catch (Exception e) {
            Log.d("execution_db", e.getMessage());
        }
    }

    @Override
    public void execDelete(){
        Log.d("sqlQuery", "in exec delete");
        try{
            StringBuilder query = new StringBuilder(this.operation);
            ArrayList<String> condition_identifier = null;

            if (this.operation.equals("DELETE")) {
                query.append(FROM_CLAUSE + this.selectedTable);
                if (this.conditions.size() != 0){
                    String[] formatCondition = formatWhereCondition().split(":");
                    String[] conditionArray = formatCondition[1].split(",");
                    condition_identifier = new ArrayList<>(Arrays.asList(conditionArray));
                    query.append(formatCondition[0]);
                }
                Log.d("sqlQuery", query.toString());
                db.execSQL(query.toString(), condition_identifier.toArray(new String[condition_identifier.size()]));
                instance = null;
            }else{
                throw new Exception("Query cannot be processed");
            }
        }catch (Exception e){
            Log.d("execution_db", e.getMessage());
        }
    }

    @Override
    public QueryBuilder<T> sum(String columnToSum) {
        this.sumOfColumn = columnToSum;
        return this;
    }

    @Override
    public Cursor exec() {
        Log.d("sqlQuery", "in exec Select");
        try{
            StringBuilder query = new StringBuilder(this.operation);
            ArrayList<String> condition_identifier = null;
            query.append(SELECT_ALL_COLUMN);
            query.append(FROM_CLAUSE + this.selectedTable);

            if (isCount) {
                query = new StringBuilder(this.operation + COUNT_CLAUSE + "(" + SELECT_ALL_COLUMN + ")");
                query.append(FROM_CLAUSE + this.selectedTable);
            }

            if (sumOfColumn != null){
                query = new StringBuilder(this.operation + SUM_CLAUSE + "(" + sumOfColumn + ")");
                query.append(FROM_CLAUSE + this.selectedTable);
                //Log.d("sqlQuery", query.toString());
            }
            if(this.table_joins.size() != 0){
                String joins = formatTableJoins();
                query.append(joins);
                //Log.d("sqlQuery", query.toString());
            }

            if (this.conditions.size() != 0){
                String[] formatCondition = formatWhereCondition().split(":");
                String[] conditionArray = formatCondition[1].split(",");
                condition_identifier = new ArrayList<>(Arrays.asList(conditionArray));
                query.append(formatCondition[0].toString());
                //Log.d("sqlQuery", query.toString());
            }

            if (this.date_comparator != null) {
                StringBuilder date_selector = new StringBuilder();
                if (this.conditions.size() == 0){
                    date_selector.append(WHERE_CLAUSE);
                }else{
                    date_selector.append(" AND ");
                }
                date_selector.append("strftime('%Y-%m-%d', " + column_date_selection + ") = ?");
                //date_selector.append("strftime('%Y-%m-%d', datetime(" + column_date_selection + ",'unixepoch')) = ? ");
                //date_selector.append(column_date_selection + " =? ");
                //date_selector.append("CAST(strftime('%Y-%m-%d', " + column_date_selection + "/1000, unixepoch)) AS TEXT) = ? ");
                query.append(date_selector);

                Log.d("sqlQuery", date_comparator);
                condition_identifier.add(date_comparator);
            }

            if(orderByDateAsc != null){
                query.append(" ORDER BY ");
                query.append("strftime('%Y-%m-%d', datetime(strftime('%s'," + column_date + "), 'unixepoch')) ");
                query.append((orderByDateAsc ? " ASC " : " DESC "));
                query.append(orderByAsc ? ", 1 ASC " : ", 1 DESC ");
            }else{
                query.append(" ORDER BY ");
                query.append(orderBy);
                query.append((orderByAsc ? " ASC " : " DESC "));
            }

            if (limitBy != null) {
                query.append(LIMIT_CLAUSE + limitBy);
            }

            if (offset != null) {
                query.append(OFFSET_CLAUSE + offset);
                //Log.d("sqlQuery", offset.toString());
            }
            Log.d("sqlQuery", condition_identifier.toString());
            Log.d("sqlQuery", query.toString());
            Cursor cursor = this.db.rawQuery(
                    query.toString(),
                    condition_identifier != null ? condition_identifier
                            .toArray(new String[condition_identifier.size()])
                            : null);
            instance = null;
            return cursor;

        }catch(Exception e){
            Log.d("execution_db", e.getMessage());
        }
        instance = null;
        return null;
    }

    /**
     * Private method of class to format String SQL for JOINS
     *
     * @return String
     */
    private String formatTableJoins(){
        if(this.table_joins.size() != 0){
            //Log.d("sqlQuery", "in table joins");

            StringBuilder joins = new StringBuilder();

            for (Map.Entry<String, String> entry : table_joins.entrySet()) {
                joins.append(" INNER JOIN ");
                String foreignKey = entry.getKey();
                String referenceKey = entry.getValue();
                String foreignTable = foreignKey.split("\\.")[0];
                joins.append(foreignTable);
                joins.append(" ON ");
                joins.append(foreignKey + " = " + referenceKey);
            }
            //Log.d("sqlQuery", joins.toString());
            return joins.toString();
        }
        return null;
    }

    /**
     * Private method of class to format WHERE conditions
     *
     * @return String
     */
    private String formatWhereCondition(){
        if (this.conditions.size() != 0) {

            String extractedColumn = WHERE_CLAUSE;
            String extractedIdentifiers = "";

            boolean isLastEntry = false;
            int counter = 0;

            for (Map.Entry<String, String> entry : conditions.entrySet()) {
                isLastEntry = ++counter == conditions.size();
                extractedColumn += entry.getKey()  + "?";
                extractedIdentifiers += entry.getValue();

                if(!isLastEntry){
                    extractedColumn += " AND ";
                    extractedIdentifiers += ",";
                }
            }
            instance = null;
            return extractedColumn + ":" + extractedIdentifiers;
        }
        instance = null;
        return null;
    }

    @Override
    public SQLiteQueryService orderByDate(boolean order, String dateColumn) {
        this.orderByDateAsc = order;
        this.column_date = dateColumn;
        return this;
    }
}
