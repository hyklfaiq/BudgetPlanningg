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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class BudgetView extends Application {
    private ComboBox<String> monthSelection;
    private ComboBox<String> categorySelection;
    private TextField amountField;
    private Label feedbackLabel;
    private Label budgetLabel;
    private Label expenseLabel;
    private Label withinBudgetLabel;
    private String userId = "user123";
    private static final String BudgetFile = "BudgetData.txt";
    private static ArrayList<BudgetApp> budgetList = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        // Load budgets from file
        loadBudgetsFromFile();

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
        VBox.setMargin(monthSelection, new Insets(10, 0, 0, 0));

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
        List<String> categories = Arrays.asList("Shopping", "Education", "Electronics", "Entertainment", "Food and Beverages", "Health and Beauty", "Medical", "Shopping", "Transportation", "Other Expenses");
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
        addButton.setOnAction(e -> setBudget());
        monthSelection.setOnAction(e -> displayBudgetStatus());
        categorySelection.setOnAction(e -> displayBudgetStatus());

        root.getChildren().addAll(monthBox, topBox, midBox, bottomBox, withinBudgetBox);

        // Create scene and set on stage
        Scene scene = new Scene(root, 840, 520);
        stage.setTitle("Budget Planning");
        stage.setScene(scene);

        stage.show();

        // Display initial status
        displayBudgetStatus();
    }

    private void setBudget() {
        String month = monthSelection.getValue();
        int monthInt = getMonthAsInt(month);
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
        saveBudget(userId, monthInt, category, tempAmount);

        feedbackLabel.setText("Budget added & updated successfully!");
        displayBudgetStatus();

        // Clear input fields
        amountField.clear();
    }

    private void displayBudgetStatus() {
        String month = monthSelection.getValue();
        int monthInt = getMonthAsInt(month);
        String category = categorySelection.getValue();
        double budgetAmount = readBudget(userId, monthInt, category);
        double[] expenses;
        try {
            expenses = BudgetApp.readAndCalculateExpenses(userId, monthInt);
        } catch (FileNotFoundException e) {
            feedbackLabel.setText("Expenses file not found.");
            return;
        }

        double expenseAmount = 0;

        switch (category) {
            case "Shopping":
                expenseAmount = expenses[0];
                break;
            case "Education":
                expenseAmount = expenses[1];
                break;
            case "Electronics":
                expenseAmount = expenses[2];
                break;
            case "Entertainment":
                expenseAmount = expenses[3];
                break;
            case "Food and Beverages":
                expenseAmount = expenses[4];
                break;
            case "Health and Beauty":
                expenseAmount = expenses[5];
                break;
            case "Medical":
                expenseAmount = expenses[6];
                break;
            case "Transportation":
                expenseAmount = expenses[7];
                break;
            case "Other Expenses":
                expenseAmount = expenses[8];
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

    // Save budget to ArrayList and text file
    private static void saveBudget(String userId, int month, String budgetCategory, double budgetAmount) {
        boolean found = false;

        for (BudgetApp budgetItem : budgetList) {
            if (budgetItem.getUserId().equals(userId) && budgetItem.getMonth()==month && budgetItem.getBudgetCategory().equals(budgetCategory)) {
               budgetItem.setBudgetAmount(budgetAmount);
                found = true;
                break;
            }
        }

        if (!found) {
            BudgetApp BudgetItem = new BudgetApp(userId, month, budgetCategory, budgetAmount);
            budgetList.add(BudgetItem);
        }

        // Write to text file
        try (FileWriter writer = new FileWriter(BudgetFile)) {
            for (BudgetApp budgetItem : budgetList) {
                writer.write(budgetItem.getUserId() + "," + budgetItem.getMonth() + "," + budgetItem.getBudgetCategory() + "," + budgetItem.getBudgetAmount() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    // Read budget from ArrayList
    private static double readBudget(String userId, int month, String category) {
        for (BudgetApp budgetItem : budgetList) {
            if (budgetItem.getUserId().equals(userId) && budgetItem.getMonth() == month && budgetItem.getBudgetCategory().equals(category)) {
                return budgetItem.getBudgetAmount();
            }
        }
        return 0.00; // Default value if no budget is found
    }


    // Load budgets from file into ArrayList
    private static void loadBudgetsFromFile() {
        budgetList.clear();

        try (Scanner scanner = new Scanner(new File(BudgetFile))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] delimiter = line.split(",");
                if (delimiter.length == 4) {
                    BudgetApp budgetItem = new BudgetApp(delimiter[0], Integer.parseInt(delimiter[1]), delimiter[2], Double.parseDouble(delimiter[3]));
                    budgetList.add(budgetItem);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
    }

    private int getMonthAsInt(String month) {
        switch (month) {
            case "JAN":
                return 1;
            case "FEB":
                return 2;
            case "MAR":
                return 3;
            case "APR":
                return 4;
            case "MAY":
                return 5;
            case "JUN":
                return 6;
            case "JUL":
                return 7;
            case "AUG":
                return 8;
            case "SEP":
                return 9;
            case "OCT":
                return 10;
            case "NOV":
                return 11;
            case "DEC":
                return 12;
            default:
                return -1; // invalid
        }
    }


    public static void main(String[] args) throws Exception {
        launch();
    }
}
