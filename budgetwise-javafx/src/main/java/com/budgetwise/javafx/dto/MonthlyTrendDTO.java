package com.budgetwise.javafx.dto;

public class MonthlyTrendDTO {

    private String month;
    private Double totalExpense;

    // ✅ Required by Jackson
    public MonthlyTrendDTO() {
    }

    // ✅ REQUIRED by JPQL constructor expression
    public MonthlyTrendDTO(String month, Double totalExpense) {
        this.month = month;
        this.totalExpense = totalExpense;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(Double totalExpense) {
        this.totalExpense = totalExpense;
    }
}
