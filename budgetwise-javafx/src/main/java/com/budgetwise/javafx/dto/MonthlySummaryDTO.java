package com.budgetwise.javafx.dto;

public class MonthlySummaryDTO {

    private Double totalIncome;
    private Double totalExpense;

    public MonthlySummaryDTO() {}

    public Double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(Double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public Double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(Double totalExpense) {
        this.totalExpense = totalExpense;
    }
}
