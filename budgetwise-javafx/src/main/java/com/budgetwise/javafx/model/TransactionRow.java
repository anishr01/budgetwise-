package com.budgetwise.javafx.model;

import javafx.beans.property.*;

public class TransactionRow {

    private final StringProperty date = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final DoubleProperty amount = new SimpleDoubleProperty();
    private final StringProperty note = new SimpleStringProperty();

    public TransactionRow(String date, String type,
                          String category, double amount, String note) {
        this.date.set(date);
        this.type.set(type);
        this.category.set(category);
        this.amount.set(amount);
        this.note.set(note);
    }

    public StringProperty dateProperty() { return date; }
    public StringProperty typeProperty() { return type; }
    public StringProperty categoryProperty() { return category; }
    public DoubleProperty amountProperty() { return amount; }
    public StringProperty noteProperty() { return note; }
}
