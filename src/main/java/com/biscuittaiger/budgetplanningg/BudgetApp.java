package com.biscuittaiger.budgetplanningg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class BudgetApp {
    private static ArrayList<BudgetApp> budgetList = new ArrayList<>();
    private static final String BudgetFile = "BudgetData.txt";

    private String userId;
    private String month;
    private String budgetCategory;
    private double budgetAmount;

    public BudgetApp(String userId, String month, String budgetCategory, double budgetAmount) {
        this.userId = userId;
        this.month = month;
        this.budgetCategory = budgetCategory;
        this.budgetAmount = budgetAmount;
    }

    // Save budget to ArrayList and text file
    public static void saveBudget(String userId, String month, String budgetCategory, double budgetAmount) {
        boolean found = false;

        for (BudgetApp budgetItem : budgetList) {
            if (budgetItem.userId.equals(userId) && budgetItem.month.equals(month) && budgetItem.budgetCategory.equals(budgetCategory)) {
                budgetItem.budgetAmount = budgetAmount;
                found = true;
                break;
            }
        }

        if (!found) {
            BudgetApp newBudgetItem = new BudgetApp(userId, month, budgetCategory, budgetAmount);
            budgetList.add(newBudgetItem);
        }

        // Write to text file
        try (FileWriter writer = new FileWriter(BudgetFile)) {
            for (BudgetApp budgetItem : budgetList) {
                writer.write(budgetItem.userId + "," + budgetItem.month + "," + budgetItem.budgetCategory + "," + budgetItem.budgetAmount + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    // Read budget from ArrayList
    public static double readBudget(String userId, String month, String category) {
        double budgetAmount = 0.00;

        for (BudgetApp budgetItem : budgetList) {
            if (budgetItem.userId.equals(userId) && budgetItem.month.equals(month) && budgetItem.budgetCategory.equals(category)) {
                budgetAmount = budgetItem.budgetAmount;
                break;
            }
        }

        return budgetAmount;
    }

    // Read expenses from file and calculate expenses for each category
    public static double[] readAndCalculateExpenses(String userId, String month) throws FileNotFoundException {
        double utilitiesExpense = 0, groceriesExpense = 0, transportationExpense = 0, insuranceExpense = 0, otherExpense = 0;

        Scanner readFile = new Scanner(new File("TotalExpense.txt"));
        try {
            while (readFile.hasNext()) {
                String line = readFile.nextLine();
                String[] delimiter = line.split(",");
                if (userId.equals(delimiter[0]) && month.equals(delimiter[1]) && delimiter[3].equals("expense")) {
                    switch (delimiter[4]) {
                        case "utility":
                            utilitiesExpense += Double.parseDouble(delimiter[2]);
                            break;
                        case "groceries":
                            groceriesExpense += Double.parseDouble(delimiter[2]);
                            break;
                        case "transportation":
                            transportationExpense += Double.parseDouble(delimiter[2]);
                            break;
                        case "insurance":
                            insuranceExpense += Double.parseDouble(delimiter[2]);
                            break;
                        case "others":
                            otherExpense += Double.parseDouble(delimiter[2]);
                            break;
                    }
                }
            }
        } finally {
            readFile.close();
        }

        return new double[]{utilitiesExpense, groceriesExpense, transportationExpense, insuranceExpense, otherExpense};
    }

    // Load budgets from file into ArrayList
    public static void loadBudgetsFromFile() {
        budgetList.clear();

        try (Scanner scanner = new Scanner(new File(BudgetFile))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] delimiter = line.split(",");
                if (delimiter.length == 4) {
                    BudgetApp budgetItem = new BudgetApp(delimiter[0], delimiter[1], delimiter[2], Double.parseDouble(delimiter[3]));
                    budgetList.add(budgetItem);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
    }
}
