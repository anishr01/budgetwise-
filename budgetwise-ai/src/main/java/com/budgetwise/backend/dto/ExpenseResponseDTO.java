package com.budgetwise.backend.dto;

import java.time.LocalDate;

public class ExpenseResponseDTO {

    private Long id;
    private String type;
    private String category;
    private Double amount;
    private LocalDate expenseDate;
    private String monthYear;
    private String note;

    // ===== CONSTRUCTOR =====
    public ExpenseResponseDTO(
            Long id,
            String type,
            String category,
            Double amount,
            LocalDate expenseDate,
            String monthYear,
            String note
    ) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.monthYear = monthYear;
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

    public String getMonthYear() {
        return monthYear;
    }

    public String getNote() {
        return note;
    }
}
