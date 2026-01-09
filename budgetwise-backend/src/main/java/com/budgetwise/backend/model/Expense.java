package com.budgetwise.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Column(name = "month_year", nullable = false)
    private String monthYear;

    @Column(nullable = false)
    private String type; // INCOME / EXPENSE

    @Column(length = 255)
    private String note;

    // ===== GETTERS =====
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
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

    public String getType() {
        return type;
    }

    public String getNote() {
        return note;
    }

    // ===== SETTERS =====
    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public void setType(String type) {
        this.type = type;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
