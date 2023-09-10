package com.example.jdtwam2finals.dto;

public class Expense {
    private Double amount;
    private String category;
    private String note;
    private Integer transactionId;

    public Expense(Double amount, String category, String note, Integer transactionId) {
        this.amount = amount;
        this.category = category;
        this.note = note;
        this.transactionId = transactionId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }
}
