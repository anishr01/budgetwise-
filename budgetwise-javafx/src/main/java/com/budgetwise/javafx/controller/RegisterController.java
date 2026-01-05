package com.budgetwise.javafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class RegisterController {

    // ================= FIELDS =================
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;

    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;

    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmPasswordTextField;

    @FXML private Label errorLabel;

    private static final String REGISTER_URL =
            "http://localhost:8080/api/auth/register";

    // ================= REGISTER =================
    @FXML
    private void handleRegister() {
        hideError();

        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();

        String password = passwordField.isVisible()
                ? passwordField.getText()
                : passwordTextField.getText();

        String confirmPassword = confirmPasswordField.isVisible()
                ? confirmPasswordField.getText()
                : confirmPasswordTextField.getText();

        // ---------- Validations ----------
        if (fullName.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("All fields are required");
            return;
        }

        if (!isValidEmail(email)) {
            showError("Invalid email format");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        if (!isValidPassword(password)) {
            showError(
                    "Password must contain:\n" +
                            "• Minimum 8 characters\n" +
                            "• Uppercase letter (A-Z)\n" +
                            "• Lowercase letter (a-z)\n" +
                            "• Number (0-9)\n" +
                            "• Special character (@$!%*?&)"
            );
            return;
        }

        // ---------- API Call ----------
        try {
            URL url = new URL(REGISTER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            String json = """
            {
              "fullName": "%s",
              "email": "%s",
              "password": "%s"
            }
            """.formatted(fullName, email, password);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();

            if (code == 201) {
                goToLogin();
            } else if (code == 409) {
                showError("Email already exists");
            } else {
                showError("Registration failed (Error code: " + code + ")");
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

    @FXML
    private void toggleConfirmPassword() {
        if (confirmPasswordField.isVisible()) {
            confirmPasswordTextField.setText(confirmPasswordField.getText());
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
            confirmPasswordTextField.setVisible(true);
            confirmPasswordTextField.setManaged(true);
        } else {
            confirmPasswordField.setText(confirmPasswordTextField.getText());
            confirmPasswordTextField.setVisible(false);
            confirmPasswordTextField.setManaged(false);
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
        }
    }

    // ================= NAVIGATION =================
    @FXML
    private void goToLogin() {
        try {
            Stage stage = (Stage) fullNameField.getScene().getWindow();
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/ui/LoginView.fxml"));
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("BudgetWise - Login");
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

    private boolean isValidPassword(String password) {
        String regex =
                "^(?=.*[a-z])" +
                        "(?=.*[A-Z])" +
                        "(?=.*\\d)" +
                        "(?=.*[@$!%*?&])" +
                        ".{8,}$";

        return password.matches(regex);
    }
}

