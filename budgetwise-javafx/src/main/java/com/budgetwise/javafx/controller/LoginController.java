package com.budgetwise.javafx.controller;

import com.budgetwise.javafx.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Pattern;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private Label errorLabel;

    private static final String LOGIN_URL =
            "http://localhost:8080/api/auth/login";

    // ================= LOGIN =================
    @FXML
    private void handleLogin() {

        hideError();

        String email = emailField.getText().trim();
        String password = passwordField.isVisible()
                ? passwordField.getText()
                : passwordTextField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Email and password required");
            return;
        }

        if (!isValidEmail(email)) {
            showError("Invalid email format");
            return;
        }

        try {
            URL url = new URL(LOGIN_URL);
            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = """
                    {
                      "email": "%s",
                      "password": "%s"
                    }
                    """.formatted(email, password);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {

                // ✅ READ RESPONSE BODY
                InputStream is = conn.getInputStream();
                String response =
                        new Scanner(is).useDelimiter("\\A").next();

                JSONObject userJson = new JSONObject(response);

                // ✅ SAVE SESSION DATA
                Session.userId = userJson.getLong("id");
                Session.email = userJson.getString("email");
                Session.fullName = userJson.getString("fullName");

                System.out.println("Logged in userId = " + Session.userId);

                openDashboard();

            } else {
                showError("Invalid credentials");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Server not reachable");
        }
    }

    // ================= PASSWORD TOGGLE =================
    @FXML
    private void togglePassword() {
        if (passwordField.isVisible()) {
            passwordTextField.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
        } else {
            passwordField.setText(passwordTextField.getText());
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
        }
    }

    // ================= NAVIGATION =================
    @FXML
    private void goToRegister() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(
                    FXMLLoader.load(
                            getClass().getResource("/ui/RegisterView.fxml")
                    )
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openDashboard() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/ui/Dashboard.fxml"));

            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);   // 🔥 THIS MAKES IT FULL PAGE
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ================= HELPERS =================
    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                email
        );
    }
}





