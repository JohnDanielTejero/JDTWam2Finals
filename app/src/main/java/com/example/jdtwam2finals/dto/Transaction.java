package com.example.jdtwam2finals.dto;

import java.util.Date;

public class Transaction {

    private Integer transactionId;
    private String type;
    private Date date;
    private String month;
    private Integer userId;
    private Expense expense;
    private Income income;

    public Transaction() {}
    public Transaction(Integer id, String type, Date date, String month, Integer userId) {
        transactionId = id;
        this.type = type;
        this.date = date;
        this.userId = userId;
        this.month = month;
    }

    public Transaction(String type, Date date, String currentMonth, int userId) {
        this.type = type;
        this.date = date;
        this.userId = userId;
        this.month = currentMonth;
    }


    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public Income getIncome() {
        return income;
    }

    public void setIncome(Income income) {
        this.income = income;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
