package com.budgetwise.backend.dto;

import java.time.LocalDate;

public class ExpenseRequestDTO {

    private String type;          // INCOME / EXPENSE
    private String category;
    private Double amount;
    private LocalDate expenseDate;
    private String monthYear;
    private String note;          // 🔥 NEW

    // ===== GETTERS =====
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

    public String getMonthYear() {
        return monthYear;
    }

    public String getNote() {
        return note;
    }

    // ===== SETTERS =====
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

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
