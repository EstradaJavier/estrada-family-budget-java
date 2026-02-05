package com.estrada.budget.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Budget {

    private YearMonth currentMonth = YearMonth.now();  // Always initialized
    private Map<String, Category> categories = new HashMap<>();
    private List<Transaction> transactions = new ArrayList<>();

    public Budget() {
        this.currentMonth = YearMonth.now();  // Double safety
        // Default categories (feel free to change/add)
        addCategory(new Category("Groceries", 800.0));
        addCategory(new Category("Rent/Mortgage", 1500.0));
        addCategory(new Category("Utilities", 300.0));
        addCategory(new Category("Salary", 5000.0));
        addCategory(new Category("Entertainment", 200.0));
        addCategory(new Category("Transportation", 400.0));
    }

    public void addCategory(Category category) {
        if (category != null && category.getName() != null) {
            categories.put(category.getName(), category);
        }
    }

    public void addTransaction(Transaction t) {
        if (t != null) {
            transactions.add(t);
            if (t.getType() == Transaction.TransactionType.EXPENSE) {
                Category cat = categories.get(t.getCategoryName());
                if (cat != null) {
                    cat.addSpent(t.getAmount());
                }
            }
        }
    }

    // Apply recurring transactions for the current month (called in getMonthlySummary)
    public void applyRecurringForMonth(YearMonth month) {
        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();

        List<Transaction> recurring = transactions.stream()
                .filter(Transaction::isRecurring)
                .filter(t -> t.getRecurrenceType() != null)
                .filter(t -> t.isActiveOn(monthEnd))  // Still active
                .toList();

        for (Transaction base : recurring) {
            LocalDate next = base.getDate();
            while (next.isBefore(monthStart) || next.isEqual(monthStart)) {
                next = calculateNextDate(next, base.getRecurrenceType());
            }
            while (!next.isAfter(monthEnd)) {
                Transaction copy = new Transaction(
                        base.getAmount(),
                        next,
                        base.getDescription() + " (recurring)",
                        base.getType(),
                        base.getCategoryName()
                );
                copy.setRecurring(true);
                copy.setRecurrenceType(base.getRecurrenceType());
                copy.setRecurringEndDate(base.getRecurringEndDate());
                addTransaction(copy);
                next = calculateNextDate(next, base.getRecurrenceType());
            }
        }
    }

    private LocalDate calculateNextDate(LocalDate current, Transaction.RecurrenceType type) {
        return switch (type) {
            case WEEKLY  -> current.plusWeeks(1);
            case MONTHLY -> current.plusMonths(1);
            case YEARLY  -> current.plusYears(1);
        };
    }

    public Map<String, Object> getMonthlySummary() {
        applyRecurringForMonth(currentMonth);  // Ensure recurring are applied

        double totalIncome = transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.INCOME)
                .filter(t -> YearMonth.from(t.getDate()).equals(currentMonth))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                .filter(t -> YearMonth.from(t.getDate()).equals(currentMonth))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double balance = totalIncome - totalExpense;

        Map<String, Double> categorySpent = categories.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getSpentThisMonth()
                ));

        Map<String, Object> summary = new HashMap<>();
        summary.put("month", currentMonth.toString());
        summary.put("income", totalIncome);
        summary.put("expense", totalExpense);
        summary.put("balance", balance);
        summary.put("categorySpent", categorySpent);
        return summary;
    }

    // Getters
    public YearMonth getCurrentMonth() { return currentMonth; }
    public void setCurrentMonth(YearMonth currentMonth) {
        this.currentMonth = (currentMonth != null) ? currentMonth : YearMonth.now();
    }

    public Map<String, Category> getCategories() { return categories; }
    public List<Transaction> getTransactions() { return transactions; }
}