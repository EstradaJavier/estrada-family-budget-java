package com.estrada.budget.app;

import com.estrada.budget.model.Budget;
import com.estrada.budget.model.Transaction;
import com.estrada.budget.model.BalanceItem;
import com.estrada.budget.util.JsonPersistence;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.scene.shape.Line;


public class App extends Application {

    private Budget budget;
    private JsonPersistence persistence;
    private Label summaryLabel;

    private List<BalanceItem> balances = new ArrayList<>();
    private List<String> upcomingPayments = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        persistence = new JsonPersistence();

        try {
            budget = persistence.load();
        } catch (IOException e) {
            showError("Failed to load budget: " + e.getMessage());
            budget = new Budget();
        }

        loadDefaultBalances();
        loadUpcomingPayments();

        // Header: Title + Date/Temp + Separator
        Label titleLabel = new Label("Estrada Family Budget");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #001F3F; -fx-font-family: Georgia, 'Times New Roman', serif;");
        titleLabel.setAlignment(Pos.CENTER);

        Label dateLabel = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        if (LocalDate.now().getMonthValue() == 2 && LocalDate.now().getDayOfMonth() == 9) {
            dateLabel.setText(dateLabel.getText() + " - Happy Birthday, Javier! 63 today 🎂");
        }
        dateLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #34495e;");

        Label tempLabel = new Label("Current Temp: 72°F (Sunny, Salado TX)");

        HBox headerInfo = new HBox(100, dateLabel, tempLabel);
        headerInfo.setAlignment(Pos.CENTER);

        Line separator = new Line();
        separator.endXProperty().bind(primaryStage.widthProperty().subtract(40));
        separator.setStyle("-fx-stroke: linear-gradient(to right, #001F3F, #2c3e50); -fx-stroke-width: 3;");

        // Main content (scrollable)
        VBox content = new VBox(20);
        content.setAlignment(Pos.TOP_CENTER);

        // Summary Card
        VBox summaryCard = createCard();
        summaryLabel = new Label();
        summaryLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        updateSummaryDisplay();
        summaryCard.getChildren().add(summaryLabel);
        content.getChildren().add(summaryCard);

        // Pop-up Buttons
        Button viewBalancesButton = new Button("View Current Balances");
        viewBalancesButton.setOnAction(e -> showBalancesPopUp());
        content.getChildren().add(viewBalancesButton);

        Button viewUpcomingButton = new Button("View Upcoming Payments");
        viewUpcomingButton.setOnAction(e -> showUpcomingPopUp());
        content.getChildren().add(viewUpcomingButton);

        // Transaction Input Form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));
        form.setAlignment(Pos.CENTER);

        // Darker labels
        Label amountLabel = new Label("Amount:");
        amountLabel.setStyle("-fx-text-fill: #001F3F; -fx-font-weight: bold;");
        TextField amountField = new TextField();
        form.add(amountLabel, 0, 0);
        form.add(amountField, 1, 0);

        Label descLabel = new Label("Description:");
        descLabel.setStyle("-fx-text-fill: #001F3F; -fx-font-weight: bold;");
        TextField descField = new TextField();
        form.add(descLabel, 0, 1);
        form.add(descField, 1, 1);

        Label catLabel = new Label("Category:");
        catLabel.setStyle("-fx-text-fill: #001F3F; -fx-font-weight: bold;");
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll(budget.getCategories().keySet());
        categoryBox.setValue("Groceries");
        form.add(catLabel, 0, 2);
        form.add(categoryBox, 1, 2);

        Label typeLabel = new Label("Type:");
        typeLabel.setStyle("-fx-text-fill: #001F3F; -fx-font-weight: bold;");
        ToggleGroup typeGroup = new ToggleGroup();
        RadioButton incomeBtn = new RadioButton("Income");
        RadioButton expenseBtn = new RadioButton("Expense");
        incomeBtn.setToggleGroup(typeGroup);
        expenseBtn.setToggleGroup(typeGroup);
        expenseBtn.setSelected(true);
        VBox typeBox = new VBox(5, incomeBtn, expenseBtn);
        form.add(typeLabel, 0, 3);
        form.add(typeBox, 1, 3);

        CheckBox recurringCheck = new CheckBox("Recurring?");
        ComboBox<Transaction.RecurrenceType> recurrenceBox = new ComboBox<>();
        recurrenceBox.getItems().addAll(Transaction.RecurrenceType.values());
        recurrenceBox.setValue(Transaction.RecurrenceType.MONTHLY);
        recurrenceBox.setDisable(true);
        recurringCheck.selectedProperty().addListener((obs, old, newVal) -> recurrenceBox.setDisable(!newVal));
        form.add(recurringCheck, 0, 4);
        form.add(recurrenceBox, 1, 4);

        Button addButton = new Button("Add Transaction");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        addButton.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                String desc = descField.getText().trim();
                String cat = categoryBox.getValue();
                Transaction.TransactionType type = expenseBtn.isSelected() ?
                        Transaction.TransactionType.EXPENSE : Transaction.TransactionType.INCOME;

                Transaction t = new Transaction(amount, LocalDate.now(), desc, type, cat);

                if (recurringCheck.isSelected()) {
                    t.setRecurring(true);
                    t.setRecurrenceType(recurrenceBox.getValue());
                }

                budget.addTransaction(t);
                persistence.save(budget);
                updateSummaryDisplay();

                amountField.clear();
                descField.clear();
                recurringCheck.setSelected(false);
                recurrenceBox.setDisable(true);

                showInfo("Transaction added successfully!");

            } catch (NumberFormatException ex) {
                showError("Invalid amount. Enter a number.");
            } catch (IOException ex) {
                showError("Failed to save: " + ex.getMessage());
            }
        });
        form.add(addButton, 1, 5);

        // New buttons
        Button addBillButton = new Button("Add New Bill");
        Button addIncomeButton = new Button("Add New Income");
        HBox newButtons = new HBox(10, addBillButton, addIncomeButton);
        newButtons.setAlignment(Pos.CENTER);
        content.getChildren().add(newButtons);

        content.getChildren().add(form);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox root = new VBox(10, titleLabel, headerInfo, separator, scrollPane);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 800, 900);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        primaryStage.setTitle("Estrada Family Budget");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String getFormattedDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        String dateStr = today.format(formatter);
        if (today.getMonthValue() == 2 && today.getDayOfMonth() == 9) {
            dateStr += " - Happy Birthday, Javier! 63 today 🎂";
        }
        return dateStr;
    }

    private VBox createCard() {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-border-radius: 12; -fx-border-color: #d0d0d0; -fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 4); " +
                "-fx-padding: 20;");
        return card;
    }

    private void loadDefaultBalances() {
        balances.add(new BalanceItem("Sam's Club Mastercard", 2558.83));
        balances.add(new BalanceItem("Citi Card", 6594.60));
        balances.add(new BalanceItem("Mortgage", 0.0));
        balances.add(new BalanceItem("Regions Bank Loan", 0.0));
        balances.add(new BalanceItem("Buckle", 0.0));
        balances.add(new BalanceItem("Cosco", 0.0));
        balances.add(new BalanceItem("AFEES", 0.0));
    }

    private void loadUpcomingPayments() {
        upcomingPayments.add("Jan 15, 2026: Regions Bank $500.00");
        upcomingPayments.add("Jan 30, 2026: DFAS Net Deposit $1,440.52 (income)");
        upcomingPayments.add("Feb 7, 2026: Sam's Club Min $74.00");
        upcomingPayments.add("Feb 24, 2026: iCloud $2.99");
        upcomingPayments.add("Dec 22, 2025: Citi Min $182.30");
        upcomingPayments.add("Monthly: SBP Deduction $212.41");
        upcomingPayments.add("Monthly: TriWest Health $63.75");
        upcomingPayments.add("Monthly: Former Spouse $1,522.29");
        upcomingPayments.add("Monthly: Fed Tax Withheld $18.03");
        upcomingPayments.add("Dec 22, 2027: Tractive Renewal $220.83");
    }

    private void updateSummaryDisplay() {
        Map<String, Object> summary = budget.getMonthlySummary();
        double income = (double) summary.get("income");
        double expense = (double) summary.get("expense");
        double balance = (double) summary.get("balance");

        VBox summaryBox = new VBox(10);
        summaryBox.setAlignment(Pos.CENTER);

        Label title = new Label("Monthly Summary");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #001F3F;");
        summaryBox.getChildren().add(title);

        VBox amounts = new VBox(8);
        amounts.setAlignment(Pos.CENTER);

        Label incLine = new Label("Income: $" + String.format("%.2f", income));
        incLine.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        Label expLine = new Label("Expenses: $" + String.format("%.2f", expense));
        expLine.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        Label balLine = new Label("Balance: $" + String.format("%.2f", balance));
        balLine.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        if (balance >= 0) {
            balLine.setStyle(balLine.getStyle() + " -fx-text-fill: #27ae60;");
        } else {
            balLine.setStyle(balLine.getStyle() + " -fx-text-fill: #e74c3c;");
        }

        amounts.getChildren().addAll(incLine, expLine, balLine);
        summaryBox.getChildren().add(amounts);

        summaryLabel.setGraphic(summaryBox);
    }

    // Pop-up methods, showError, showInfo, createCard, loadDefaultBalances, loadUpcomingPayments remain as before

    private void showBalancesPopUp() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Current Balances");

        VBox popRoot = new VBox(10);
        popRoot.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-spacing: 10;");

        for (BalanceItem item : balances) {
            Label line = new Label(item.getName() + ": $" + String.format("%.2f", item.getBalance()));
            popRoot.getChildren().add(line);
        }

        Scene popScene = new Scene(popRoot, 400, 300);
        popup.setScene(popScene);
        popup.showAndWait();
    }

    private void showUpcomingPopUp() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Upcoming Payments");

        VBox popRoot = new VBox(10);
        popRoot.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-spacing: 10;");

        for (String payment : upcomingPayments) {
            CheckBox checkBox = new CheckBox(payment);
            popRoot.getChildren().add(checkBox);
            checkBox.selectedProperty().addListener((obs, old, newVal) -> {
                if (newVal) {
                    showInfo("Marked as paid: " + payment);
                }
            });
        }

        Scene popScene = new Scene(popRoot, 400, 400);
        popup.setScene(popScene);
        popup.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}