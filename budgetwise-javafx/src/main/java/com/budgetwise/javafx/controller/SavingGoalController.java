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

public class SavingGoalController {

    @FXML
    private TextField goalNameField;

    @FXML
    private TextField targetAmountField;

    @FXML
    private VBox goalList;

    @FXML
    public void initialize() {
        System.out.println("SavingGoal userId = " + Session.userId);
        loadGoals();
    }

    // ================= LOAD SAVING GOALS =================
    private void loadGoals() {
        goalList.getChildren().clear();

        try {
            URL url = new URL(
                    "http://localhost:8080/api/saving-goals/user/" + Session.userId
            );
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner sc = new Scanner(conn.getInputStream());
            StringBuilder json = new StringBuilder();
            while (sc.hasNext()) json.append(sc.nextLine());

            JSONArray array = new JSONArray(json.toString());

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                long id = obj.getLong("id");
                String name = obj.getString("goalName");
                double target = obj.getDouble("targetAmount");
                double saved = obj.getDouble("savedAmount");

                double progress = saved / target;
                double remaining = target - saved;

                // ---------- CARD ----------
                VBox card = new VBox(8);
                card.setMaxWidth(800);
                card.setStyle("""
                        -fx-background-color: white;
                        -fx-padding: 18;
                        -fx-background-radius: 12;
                        -fx-border-radius: 12;
                        -fx-border-color: #e5e7eb;
                """);

                Label title = new Label(name);
                title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

                Label targetLbl = new Label("Target: ₹" + (int) target);
                Label savedLbl = new Label("Saved: ₹" + (int) saved);

                ProgressBar bar = new ProgressBar(progress);
                bar.setPrefWidth(400);

                if (progress >= 1) {
                    bar.setStyle("-fx-accent: green;");
                } else if (progress >= 0.7) {
                    bar.setStyle("-fx-accent: orange;");
                } else {
                    bar.setStyle("-fx-accent: #2563eb;");
                }

                Label remainingLbl = new Label(
                        "Remaining: ₹" + (int) remaining
                );
                remainingLbl.setStyle("-fx-font-weight: bold;");

                Button addMoneyBtn = new Button("Add Money");
                Button deleteBtn = new Button("Delete");

                addMoneyBtn.setStyle(
                        "-fx-background-color: #22c55e; -fx-text-fill: white;"
                );
                deleteBtn.setStyle(
                        "-fx-background-color: #ef4444; -fx-text-fill: white;"
                );

                addMoneyBtn.setOnAction(e -> addMoney(id));
                deleteBtn.setOnAction(e -> deleteGoal(id));

                HBox actions = new HBox(12, addMoneyBtn, deleteBtn);

                card.getChildren().addAll(
                        title,
                        targetLbl,
                        savedLbl,
                        bar,
                        remainingLbl,
                        actions
                );

                goalList.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= ADD GOAL =================
    @FXML
    private void handleAddGoal() {

        String name = goalNameField.getText();
        String targetText = targetAmountField.getText();

        if (name.isEmpty() || targetText.isEmpty()) return;

        try {
            double target = Double.parseDouble(targetText);

            URL url = new URL("http://localhost:8080/api/saving-goals/add");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = """
                    {
                      "userId": %d,
                      "goalName": "%s",
                      "targetAmount": %f
                    }
                    """.formatted(Session.userId, name, target);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
                goalNameField.clear();
                targetAmountField.clear();
                loadGoals();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= ADD MONEY =================
    private void addMoney(long goalId) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Savings");
        dialog.setHeaderText("Enter amount to add");
        dialog.setContentText("Amount:");

        dialog.showAndWait().ifPresent(value -> {
            try {
                double amount = Double.parseDouble(value);

                URL url = new URL(
                        "http://localhost:8080/api/saving-goals/" + goalId + "/save"
                );
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String json = """
                        { "amount": %f }
                        """.formatted(amount);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes());
                }

                if (conn.getResponseCode() == 200) {
                    loadGoals();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // ================= DELETE GOAL =================
    private void deleteGoal(long id) {
        try {
            URL url = new URL(
                    "http://localhost:8080/api/saving-goals/" + id
            );
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            if (conn.getResponseCode() == 200 || conn.getResponseCode() == 204) {
                loadGoals();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= BACK =================
    @FXML
    private void handleBack() {
        try {
            Stage stage = (Stage) goalList.getScene().getWindow();
            stage.setScene(new Scene(
                    FXMLLoader.load(
                            getClass().getResource("/ui/Dashboard.fxml")
                    )
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
