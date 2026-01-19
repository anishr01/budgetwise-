package com.budgetwise.javafx.dto;

public class MonthlySummaryDTO {

    private Double totalIncome;
    private Double totalExpense;

    // ✅ REQUIRED: no-args constructor
    public MonthlySummaryDTO() {
    }

    // ✅ REQUIRED: 2-arg constructor (YOU ARE MISSING THIS)
    public MonthlySummaryDTO(Double totalIncome, Double totalExpense) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
    }

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
