package com.budgetwise.backend.dto;

public class MonthlySummaryDTO {

    private Double totalIncome;
    private Double totalExpense;

    public MonthlySummaryDTO(Double totalIncome, Double totalExpense) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
    }

    public Double getTotalIncome() {
        return totalIncome;
    }

    public Double getTotalExpense() {
        return totalExpense;
    }
}
