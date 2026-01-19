package com.budgetwise.javafx.controller;

import com.budgetwise.javafx.dto.CategoryExpenseDTO;
import com.budgetwise.javafx.dto.MonthlySummaryDTO;
import com.budgetwise.javafx.dto.MonthlyTrendDTO;
import com.budgetwise.javafx.service.ExpenseApiService;
import com.budgetwise.javafx.util.Session;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import javafx.stage.FileChooser;


public class DashboardController {

    // ================= UI =================
    @FXML private AnchorPane rootPane;
    @FXML private Label welcomeLabel;

    @FXML private PieChart expensePieChart;
    @FXML private BarChart<String, Number> incomeExpenseBarChart;
    @FXML private LineChart<String, Number> monthlyTrendLineChart;

    @FXML
    private javafx.scene.control.TextArea aiInsightArea;


    // ================= SERVICE =================
    private final ExpenseApiService expenseApiService =
            new ExpenseApiService();

    // ================= INITIALIZE =================
    @FXML
    public void initialize() {

        System.out.println("Logged in userId = " + Session.userId);

        welcomeLabel.setText(
                Session.fullName != null
                        ? "Welcome, " + Session.fullName
                        : "Welcome"
        );

        loadExpensePieChart();
        loadIncomeExpenseChart();
        loadMonthlyTrendChart();
        loadAiInsights();
    }

    // ================= DARK MODE =================
    @FXML
    private void toggleDarkMode() {
        if (rootPane.getStyleClass().contains("dark")) {
            rootPane.getStyleClass().remove("dark");
        } else {
            rootPane.getStyleClass().add("dark");
        }
    }

    // ================= PIE CHART =================
    private void loadExpensePieChart() {

        List<CategoryExpenseDTO> data =
                expenseApiService.getCategoryExpenseSummary(Session.userId);

        Platform.runLater(() -> {
            expensePieChart.getData().clear();

            for (CategoryExpenseDTO dto : data) {
                expensePieChart.getData().add(
                        new PieChart.Data(
                                dto.getCategory(),
                                dto.getTotalAmount()
                        )
                );
            }
        });
    }

    // ================= BAR CHART =================
    private void loadIncomeExpenseChart() {

        // current month automatically
        LocalDate now = LocalDate.now();
        String monthYear =
                now.getYear() + "-" + String.format("%02d", now.getMonthValue());

        System.out.println("Dashboard → monthYear = " + monthYear);

        MonthlySummaryDTO summary =
                expenseApiService.getMonthlySummary(
                        Session.userId,
                        monthYear
                );

        if (summary == null) {
            summary = new MonthlySummaryDTO(0.0, 0.0);
        }

        MonthlySummaryDTO finalSummary = summary;

        Platform.runLater(() -> {

            incomeExpenseBarChart.getData().clear();

            CategoryAxis xAxis =
                    (CategoryAxis) incomeExpenseBarChart.getXAxis();
            xAxis.setCategories(
                    javafx.collections.FXCollections.observableArrayList(
                            "Income", "Expense"
                    )
            );

            XYChart.Series<String, Number> series =
                    new XYChart.Series<>();
            series.setName("Income vs Expense");

            series.getData().add(
                    new XYChart.Data<>("Income",
                            finalSummary.getTotalIncome())
            );
            series.getData().add(
                    new XYChart.Data<>("Expense",
                            finalSummary.getTotalExpense())
            );

            incomeExpenseBarChart.getData().add(series);
        });
    }

    // ================= LINE CHART =================
    private void loadMonthlyTrendChart() {

        List<MonthlyTrendDTO> trends =
                expenseApiService.getMonthlyTrend(Session.userId);

        Platform.runLater(() -> {

            monthlyTrendLineChart.getData().clear();

            XYChart.Series<String, Number> series =
                    new XYChart.Series<>();
            series.setName("Monthly Expense Trend");

            for (MonthlyTrendDTO dto : trends) {
                series.getData().add(
                        new XYChart.Data<>(
                                dto.getMonth(),
                                dto.getTotalExpense()
                        )
                );
            }

            monthlyTrendLineChart.getData().add(series);
        });
    }

    // ================= AI INSIGHTS =================
    private void loadAiInsights() {

        try {
            Long userId = Session.userId;

            LocalDate now = LocalDate.now();
            String monthYear =
                    now.getYear() + "-" + String.format("%02d", now.getMonthValue());

            String url =
                    "http://localhost:8080/api/ai/insights/"
                            + userId + "?monthYear=" + monthYear;

            java.net.http.HttpClient client =
                    java.net.http.HttpClient.newHttpClient();

            java.net.http.HttpRequest request =
                    java.net.http.HttpRequest.newBuilder()
                            .uri(java.net.URI.create(url))
                            .GET()
                            .build();

            java.net.http.HttpResponse<String> response =
                    client.send(
                            request,
                            java.net.http.HttpResponse.BodyHandlers.ofString()
                    );

            aiInsightArea.setText(response.body());

        } catch (Exception e) {
            aiInsightArea.setText("AI insights unavailable.");
            e.printStackTrace();
        }
    }



    // ================= DOWNLOAD REPORT =================
    @FXML
    private void downloadCsv() {
        downloadFile("csv");
    }

    @FXML
    private void downloadPdf() {
        downloadFile("pdf");
    }

    private void downloadFile(String type) {

        try {
            Long userId = Session.userId;

            // current month-year
            LocalDate now = LocalDate.now();
            String monthYear =
                    now.getYear() + "-" + String.format("%02d", now.getMonthValue());

            String url =
                    "http://localhost:8080/api/reports/"
                            + type + "/" + userId
                            + "?monthYear=" + monthYear;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<byte[]> response =
                    client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            FileChooser chooser = new FileChooser();
            chooser.setInitialFileName(
                    "budget-report-" + monthYear + "." + type
            );

            File file =
                    chooser.showSaveDialog(rootPane.getScene().getWindow());

            if (file != null) {
                Files.write(file.toPath(), response.body());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ================= NAVIGATION =================
    @FXML
    private void openExpenses() {
        switchScene("/ui/Expense.fxml", "Expense Tracking");
    }

    @FXML
    private void openBudget() {
        switchScene("/ui/BudgetView.fxml", "Budget");
    }

    @FXML
    private void openSavingGoals() {
        switchScene("/ui/SavingGoalView.fxml", "Saving Goals");
    }

    @FXML
    private void openReports() {
        switchScene("/ui/ReportsView.fxml", "Reports");
    }

    @FXML
    private void openSettings() {
        switchScene("/ui/SettingsView.fxml", "Settings");
    }

    private void switchScene(String fxmlPath, String title) {
        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) return;

            Parent root = FXMLLoader.load(url);
            Stage stage =
                    (Stage) rootPane.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("BudgetWise - " + title);
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Session.clear();
            Stage stage =
                    (Stage) rootPane.getScene().getWindow();

            Parent root =
                    FXMLLoader.load(
                            getClass().getResource("/ui/LoginView.fxml")
                    );

            stage.setScene(new Scene(root));
            stage.setMaximized(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
