package com.budgetwise.javafx.dto;

public class MonthlyTrendDTO {

    private String month;
    private Double totalExpense;

    public MonthlyTrendDTO() {}

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
