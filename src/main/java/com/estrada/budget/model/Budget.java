package com.estrada.budget.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.YearMonth;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Budget {

    private YearMonth currentMonth = YearMonth.now();
    private Map<String, Category> categories = new HashMap<>();
    private List<Transaction> transactions = new ArrayList<>();

    public Budget() {
        this.currentMonth = YearMonth.now();

        // Updated categories from your February statement + dog training
        addCategory(new Category("Javier's Military Retirement", 4298.00));
        addCategory(new Category("Alesha's Payroll", 3608.00));
        addCategory(new Category("Tax Refunds / Other Income", 0.00));

        addCategory(new Category("Mortgage", 3303.00));
        addCategory(new Category("Amazon / Online Shopping", 400.00));
        addCategory(new Category("Pet Care", 200.00));
        addCategory(new Category("Pet Training / Boarding", 0.00));   // ← For Darious & Bear
        addCategory(new Category("Subscriptions & Streaming", 100.00));
        addCategory(new Category("T-Mobile", 262.00));
        addCategory(new Category("Groceries", 650.00));
        addCategory(new Category("Dining Out / Fast Food", 250.00));
        addCategory(new Category("Utilities", 380.00));
        addCategory(new Category("Credit Card Payments", 500.00));
        addCategory(new Category("Former Spouse Support", 1522.00));
        addCategory(new Category("SBP Costs", 212.00));
        addCategory(new Category("Health Insurance / TriWest", 64.00));
        addCategory(new Category("Miscellaneous", 300.00));
    }

    public void addCategory(Category category) {
        if (category != null && category.getName() != null) {
            categories.put(category.getName(), category);
        }
    }

    public void addTransaction(Transaction t) {
        if (t != null) {
            transactions.add(t);
            if (t.getType() == Transaction.TransactionType.INCOME) {
                // Income does nothing to categories
            } else {
                Category cat = categories.get(t.getCategory());
                if (cat != null) cat.addSpent(t.getAmount());
            }
        }
    }

    public Map<String, Object> getMonthlySummary() {
        double totalIncome = 0.0;
        double totalExpense = 0.0;

        for (Transaction t : transactions) {
            if (t.getType() == Transaction.TransactionType.INCOME) {
                totalIncome += t.getAmount();
            } else {
                totalExpense += t.getAmount();
            }
        }

        double balance = totalIncome - totalExpense;

        Map<String, Double> categorySpent = new HashMap<>();
        for (Map.Entry<String, Category> entry : categories.entrySet()) {
            categorySpent.put(entry.getKey(), entry.getValue().getSpentThisMonth());
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("month", currentMonth.toString());
        summary.put("income", totalIncome);
        summary.put("expense", totalExpense);
        summary.put("balance", balance);
        summary.put("categorySpent", categorySpent);

        summary.put("safetyWarning", balance < 1000 ?
                "⚠️ WARNING: Projected balance may drop below safe buffer ($1,000)!" :
                "✅ Safe buffer maintained");

        return summary;
    }

    public YearMonth getCurrentMonth() { return currentMonth; }
    public void setCurrentMonth(YearMonth currentMonth) {
        this.currentMonth = (currentMonth != null) ? currentMonth : YearMonth.now();
    }

    public Map<String, Category> getCategories() { return categories; }
    public List<Transaction> getTransactions() { return transactions; }
}