package com.biscuittaiger.budgetplanningg;

import javafx.application.Application;
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
    private Label withinBudgetLabel;
    private String userId = "user123";

    @Override
    public void start(Stage stage) {
        // Load budgets from file
        BudgetApp.loadBudgetsFromFile();

        VBox root = new VBox(25);
        HBox topBox = new HBox(10);
        VBox midBox = new VBox(20);
        HBox bottomBox = new HBox(40);
        VBox monthBox = new VBox(10);
        VBox categoryBox = new VBox(10);
        HBox amountBox = new HBox(10);
        VBox expenseBox = new VBox(10);
        VBox withinBudgetBox = new VBox(10);
        VBox budgetBox = new VBox(10);

        // monthBox
        monthSelection = new ComboBox<>();
        monthSelection.setMinWidth(200);
        List<String> months = Arrays.asList("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC");
        monthSelection.getItems().addAll(months);
        monthSelection.setValue("JAN");
        monthSelection.setStyle("-fx-font-size: 12 px;");
        monthBox.getChildren().addAll(monthSelection);
        monthBox.setAlignment(Pos.TOP_LEFT);

        //topBox
        //title
        Label titleLabel = new Label("BUDGET PLANNING");
        titleLabel.setAlignment(Pos.TOP_CENTER);
        titleLabel.setStyle("-fx-font-size: 30 px; -fx-font-weight: bold;");
        topBox.getChildren().add(titleLabel);
        topBox.setAlignment(Pos.TOP_CENTER);

        //midBox
        // categoryBox
        categorySelection = new ComboBox<>();
        categorySelection.setMinWidth(200);
        List<String> categories = Arrays.asList("Utility", "Groceries", "Transportation", "Insurance", "Others");
        categorySelection.getItems().addAll(categories);
        categorySelection.setValue("Utility");
        categorySelection.setStyle("-fx-font-size: 16px;");
        Label categoryLabel = new Label("Choose Category");
        categoryBox.getChildren().addAll(categoryLabel, categorySelection);

        // amountBox + budget button + feedback label
        amountField = new TextField();
        amountField.setPromptText("Enter Budget Amount");
        amountField.setMaxSize(200, 40);

        Button addButton = new Button("Set Budget");
        addButton.setStyle("-fx-font-size: 16px;");
        amountBox.getChildren().addAll(amountField, addButton);

        feedbackLabel = new Label();
        feedbackLabel.setTextFill(Color.GREEN);

        amountBox.setAlignment(Pos.CENTER);
        categoryBox.setAlignment(Pos.CENTER);

        midBox.getChildren().addAll(categoryBox, amountBox, feedbackLabel);
        midBox.setAlignment(Pos.CENTER);

        //bottomBox
        //budgetBox
        budgetLabel = new Label();
        budgetBox.getChildren().addAll(budgetLabel);

        //expenseBox
        expenseLabel = new Label();
        expenseBox.getChildren().addAll(expenseLabel);

        budgetLabel.setStyle("-fx-font-size: 16px; -fx-border-color: black; -fx-border-width: 1px; -fx-padding: 5px;");
        expenseLabel.setStyle("-fx-font-size: 16px; -fx-border-color: black; -fx-border-width: 1px; -fx-padding: 5px;");

        bottomBox.getChildren().addAll(budgetBox, expenseBox);
        bottomBox.setAlignment(Pos.CENTER);

        //comparisonBox
        withinBudgetLabel = new Label();
        withinBudgetLabel.setStyle("-fx-font-weight: bold");
        withinBudgetBox.getChildren().addAll(withinBudgetLabel);
        withinBudgetBox.setAlignment(Pos.CENTER);

        withinBudgetLabel.setStyle("-fx-font-size: 20px; -fx-border-color: black; -fx-border-width: 1px; -fx-padding: 5px;");

        // Set up event handlers
        addButton.setOnAction(e -> addBudget());
        monthSelection.setOnAction(e -> withinBudget());
        categorySelection.setOnAction(e -> withinBudget());

        root.getChildren().addAll(monthBox, topBox, midBox, bottomBox, withinBudgetBox);

        // Create scene and set on stage
        Scene scene = new Scene(root, 840, 520);
        stage.setTitle("Budget Planning");
        stage.setScene(scene);

        stage.show();

        // Display within budget for utility
        withinBudget();
    }

    private void addBudget() {
        String month = monthSelection.getValue();
        String category = categorySelection.getValue();
        double tempAmount;

        try {
            tempAmount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Please enter a valid amount.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        // Save budget to ArrayList and file
        BudgetApp.saveBudget(userId, month, category, tempAmount);

        feedbackLabel.setText("Budget added & updated successfully!");
        withinBudget();

        // Clear input fields
        amountField.clear();
    }

    private void withinBudget() {
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

        switch (category) {
            case "Utility":
                expenseAmount = expenses[0];
                break;
            case "Groceries":
                expenseAmount = expenses[1];
                break;
            case "Transportation":
                expenseAmount = expenses[2];
                break;
            case "Insurance":
                expenseAmount = expenses[3];
                break;
            case "Others":
                expenseAmount = expenses[4];
                break;
        }

        budgetLabel.setText("Budgeted:" + String.format("\nRM%.2f", budgetAmount));
        expenseLabel.setText("Total Expense:" + String.format("\nRM%.2f", expenseAmount));

        if (expenseAmount > budgetAmount) {
            withinBudgetLabel.setText("You have exceeded\n the budget!!!");
            withinBudgetLabel.setTextFill(Color.RED);
        } else {
            withinBudgetLabel.setText("You are still within\n the budget!!!");
            withinBudgetLabel.setTextFill(Color.GREEN);
        }
    }


    public static void main(String[] args) {
        launch();
    }
}
