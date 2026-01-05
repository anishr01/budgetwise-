package com.budgetwise.javafx.controller;

import com.budgetwise.javafx.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class BudgetController {

    @FXML private TextField categoryField;
    @FXML private TextField limitField;
    @FXML private VBox budgetList;

    @FXML
    public void initialize() {
        System.out.println("Budget userId = " + Session.userId);
        loadBudgets();
    }

    // =====================================================
    // LOAD BUDGETS (CRUD API)
    // =====================================================
    private void loadBudgets() {

        budgetList.getChildren().clear();

        try {
            URL url = new URL(
                    "http://localhost:8080/api/budget/user/" + Session.userId
            );

            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner sc = new Scanner(conn.getInputStream());
            StringBuilder json = new StringBuilder();
            while (sc.hasNext()) json.append(sc.nextLine());

            JSONArray array = new JSONArray(json.toString());

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                long budgetId = obj.getLong("id");
                String categoryText = obj.getString("category");
                double limitValue = obj.getDouble("monthlyLimit");

                // For now, spent = 0 (can be connected to expenses later)
                double spentValue = 0;
                double remainingValue = limitValue - spentValue;

                // ---------------- CARD ----------------
                VBox card = new VBox(8);
                card.setMaxWidth(800);
                card.setStyle("""
                    -fx-background-color: #ffffff;
                    -fx-padding: 18;
                    -fx-background-radius: 12;
                    -fx-border-radius: 12;
                    -fx-border-color: #e5e7eb;
                """);

                Label category = new Label(categoryText);
                category.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

                Label limit = new Label("Limit: ₹" + (int) limitValue);
                Label spent = new Label("Spent: ₹" + (int) spentValue);

                Label remaining = new Label(
                        "Remaining: ₹" + (int) remainingValue
                );
                remaining.setStyle(
                        "-fx-text-fill: green; -fx-font-weight: bold;"
                );

                HBox actions = new HBox(12);
                Button edit = new Button("Edit");
                Button delete = new Button("Delete");

                edit.setStyle(
                        "-fx-background-color: #2196F3; -fx-text-fill: white;"
                );
                delete.setStyle(
                        "-fx-background-color: #f44336; -fx-text-fill: white;"
                );

                edit.setOnAction(e ->
                        editBudget(budgetId, limitValue)
                );

                delete.setOnAction(e ->
                        deleteBudget(budgetId)
                );

                actions.getChildren().addAll(edit, delete);

                card.getChildren().addAll(
                        category,
                        limit,
                        spent,
                        remaining,
                        actions
                );

                budgetList.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // ADD BUDGET
    // =====================================================
    @FXML
    private void addBudget() {

        if (categoryField.getText().isEmpty()
                || limitField.getText().isEmpty()) {
            return;
        }

        try {
            double limit = Double.parseDouble(limitField.getText());

            URL url = new URL(
                    "http://localhost:8080/api/budget/add"
            );

            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty(
                    "Content-Type", "application/json"
            );
            conn.setDoOutput(true);

            String json = """
                {
                  "userId": %d,
                  "category": "%s",
                  "monthlyLimit": %f,
                  "monthYear": "2025-12"
                }
                """.formatted(
                    Session.userId,
                    categoryField.getText(),
                    limit
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            if (conn.getResponseCode() == 200
                    || conn.getResponseCode() == 201) {

                categoryField.clear();
                limitField.clear();
                loadBudgets();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // EDIT BUDGET
    // =====================================================
    private void editBudget(long id, double oldLimit) {

        TextInputDialog dialog =
                new TextInputDialog(String.valueOf(oldLimit));

        dialog.setTitle("Edit Budget");
        dialog.setHeaderText("Update Monthly Limit");

        dialog.showAndWait().ifPresent(value -> {
            try {
                double newLimit = Double.parseDouble(value);

                URL url = new URL(
                        "http://localhost:8080/api/budget/" + id
                );

                HttpURLConnection conn =
                        (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("PUT");
                conn.setRequestProperty(
                        "Content-Type", "application/json"
                );
                conn.setDoOutput(true);

                String json = """
                    {
                      "monthlyLimit": %f
                    }
                    """.formatted(newLimit);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes());
                }

                if (conn.getResponseCode() == 200) {
                    loadBudgets();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // =====================================================
    // DELETE BUDGET
    // =====================================================
    private void deleteBudget(long id) {

        try {
            URL url = new URL(
                    "http://localhost:8080/api/budget/" + id
            );

            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            if (conn.getResponseCode() == 200
                    || conn.getResponseCode() == 204) {

                loadBudgets();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // BACK TO DASHBOARD
    // =====================================================
    @FXML
    private void handleBack() {

        try {
            Stage stage =
                    (Stage) budgetList.getScene().getWindow();

            stage.setScene(new Scene(
                    FXMLLoader.load(
                            getClass().getResource(
                                    "/ui/Dashboard.fxml"
                            )
                    )
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
