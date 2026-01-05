package com.budgetwise.backend.dto;

public class SavingGoalDTO {

    private String goalName;
    private Double remaining;
    private Double progress;

    public SavingGoalDTO(String goalName, Double remaining, Double progress) {
        this.goalName = goalName;
        this.remaining = remaining;
        this.progress = progress;
    }

    // getters & setters
}

