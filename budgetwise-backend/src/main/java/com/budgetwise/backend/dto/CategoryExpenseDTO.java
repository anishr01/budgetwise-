package com.budgetwise.backend.dto;

public class CategoryExpenseDTO {

    private String category;
    private Double totalAmount;

    // ✅ JPQL-safe constructor
    public CategoryExpenseDTO(String category, Number totalAmount) {
        this.category = category;
        this.totalAmount = totalAmount.doubleValue();
    }

    public String getCategory() {
        return category;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }
}

