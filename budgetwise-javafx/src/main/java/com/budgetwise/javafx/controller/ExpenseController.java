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

    /* ================= FORM ================= */
    @FXML private ComboBox<String> typeBox;
    @FXML private ComboBox<String> categoryBox;
    @FXML private TextField amountField;
    @FXML private DatePicker datePicker;
    @FXML private TextArea noteArea;
    @FXML private Button backButton;

    /* ================= TABLE ================= */
    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense, LocalDate> dateCol;
    @FXML private TableColumn<Expense, String> typeCol;
    @FXML private TableColumn<Expense, String> categoryCol;
    @FXML private TableColumn<Expense, Double> amountCol;
    @FXML private TableColumn<Expense, String> noteCol;
    @FXML private TableColumn<Expense, Void> actionCol;

    /* ================= BALANCE ================= */
    @FXML private Label balanceLabel;

    /* ================= DATA ================= */
    private final ObservableList<Expense> expenseList = FXCollections.observableArrayList();

    private final ObservableList<String> incomeCategories =
            FXCollections.observableArrayList("Salary", "Bonus", "Interest", "Other Income");

    private final ObservableList<String> expenseCategories =
            FXCollections.observableArrayList("Food", "Transport", "Rent", "Shopping", "Bills", "Other");

    private static final String BASE_URL = "http://localhost:8080/api/expenses";

    private Expense editingExpense = null;

    /* ================= INITIALIZE ================= */
    @FXML
    public void initialize() {

        typeBox.setItems(FXCollections.observableArrayList("INCOME", "EXPENSE"));
        datePicker.setValue(LocalDate.now());

        typeBox.valueProperty().addListener((obs, o, n) -> {
            if ("INCOME".equals(n)) {
                categoryBox.setItems(incomeCategories);
            } else if ("EXPENSE".equals(n)) {
                categoryBox.setItems(expenseCategories);
            }
            categoryBox.setValue(null);
        });

        amountField.textProperty().addListener((obs, old, val) -> {
            if (!val.matches("\\d*(\\.\\d*)?")) {
                amountField.setText(old);
            }
        });

        datePicker.valueProperty().addListener((obs, o, n) -> loadExpensesFromBackend());

        dateCol.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getExpenseDate()));
        typeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getType()));
        categoryCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategory()));
        amountCol.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getAmount()));
        noteCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNote()));

        expenseTable.setItems(expenseList);
        expenseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        addActionButtons();
        loadExpensesFromBackend();
    }

    /* ================= ACTION COLUMN ================= */
    private void addActionButtons() {

        actionCol.setCellFactory(col -> new TableCell<>() {

            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");

            {
                editBtn.setOnAction(e ->
                        loadExpenseToForm(getTableView().getItems().get(getIndex()))
                );
                deleteBtn.setOnAction(e ->
                        deleteExpense(getTableView().getItems().get(getIndex()).getId())
                );
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(8, editBtn, deleteBtn));
            }
        });
    }

    /* ================= SAVE / UPDATE ================= */
    @FXML
    private void handleSave() {

        try {
            if (Session.userId == null) {
                showAlert("User not logged in");
                return;
            }

            if (typeBox.getValue() == null ||
                    categoryBox.getValue() == null ||
                    amountField.getText().isBlank()) {
                showAlert("All fields required");
                return;
            }

            double amount = Double.parseDouble(amountField.getText());
            LocalDate date = datePicker.getValue();

            JSONObject body = new JSONObject();
            body.put("type", typeBox.getValue());
            body.put("category", categoryBox.getValue());
            body.put("amount", amount);
            body.put("expenseDate", date.toString());
            body.put("monthYear", date.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            body.put("note", noteArea.getText());

            HttpRequest request;

            if (editingExpense == null) {
                request = HttpRequest.newBuilder()
                        .uri(new URI(BASE_URL + "/add/" + Session.userId))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .build();
            } else {
                request = HttpRequest.newBuilder()
                        .uri(new URI(BASE_URL + "/" + editingExpense.getId()))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .build();
            }

            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            editingExpense = null;
            clearForm();
            loadExpensesFromBackend();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Save failed");
        }
    }

    /* ================= LOAD TABLE ================= */
    private void loadExpensesFromBackend() {

        try {
            expenseList.clear();

            String monthYear = datePicker.getValue()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM"));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BASE_URL + "/user/" + Session.userId + "?monthYear=" + monthYear))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            JSONArray array = new JSONArray(response.body());
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
                balance += "INCOME".equals(e.getType()) ? e.getAmount() : -e.getAmount();
            }

            balanceLabel.setText("₹" + String.format("%.2f", balance));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= DELETE ================= */
    private void deleteExpense(Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BASE_URL + "/" + id))
                    .DELETE()
                    .build();

            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            loadExpensesFromBackend();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= BACK ================= */
    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/ui/Dashboard.fxml")
            );
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= CLEAR ================= */
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

    private void loadExpenseToForm(Expense e) {
        editingExpense = e;
        typeBox.setValue(e.getType());
        categoryBox.setItems("INCOME".equals(e.getType()) ? incomeCategories : expenseCategories);
        categoryBox.setValue(e.getCategory());
        amountField.setText(String.valueOf(e.getAmount()));
        datePicker.setValue(e.getExpenseDate());
        noteArea.setText(e.getNote());
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}
