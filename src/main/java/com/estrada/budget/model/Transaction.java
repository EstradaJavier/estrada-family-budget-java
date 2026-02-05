package com.estrada.budget.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)  // Helps with future JSON changes
public class Transaction {

    private double amount;
    private LocalDate date = LocalDate.now();  // Safe default: today
    private String description;
    private TransactionType type;
    private String categoryName;
    private boolean isRecurring = false;
    private RecurrenceType recurrenceType;
    private LocalDate recurringEndDate;        // Can be null = no end date

    public enum TransactionType {
        INCOME, EXPENSE
    }

    public enum RecurrenceType {
        WEEKLY, MONTHLY, YEARLY
    }

    public Transaction() {
        // Default constructor for Jackson
    }

    public Transaction(double amount, LocalDate date, String description,
                       TransactionType type, String categoryName) {
        this.amount = amount;
        this.date = (date != null) ? date : LocalDate.now();
        this.description = description;
        this.type = type;
        this.categoryName = categoryName;
    }

    // Getters and Setters
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) {
        this.date = (date != null) ? date : LocalDate.now();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public boolean isRecurring() { return isRecurring; }
    public void setRecurring(boolean recurring) { isRecurring = recurring; }

    public RecurrenceType getRecurrenceType() { return recurrenceType; }
    public void setRecurrenceType(RecurrenceType recurrenceType) { this.recurrenceType = recurrenceType; }

    public LocalDate getRecurringEndDate() { return recurringEndDate; }
    public void setRecurringEndDate(LocalDate recurringEndDate) { this.recurringEndDate = recurringEndDate; }

    // Helper: Check if this recurring transaction is still active
    public boolean isActiveOn(LocalDate checkDate) {
        if (!isRecurring) return false;
        if (recurringEndDate != null && checkDate.isAfter(recurringEndDate)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "amount=" + amount +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", category='" + categoryName + '\'' +
                ", recurring=" + (isRecurring ? recurrenceType : "no") +
                (recurringEndDate != null ? ", ends=" + recurringEndDate : "") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Double.compare(that.amount, amount) == 0 &&
                isRecurring == that.isRecurring &&
                Objects.equals(date, that.date) &&
                Objects.equals(description, that.description) &&
                type == that.type &&
                Objects.equals(categoryName, that.categoryName) &&
                recurrenceType == that.recurrenceType &&
                Objects.equals(recurringEndDate, that.recurringEndDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, date, description, type, categoryName, isRecurring, recurrenceType, recurringEndDate);
    }
}