package com.biscuittaiger.budgetplanningg;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class BudgetView extends Application {
    private ComboBox<String> monthSelection;
    private ComboBox<String> categorySelection;
    private TextField amountField;
    private Label feedbackLabel;
    private Label budgetLabel;
    private Label expenseLabel;
    private Label comparisonLabel;
    private String userId = "user123";

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(50);
        HBox box = new HBox(40);
        VBox leftBox = new VBox(40);
        VBox rightBox = new VBox(40);
        VBox monthSection = new VBox(10);
        VBox categorySection = new VBox(10);
     VBox amountSection = new VBox(10);



        // Title
        Label titleLabel = new Label("Budget Tracker");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        // monthsection
        monthSelection = new ComboBox<>();
        monthSelection.setMinWidth(200);
        List<String> months = Arrays.asList("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC");
        monthSelection.getItems().addAll(months);
        monthSelection.setValue("JAN");
        Label monthLabel = new Label("Choose Month");
        monthSection.getChildren().addAll(monthLabel,monthSelection);

        // categorySection
        categorySelection = new ComboBox<>();
        categorySelection.setMinWidth(200);
        List<String> categories = Arrays.asList("Utility", "Groceries", "Transportation", "Insurance", "Others");
        categorySelection.getItems().addAll(categories);
        categorySelection.setValue("Utility");
        Label categoryLabel = new Label("Choose Category");
        categorySection.getChildren().addAll(categoryLabel,categorySelection);


        // amountSection + budget button
        amountField = new TextField();
        amountField.setPromptText("Enter Budget Amount Here");
        amountField.setMaxSize(200, 20);
        Button addButton = new Button("Set Budget");
        amountSection.getChildren().addAll(amountField,addButton);

        // Feedback label
        feedbackLabel = new Label();
        feedbackLabel.setTextFill(Color.GREEN);

        // Labels for displaying budget and expenses
        budgetLabel = new Label();
        expenseLabel = new Label();

        // Comparison result label
        comparisonLabel = new Label();
        comparisonLabel.setStyle("-fx-font-weight: bold;");

        comparisonLabel.setStyle("-fx-border-color: #000000;");
        budgetLabel.setStyle("-fx-border-color: #000000;");
        expenseLabel.setStyle("-fx-border-color: #000000;");

        // Set up event handlers
        addButton.setOnAction(e -> addBudget());
        monthSelection.setOnAction(e -> displayComparison());
        categorySelection.setOnAction(e -> displayComparison());
        titleLabel.setAlignment(Pos.TOP_CENTER);

        // getchildren
        leftBox.getChildren().addAll(monthSection, categorySection, amountSection, feedbackLabel);
        rightBox.getChildren().addAll(budgetLabel, expenseLabel, comparisonLabel);
        box.getChildren().addAll(leftBox, rightBox);
        box.setAlignment(Pos.CENTER);
        root.getChildren().addAll(titleLabel, box);
        root.setAlignment(Pos.CENTER);


        // Create scene and set on stage
        Scene scene = new Scene(root, 800, 500);
        stage.setTitle("Budget Planning");
        stage.setScene(scene);

        stage.show();

        // Display initial comparison
        displayComparison();
    }

    private void addBudget() {
        String month = monthSelection.getValue();
        String category = categorySelection.getValue();
        double amount = 0;

        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Please enter a valid amount.");
            return;
        }

        // Create a BudgetApp object and save to file
        BudgetApp budget = new BudgetApp(userId, month, category, amount);
        budget.SaveBudget();

        feedbackLabel.setText("Budget added/updated successfully!");
        displayComparison();

        // Clear input fields
        amountField.clear();
    }

    private void displayComparison() {
        String month = monthSelection.getValue();
        String category = categorySelection.getValue();
        double budgetAmount = BudgetApp.readBudget(userId, month, category);
        double[] expenses;
        try {
            expenses = BudgetApp.readAndCalculateExpenses(userId, month);
        } catch (FileNotFoundException e) {
            feedbackLabel.setText("Expenses file not found.");
            return;
        }

        double expenseAmount = 0;
        if (category.equals("Utility")) {
            expenseAmount = expenses[0];
        } else if (category.equals("Groceries")) {
            expenseAmount = expenses[1];
        } else if (category.equals("Transportation")) {
            expenseAmount = expenses[2];
        } else if (category.equals("Insurance")) {
            expenseAmount = expenses[3];
        } else if (category.equals("Others")) {
            expenseAmount = expenses[4];
        }

        budgetLabel.setText("Budget for " + category + " in " + month + ": RM" + budgetAmount);
        expenseLabel.setText("Total expenses for " + category + " in " + month + ": RM" + expenseAmount);

        if (expenseAmount > budgetAmount) {
            comparisonLabel.setText("You have exceeded the budget!");
            comparisonLabel.setTextFill(Color.RED);

        } else if (expenseAmount <= budgetAmount) {
            comparisonLabel.setText("You are within the budget!");
            comparisonLabel.setTextFill(Color.GREEN);
        }
    }

    public static void main(String[] args) throws Exception {
        launch();
    }
}
