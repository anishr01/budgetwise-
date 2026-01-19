package com.budgetwise.javafx.controller;

import java.time.LocalDate;

public class Expense {

    private LocalDate date;
    private String type;
    private String category;
    private double amount;
    private String note;

    public Expense(LocalDate date, String type, String category, double amount, String note) {
        this.date = date;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.note = note;
    }

    public LocalDate getDate() { return date; }
    public String getType() { return type; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getNote() { return note; }
}
