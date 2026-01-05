package com.budgetwise.javafx.controller;

import com.budgetwise.javafx.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private AnchorPane rootPane;

    private boolean darkMode = false;

    @FXML
    public void initialize() {
        if (Session.fullName != null && !Session.fullName.isEmpty()) {
            welcomeLabel.setText("Welcome, " + Session.fullName);
        } else {
            welcomeLabel.setText("Welcome");
        }
    }

    @FXML
    private void toggleDarkMode() {
        darkMode = !darkMode;

        if (darkMode) {
            if (!rootPane.getStyleClass().contains("dark")) {
                rootPane.getStyleClass().add("dark");
            }
        } else {
            rootPane.getStyleClass().remove("dark");
        }
    }

    // ================= SIDEBAR =================

    @FXML
    private void openDashboard() {
        System.out.println("Dashboard clicked");
    }

    @FXML
    private void openExpenses() {
        switchScene("/ui/expense.fxml", "Expenses");
    }

    @FXML
    private void openBudget() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/ui/BudgetView.fxml"));

            Stage stage = (Stage) rootPane.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 1000, 600); // BIG SIZE

            stage.setScene(scene);
            stage.setTitle("BudgetWise - Budget Plan");
            stage.setMaximized(true); // 🔥 FULL SCREEN LOOK

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void openSavingGoals() {
        switchScene("/ui/SavingGoalView.fxml", "Saving Goals");
    }



    @FXML
    private void openReports() {
        switchScene("/ui/reports.fxml", "Reports");
    }

    @FXML
    private void openSettings() {
        switchScene("/ui/settings.fxml", "Settings");
    }

    private void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource(fxmlPath));

            Stage stage =
                    (Stage) rootPane.getScene().getWindow();

            stage.setScene(new Scene(loader.load()));
            stage.setTitle("BudgetWise - " + title);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= LOGOUT =================

    @FXML
    private void handleLogout() {
        try {
            Session.clear();

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/ui/LoginView.fxml"));

            stage.setScene(new Scene(loader.load()));
            stage.setTitle("BudgetWise - Login");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



