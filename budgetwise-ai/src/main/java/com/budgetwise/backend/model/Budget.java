package com.budgetwise.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "budgets",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "category", "month_year"}
                )
        }
)
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== USER =====
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // ===== CATEGORY =====
    @Column(nullable = false)
    private String category;

    // ===== BUDGET LIMIT =====
    @Column(name = "monthly_limit", nullable = false)
    private Double monthlyLimit;

    // ===== MONTH (e.g. 2025-01) =====
    @Column(name = "month_year", nullable = false)
    private String monthYear;

    // ================== CONSTRUCTORS ==================
    public Budget() {
    }

    public Budget(Long userId, String category,
                  Double monthlyLimit, String monthYear) {
        this.userId = userId;
        this.category = category;
        this.monthlyLimit = monthlyLimit;
        this.monthYear = monthYear;
    }

    // ================== GETTERS ==================
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getCategory() {
        return category;
    }

    public Double getMonthlyLimit() {
        return monthlyLimit;
    }

    public String getMonthYear() {
        return monthYear;
    }

    // ================== SETTERS ==================
    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setMonthlyLimit(Double monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }
}
