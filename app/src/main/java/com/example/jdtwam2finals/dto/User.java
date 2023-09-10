package com.example.jdtwam2finals.dto;

import java.util.List;

public class User {
    private Integer userId;
    private String username;
    private String password;
    private List<Transaction> transactions;

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public User(){}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(Integer userId, String username, String password, List<Transaction> transactions) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.transactions = transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public User(Integer userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
