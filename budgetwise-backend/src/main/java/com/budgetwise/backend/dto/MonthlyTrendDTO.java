package com.budgetwise.backend.dto;

public class MonthlyTrendDTO {

    private String month;
    private Double totalExpense;

    public MonthlyTrendDTO(String month, Double totalExpense) {
        this.month = month;
        this.totalExpense = totalExpense;
    }

    public String getMonth() {
        return month;
    }

    public Double getTotalExpense() {
        return totalExpense;
    }
}
