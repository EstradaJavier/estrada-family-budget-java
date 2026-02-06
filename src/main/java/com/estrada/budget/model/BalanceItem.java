package com.estrada.budget.model;

public class BalanceItem {
    private String name;
    private double balance;

    public BalanceItem(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return name + ": $" + String.format("%.2f", balance);
    }
}