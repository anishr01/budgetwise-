package com.budgetwise.javafx.model;

import java.time.LocalDate;

public class Expense {

    private Long id;
    private String type;      // INCOME / EXPENSE
    private String category;
    private Double amount;
    private LocalDate expenseDate;
    private String note;

    // 🔥 REQUIRED: No-arg constructor
    public Expense() {
    }

    // Optional: parameterized constructor
    public Expense(Long id, String type, String category,
                   Double amount, LocalDate expenseDate, String note) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.note = note;
    }

    // ===== GETTERS =====
    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public Double getAmount() {
        return amount;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public String getNote() {
        return note;
    }

    // ===== SETTERS =====
    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
