package com.example.jdtwam2finals.dto;

import java.util.Date;

public class Transaction {

    private String type;
    private Date date;
    private Integer userId;
    private Expense expense;
    private Income income;

    public Transaction() {}
    public Transaction(String type, Date date, Integer userId) {
        this.type = type;
        this.date = date;
        this.userId = userId;
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
