package com.budgetwise.javafx.dto;

public class CategoryExpenseDTO {

    private String category;
    private Double totalAmount;

    // Jackson needs empty constructor
    public CategoryExpenseDTO() {
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
