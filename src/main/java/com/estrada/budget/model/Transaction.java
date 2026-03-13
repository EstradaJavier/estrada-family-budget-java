package com.estrada.budget.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class Transaction {
    private double amount;
    private LocalDate date;
    private String description;
    private TransactionType type;
    private String category;
    private boolean recurring = false;
    private RecurrenceType recurrenceType = RecurrenceType.MONTHLY;

    public Transaction(double amount, LocalDate date, String description, TransactionType type, String category) {
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.type = type;
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(RecurrenceType recurrenceType) {
        this.recurrenceType = recurrenceType;
    }

    public enum TransactionType {
        INCOME, EXPENSE
    }

    public enum RecurrenceType {
        WEEKLY, MONTHLY, QUARTERLY, YEARLY
    }
}