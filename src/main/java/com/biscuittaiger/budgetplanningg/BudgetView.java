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
    private Label comparisonLabel;
    private String userId = "user123";

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(50);
        HBox box = new HBox(40);
        VBox leftBox = new VBox(40);
        VBox rightBox = new VBox(40);
        VBox monthBox = new VBox(10);
        VBox categoryBox = new VBox(10);
        VBox amountBox = new VBox(10);
        VBox expenseBox = new VBox(10);
        VBox comparisonBox = new VBox(10);
        VBox budgetBox = new VBox(10);


        // Title
        Label titleLabel = new Label("Budget Planning");
        titleLabel.setAlignment(Pos.TOP_CENTER);
        titleLabel.setStyle("-fx-font-size: 30 px; -fx-font-weight: bold;");

        // monthBox
        monthSelection = new ComboBox<>();
        monthSelection.setMinWidth(200);
        List<String> months = Arrays.asList("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC");
        monthSelection.getItems().addAll(months);
        monthSelection.setValue("JAN");
        monthSelection.setStyle("-fx-font-size: 16px;");
        Label monthLabel = new Label("Choose Month");
        monthBox.getChildren().addAll(monthLabel, monthSelection);

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
        amountField.setPromptText("Enter Budget Amount Here");
        amountField.setMaxSize(200, 20);
        Button addButton = new Button("Set Budget");
        addButton.setStyle("-fx-font-size: 16px;");
        feedbackLabel = new Label();
        feedbackLabel.setTextFill(Color.GREEN);
        amountBox.getChildren().addAll(amountField, addButton, feedbackLabel);


        //budgetBox
        budgetLabel = new Label();
        budgetBox.getChildren().addAll(budgetLabel);



        //expenseBox
        expenseLabel = new Label();
        expenseBox.getChildren().addAll(expenseLabel);


        //comparisonBox
        comparisonLabel = new Label();
        comparisonBox.getChildren().addAll(comparisonLabel);
        comparisonLabel.setStyle("-fx-font-weight: bold");

        budgetLabel.setStyle("-fx-font-size: 16px; -fx-border-color: black; -fx-border-width: 1px; -fx-padding: 5px;");
        expenseLabel.setStyle("-fx-font-size: 16px; -fx-border-color: black; -fx-border-width: 1px; -fx-padding: 5px;");
        comparisonLabel.setStyle("-fx-font-size: 18px; -fx-border-color: black; -fx-border-width: 1px; -fx-padding: 5px;");



        // Set up event handlers
        addButton.setOnAction(e -> addBudget());
        monthSelection.setOnAction(e -> displayComparison());
        categorySelection.setOnAction(e -> displayComparison());


        // getchildren
        leftBox.getChildren().addAll(monthBox, categoryBox, amountBox);
        rightBox.getChildren().addAll(budgetBox, expenseBox, comparisonBox);
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
        double tempAmount = 0;

        try {
            tempAmount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Please enter a valid amount.");
            return;
        }

        // Create a BudgetApp object and save to file
        BudgetApp budget = new BudgetApp(userId, month, category, tempAmount);
        budget.SaveBudget();

        feedbackLabel.setText("Budget added & updated successfully!");
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


        budgetLabel.setText("Total Budget For " + category + String.format("\n\t%.2f", budgetAmount));

        expenseLabel.setText("Total Expense For " + category + String.format("\n\t%.2f", expenseAmount));



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
