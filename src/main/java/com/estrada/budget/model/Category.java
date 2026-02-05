package com.estrada.budget.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {

    private String name;
    private double budgetedAmount;
    private double spentThisMonth = 0.0;

    public Category() {
        // Default for Jackson
    }

    public Category(String name, double budgetedAmount) {
        this.name = name;
        this.budgetedAmount = budgetedAmount;
    }

    public void addSpent(double amount) {
        if (amount > 0) {
            this.spentThisMonth += amount;
        }
    }

    public double getRemaining() {
        return budgetedAmount - spentThisMonth;
    }

    public void resetMonthly() {
        spentThisMonth = 0.0;
    }

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getBudgetedAmount() { return budgetedAmount; }
    public void setBudgetedAmount(double budgetedAmount) { this.budgetedAmount = budgetedAmount; }

    public double getSpentThisMonth() { return spentThisMonth; }
    public void setSpentThisMonth(double spentThisMonth) { this.spentThisMonth = spentThisMonth; }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", budgeted=" + budgetedAmount +
                ", spent=" + spentThisMonth +
                ", remaining=" + getRemaining() +
                '}';
    }
}