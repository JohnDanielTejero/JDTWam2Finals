package com.example.jdtwam2finals.dto;

public class Income {

    private Integer incomeId;
    private Double amount;
    private String note;
    private Integer transactionId;

    public Income () {}
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
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

    public Integer getIncomeId() {
        return incomeId;
    }

    public void setIncomeId(Integer incomeId) {
        this.incomeId = incomeId;
    }

    public Income(Integer id, Double amount, String note, Integer transactionId) {
        this.incomeId = id;
        this.amount = amount;
        this.note = note;
        this.transactionId = transactionId;
    }
    public Income(Double amount, String note, Integer transactionId) {
        this.amount = amount;
        this.note = note;
        this.transactionId = transactionId;
    }
}
