package com.budgetwise.backend.dto;

public class SavingGoalDTO {

    private String goalName;
    private Double remaining;
    private Double progress;

    // 1️⃣ No-args constructor (MANDATORY for Jackson)
    public SavingGoalDTO() {
    }

    // 2️⃣ Parameterized constructor
    public SavingGoalDTO(String goalName, Double remaining, Double progress) {
        this.goalName = goalName;
        this.remaining = remaining;
        this.progress = progress;
    }

    // 3️⃣ Getters and Setters
    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public Double getRemaining() {
        return remaining;
    }

    public void setRemaining(Double remaining) {
        this.remaining = remaining;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }
}

