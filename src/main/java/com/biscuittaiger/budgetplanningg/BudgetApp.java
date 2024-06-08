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



    // Save budget to file
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
        double budgetAmount = 0.00;

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
}
