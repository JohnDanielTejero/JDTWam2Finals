package com.example.jdtwam2finals.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
    protected String selectedTable;
    protected String operation;
    protected Integer limitBy;
    private Boolean isCount = false;
    private String sumOfColumn = null;

    public QueryBuilderImpl(SQLiteDatabase db) {
        this.database(db);
    }

    private String orderBy = "1";
    private boolean orderByAsc = false;
    private static final String WHERE_CLAUSE = " WHERE ";
    private static final String SELECT_ALL_COLUMN = " * ";
    private static final String LIMIT_CLAUSE = " LIMIT ";
    private static final String FROM_CLAUSE = " FROM ";
    private static final String COUNT_CLAUSE = " COUNT";
    private static final String SUM_CLAUSE = " SUM";

    public QueryBuilderImpl() {

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
    public QueryBuilder<T> count() {
        this.isCount = true;
        return this;
    }

    @Override
    public void execDelete(){
        Log.d("sqlQuery", "in exec");
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
        Log.d("sqlQuery", "in exec");
        try{
            StringBuilder query = new StringBuilder(this.operation);
            ArrayList<String> condition_identifier = null;

            if (this.operation.equals("SELECT")){
                query.append(SELECT_ALL_COLUMN);
                query.append(FROM_CLAUSE + this.selectedTable);

                if (isCount) {
                    query = new StringBuilder(this.operation + COUNT_CLAUSE + "(" + SELECT_ALL_COLUMN + ")");
                    query.append(FROM_CLAUSE + this.selectedTable);
                }

                if (sumOfColumn != null){
                    query = new StringBuilder(this.operation + SUM_CLAUSE + "(" + sumOfColumn + ")");
                    query.append(FROM_CLAUSE + this.selectedTable);
                    Log.d("sqlQuery", query.toString());
                }
                if(this.table_joins.size() != 0){
                    String joins = formatTableJoins();
                    query.append(joins);
                    Log.d("sqlQuery", query.toString());
                }

                if (this.conditions.size() != 0){
                    String[] formatCondition = formatWhereCondition().split(":");
                    String[] conditionArray = formatCondition[1].split(",");
                    condition_identifier = new ArrayList<>(Arrays.asList(conditionArray));
                    query.append(formatCondition[0].toString());
                    Log.d("sqlQuery", query.toString());

                }

                query.append(" ORDER BY ");
                query.append(orderBy);
                query.append((orderByAsc == true ? " ASC " : " DESC "));

                if (limitBy != null) {
                    query.append(LIMIT_CLAUSE + limitBy);
                }

            }else{
                throw new Exception("Query cannot be processed");
            }
            Log.d("sqlQuery", query.toString());
            return this.db.rawQuery(
                    query.toString(),
                    condition_identifier != null ? condition_identifier
                            .toArray(new String[condition_identifier.size()])
                            : null);

        }catch(Exception e){
            Log.d("execution_db", e.getMessage());
        }
        return null;
    }

    /**
     * Private method of class to format String SQL for JOINS
     *
     * @return String
     */
    private String formatTableJoins(){
        if(this.table_joins.size() != 0){
            Log.d("sqlQuery", "in table joins");

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
            Log.d("sqlQuery", joins.toString());
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

            String extractedConditions = WHERE_CLAUSE;
            String extractedIdentifiers = "";

            boolean isLastEntry = false;
            int counter = 0;

            for (Map.Entry<String, String> entry : conditions.entrySet()) {
                isLastEntry = ++counter == conditions.size();
                extractedConditions += entry.getKey()  + "?";
                extractedIdentifiers += entry.getValue();

                if(!isLastEntry){
                    extractedConditions += " AND ";
                    extractedIdentifiers += ",";
                }
            }
            return extractedConditions + ":" + extractedIdentifiers;
        }
        return null;
    }
}
