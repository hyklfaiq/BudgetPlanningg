package com.biscuittaiger.budgetplanningg;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class BudgetApp {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getBudgetCategory() {
        return budgetCategory;
    }

    public void setBudgetCategory(String budgetCategory) {
        this.budgetCategory = budgetCategory;
    }

    public double getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(double budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    // Add to file
    public void SaveBudget() {
        ArrayList<String> budgetFileContent = new ArrayList<>();
        boolean found = false;

        try (Scanner scanner = new Scanner(new File("BudgetData.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] delimiter = line.split(",");
                if (delimiter[0].equals(userId) && delimiter[1].equals(month) && delimiter[2].equals(budgetCategory)) {
                    delimiter[3] = String.valueOf(budgetAmount); // update the amount
                    found = true;
                }
                budgetFileContent.add(String.join(",", delimiter));
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }

        if (!found) {
            budgetFileContent.add(userId + "," + month + "," + budgetCategory + "," + budgetAmount);
        }

        try (PrintWriter out = new PrintWriter(new FileWriter("BudgetData.txt"))) {
            for (String line : budgetFileContent) {
                out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    // Read budget for a specific month and category
    public static double readBudget(String userId, String month, String category) {
        double budgetAmount = 0.0;

        try (Scanner scanner = new Scanner(new File("BudgetData.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] delimiter = line.split(",");
                if (delimiter[0].equals(userId) && delimiter[1].equals(month) && delimiter[2].equals(category)) {
                    budgetAmount = Double.parseDouble(delimiter[3]);
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }

        return budgetAmount;
    }

    public static double[] readAndCalculateExpenses(String userId, String month) throws FileNotFoundException {
        double utilitiesExpense = 0, groceriesExpense = 0, transportationExpense = 0, insuranceExpense = 0, otherExpense = 0;

        Scanner readFile = new Scanner(new File("C://Users//faiqb//OneDrive//Desktop//Coding//BudgetPlanningg//TotalExpense.txt"));
        try {
            while (readFile.hasNext()) {
                String line = readFile.nextLine();
                String[] delimiter = line.split(",");
                if (userId.equals(delimiter[0]) && month.equals(delimiter[1]) && delimiter[3].equals("expense")) {
                    if (delimiter[4].equals("utility")) {
                        utilitiesExpense += Double.parseDouble(delimiter[2]);
                    } else if (delimiter[4].equals("groceries")) {
                        groceriesExpense += Double.parseDouble(delimiter[2]);
                    } else if (delimiter[4].equals("transportation")) {
                        transportationExpense += Double.parseDouble(delimiter[2]);
                    } else if (delimiter[4].equals("insurance")) {
                       insuranceExpense += Double.parseDouble(delimiter[2]);
                    } else if (delimiter[4].equals("others")) {
                        otherExpense += Double.parseDouble(delimiter[2]);
                    }
                }
            }
        } finally {
            readFile.close();
        }

        return new double[]{utilitiesExpense, groceriesExpense, transportationExpense, insuranceExpense, otherExpense};
    }


}
