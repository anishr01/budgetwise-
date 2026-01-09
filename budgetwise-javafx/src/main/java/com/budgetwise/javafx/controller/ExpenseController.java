package com.budgetwise.javafx.controller;

import com.budgetwise.javafx.model.Expense;
import com.budgetwise.javafx.util.Session;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ExpenseController {

    // ================= FORM =================
    @FXML private ComboBox<String> typeBox;
    @FXML private ComboBox<String> categoryBox;
    @FXML private TextField amountField;
    @FXML private DatePicker datePicker;
    @FXML private TextArea noteArea;
    @FXML private Button backButton;

    // ================= TABLE =================
    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense, LocalDate> dateCol;
    @FXML private TableColumn<Expense, String> typeCol;
    @FXML private TableColumn<Expense, String> categoryCol;
    @FXML private TableColumn<Expense, Double> amountCol;
    @FXML private TableColumn<Expense, String> noteCol;
    @FXML private TableColumn<Expense, Void> actionCol;

    // ================= BALANCE =================
    @FXML private Label balanceLabel;

    // ================= DATA =================
    private final ObservableList<Expense> expenseList =
            FXCollections.observableArrayList();

    private final ObservableList<String> incomeCategories =
            FXCollections.observableArrayList(
                    "Salary", "Bonus", "Interest", "Other Income"
            );

    private final ObservableList<String> expenseCategories =
            FXCollections.observableArrayList(
                    "Food", "Transport", "Rent", "Shopping", "Bills", "Other"
            );

    private static final String BASE_URL =
            "http://localhost:8080/api/expenses";

    // ================= EDIT MODE =================
    private Expense editingExpense = null;

    // ================= INITIALIZE =================
    @FXML
    public void initialize() {

        typeBox.setItems(FXCollections.observableArrayList("INCOME", "EXPENSE"));
        categoryBox.setPromptText("Select Category");

        // Switch categories based on type
        typeBox.valueProperty().addListener((obs, o, n) -> {
            if ("INCOME".equals(n)) {
                categoryBox.setItems(incomeCategories);
            } else if ("EXPENSE".equals(n)) {
                categoryBox.setItems(expenseCategories);
            }
            categoryBox.setValue(null);
        });

        // Numeric-only amount
        amountField.textProperty().addListener((obs, old, val) -> {
            if (!val.matches("\\d*(\\.\\d*)?")) {
                amountField.setText(old);
            }
        });

        // Table bindings
        dateCol.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().getExpenseDate()));
        typeCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getType()));
        categoryCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCategory()));
        amountCol.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().getAmount()));
        noteCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNote()));

        expenseTable.setItems(expenseList);
        expenseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        addActionButtons();

        // default date = today
        datePicker.setValue(LocalDate.now());

        loadExpensesFromBackend();
    }

    // ================= ACTION COLUMN =================
    private void addActionButtons() {

        actionCol.setCellFactory(col -> new TableCell<>() {

            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");

            {
                editBtn.setStyle("-fx-background-color:#2563eb; -fx-text-fill:white;");
                deleteBtn.setStyle("-fx-background-color:#dc2626; -fx-text-fill:white;");

                editBtn.setOnAction(e -> {
                    Expense expense =
                            getTableView().getItems().get(getIndex());
                    loadExpenseToForm(expense);
                });

                deleteBtn.setOnAction(e -> {
                    Expense expense =
                            getTableView().getItems().get(getIndex());
                    deleteExpense(expense.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(new HBox(8, editBtn, deleteBtn));
            }
        });
    }

    // ================= LOAD TO FORM =================
    private void loadExpenseToForm(Expense expense) {

        editingExpense = expense;

        typeBox.setValue(expense.getType());
        categoryBox.setItems(
                "INCOME".equals(expense.getType())
                        ? incomeCategories
                        : expenseCategories
        );

        categoryBox.setValue(expense.getCategory());
        amountField.setText(String.valueOf(expense.getAmount()));
        datePicker.setValue(expense.getExpenseDate());
        noteArea.setText(expense.getNote());
    }

    // ================= SAVE =================
    @FXML
    private void handleSave() {

        try {
            if (Session.userId == null) {
                showAlert("User not logged in");
                return;
            }

            if (typeBox.getValue() == null ||
                    categoryBox.getValue() == null ||
                    amountField.getText().isBlank() ||
                    datePicker.getValue() == null) {

                showAlert("All fields are required");
                return;
            }

            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                showAlert("Amount must be greater than zero");
                return;
            }

            LocalDate date = datePicker.getValue();
            String monthYear =
                    date.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            JSONObject body = new JSONObject();
            body.put("type", typeBox.getValue());
            body.put("category", categoryBox.getValue());
            body.put("amount", amount);
            body.put("expenseDate", date.toString());
            body.put("monthYear", monthYear);
            body.put("note", noteArea.getText());

            HttpRequest request;

            if (editingExpense != null) {
                request = HttpRequest.newBuilder()
                        .uri(new URI(BASE_URL + "/update/" + editingExpense.getId()))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .build();
            } else {
                request = HttpRequest.newBuilder()
                        .uri(new URI(BASE_URL + "/add/" + Session.userId))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .build();
            }

            HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            clearForm();
            loadExpensesFromBackend();
            showAlert("Saved successfully");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to save expense");
        }
    }

    // ================= LOAD TABLE =================
    private void loadExpensesFromBackend() {

        try {
            if (Session.userId == null) return;

            LocalDate selectedDate =
                    datePicker.getValue() != null
                            ? datePicker.getValue()
                            : LocalDate.now();

            String monthYear =
                    selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(
                            BASE_URL + "/user/" + Session.userId +
                                    "?monthYear=" + monthYear
                    ))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    HttpClient.newHttpClient()
                            .send(request, HttpResponse.BodyHandlers.ofString());

            JSONArray array = new JSONArray(response.body());

            expenseList.clear();
            double balance = 0;

            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);

                Expense e = new Expense();
                e.setId(o.getLong("id"));
                e.setType(o.getString("type"));
                e.setCategory(o.getString("category"));
                e.setAmount(o.getDouble("amount"));
                e.setExpenseDate(LocalDate.parse(o.getString("expenseDate")));
                e.setNote(o.optString("note", ""));

                expenseList.add(e);

                balance += "INCOME".equals(e.getType())
                        ? e.getAmount()
                        : -e.getAmount();
            }

            balanceLabel.setText("₹" + String.format("%.2f", balance));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= DELETE =================
    private void deleteExpense(Long expenseId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BASE_URL + "/delete/" + expenseId))
                    .DELETE()
                    .build();

            HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            loadExpensesFromBackend();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to delete expense");
        }
    }

    // ================= CLEAR =================
    @FXML
    private void handleClear() {
        clearForm();
    }

    private void clearForm() {
        editingExpense = null;
        typeBox.setValue(null);
        categoryBox.setValue(null);
        amountField.clear();
        datePicker.setValue(LocalDate.now());
        noteArea.clear();
    }

    // ================= BACK =================
    @FXML
    private void handleBack() {
        try {
            URL url = getClass().getResource("/ui/Dashboard.fxml");
            Parent root = FXMLLoader.load(url);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= ALERT =================
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}
