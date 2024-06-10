package com.biscuittaiger.budgetplanningg;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class BudgetApp {
    private String userId;
    private int month;
    private String budgetCategory;
    private double budgetAmount;

    public BudgetApp(String userId, int month, String budgetCategory, double budgetAmount) {
        this.userId = userId;
        this.month = month;
        this.budgetCategory = budgetCategory;
        this.budgetAmount = budgetAmount;
    }

    public String getUserId() {
        return userId;
    }

    public int getMonth() {
        return month;
    }

    public String getBudgetCategory() {
        return budgetCategory;
    }

    public double getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(double budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    // Read expenses from file and calculate expenses for each category
    public static double[] readAndCalculateExpenses(String userId, int month) throws FileNotFoundException {
        double shoppingExpense = 0, educationExpense = 0, electronicsExpense = 0, entertainmentExpense = 0, foodBeveragesExpense = 0,
                healthBeautyExpense = 0, medicalExpense = 0, transportationExpense = 0, otherExpense = 0;

        Scanner readFile = new Scanner(new File("TotalExpense.txt"));
        try {
            while (readFile.hasNext()) {
                String line = readFile.nextLine();
                String[] delimiter = line.split(",");
                if (userId.equals(delimiter[0]) && month == (Integer.parseInt(delimiter[1])) && delimiter[3].equals("expense")) {
                    switch (delimiter[4]) {
                        case "Shopping":
                            shoppingExpense += Double.parseDouble(delimiter[2]);
                            break;
                        case "Education":
                            educationExpense += Double.parseDouble(delimiter[2]);
                            break;
                        case "Electronics":
                            electronicsExpense += Double.parseDouble(delimiter[2]);
                            break;
                        case "Entertainment":
                            entertainmentExpense += Double.parseDouble(delimiter[2]);
                            break;
                        case "Food and Beverages":
                            foodBeveragesExpense += Double.parseDouble(delimiter[2]);
                            break;
                        case "Health and Beauty":
                            healthBeautyExpense += Double.parseDouble(delimiter[2]);
                            break;
                        case "Medical":
                            medicalExpense += Double.parseDouble(delimiter[2]);
                            break;
                        case "Transportation":
                            transportationExpense += Double.parseDouble(delimiter[2]);
                            break;
                        case "Other Expenses":
                            otherExpense += Double.parseDouble(delimiter[2]);
                            break;
                    }
                }
            }
        } finally {
            readFile.close();
        }

        return new double[]{shoppingExpense, educationExpense, electronicsExpense, entertainmentExpense, foodBeveragesExpense,
                healthBeautyExpense, medicalExpense, transportationExpense, otherExpense};
    }
}
