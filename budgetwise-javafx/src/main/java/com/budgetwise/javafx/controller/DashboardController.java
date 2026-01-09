package com.budgetwise.javafx.controller;

import com.budgetwise.javafx.dto.CategoryExpenseDTO;
import com.budgetwise.javafx.dto.MonthlySummaryDTO;
import com.budgetwise.javafx.dto.MonthlyTrendDTO;
import com.budgetwise.javafx.service.ExpenseApiService;
import com.budgetwise.javafx.util.Session;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.List;

public class DashboardController {

    // ================= UI =================
    @FXML private Label welcomeLabel;
    @FXML private AnchorPane rootPane;

    @FXML private PieChart expensePieChart;
    @FXML private BarChart<String, Number> incomeExpenseBarChart;
    @FXML private LineChart<String, Number> monthlyTrendLineChart;

    @FXML private ComboBox<Integer> yearCombo;
    @FXML private ComboBox<String> monthCombo;

    // ================= SERVICE =================
    private final ExpenseApiService expenseApiService = new ExpenseApiService();
    private boolean darkMode = false;

    // ================= INITIALIZE =================
    @FXML
    public void initialize() {

        welcomeLabel.setText(
                Session.fullName != null && !Session.fullName.isEmpty()
                        ? "Welcome, " + Session.fullName
                        : "Welcome"
        );

        setupFilters();
        reloadCharts();   // 🔥 LOAD ALL CHARTS
    }

    // ================= FILTER SETUP =================
    private void setupFilters() {

        yearCombo.setItems(
                FXCollections.observableArrayList(2024, 2025, 2026)
        );
        yearCombo.setValue(2026);

        monthCombo.setItems(
                FXCollections.observableArrayList(
                        "Jan","Feb","Mar","Apr","May","Jun",
                        "Jul","Aug","Sep","Oct","Nov","Dec"
                )
        );
        monthCombo.setValue("Jan");

        yearCombo.setOnAction(e -> reloadCharts());
        monthCombo.setOnAction(e -> reloadCharts());
    }

    private void reloadCharts() {
        loadExpensePieChart();
        loadIncomeExpenseChart();
        loadMonthlyTrendChart();
    }

    // ================= 1️⃣ CATEGORY-WISE PIE =================
    private void loadExpensePieChart() {

        expensePieChart.getData().clear();

        Long userId = Session.userId != null ? Session.userId : 1L;

        List<CategoryExpenseDTO> data =
                expenseApiService.getCategoryWiseExpense(userId);

        if (data == null || data.isEmpty()) return;

        for (CategoryExpenseDTO dto : data) {
            expensePieChart.getData().add(
                    new PieChart.Data(
                            dto.getCategory(),
                            dto.getTotalAmount()
                    )
            );
        }
    }

    // ================= 2️⃣ INCOME vs EXPENSE BAR =================
    private void loadIncomeExpenseChart() {

        incomeExpenseBarChart.getData().clear();

        Long userId = Session.userId != null ? Session.userId : 1L;
        String monthYear = getSelectedMonthYear();

        MonthlySummaryDTO summary =
                expenseApiService.getMonthlySummary(userId, monthYear);

        if (summary == null) return;

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Amount");

        series.getData().add(
                new XYChart.Data<>("Income", summary.getTotalIncome())
        );
        series.getData().add(
                new XYChart.Data<>("Expense", summary.getTotalExpense())
        );

        incomeExpenseBarChart.getData().add(series);
    }

    // ================= 3️⃣ MONTHLY SPENDING TREND (FIXED) =================
    private void loadMonthlyTrendChart() {

        monthlyTrendLineChart.getData().clear();

        Long userId = Session.userId != null ? Session.userId : 1L;
        Integer selectedYear = yearCombo.getValue();

        List<MonthlyTrendDTO> data =
                expenseApiService.getMonthlyTrend(userId);

        if (data == null || data.isEmpty()) return;

        // ✅ FILTER ONLY SELECTED YEAR
        data = data.stream()
                .filter(d -> d.getMonth().startsWith(selectedYear.toString()))
                .sorted((a, b) -> a.getMonth().compareTo(b.getMonth()))
                .toList();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Expense");

        for (MonthlyTrendDTO dto : data) {
            series.getData().add(
                    new XYChart.Data<>(
                            formatMonth(dto.getMonth()),
                            dto.getTotalExpense()
                    )
            );
        }

        monthlyTrendLineChart.getData().add(series);
    }

    // ================= HELPERS =================
    private String getSelectedMonthYear() {

        int year = yearCombo.getValue();
        int monthIndex = monthCombo.getSelectionModel().getSelectedIndex() + 1;

        return String.format("%d-%02d", year, monthIndex);
    }

    private String formatMonth(String monthYear) {
        // "2026-01" → "Jan 2026"
        String[] parts = monthYear.split("-");
        int month = Integer.parseInt(parts[1]);

        String[] months = {
                "Jan","Feb","Mar","Apr","May","Jun",
                "Jul","Aug","Sep","Oct","Nov","Dec"
        };

        return months[month - 1] + " " + parts[0];
    }

    // ================= DARK MODE =================
    @FXML
    private void toggleDarkMode() {
        darkMode = !darkMode;
        if (darkMode) rootPane.getStyleClass().add("dark");
        else rootPane.getStyleClass().remove("dark");
    }

    // ================= NAVIGATION =================
    @FXML private void openExpenses() {
        switchScene("/ui/expense.fxml", "Expenses");
    }

    @FXML private void openBudget() {
        switchScene("/ui/BudgetView.fxml", "Budget Plan");
    }

    @FXML private void openSavingGoals() {
        switchScene("/ui/SavingGoalView.fxml", "Saving Goals");
    }

    @FXML private void openReports() {
        switchScene("/ui/reports.fxml", "Reports");
    }

    @FXML private void openSettings() {
        switchScene("/ui/settings.fxml", "Settings");
    }

    private void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("BudgetWise - " + title);
            stage.setMaximized(true);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
