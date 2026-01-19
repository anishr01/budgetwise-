package com.budgetwise.backend.dto;

public class BudgetSummaryDTO {

    private String category;
    private Double limit;
    private Double spent;
    private Double remaining;
    private Double usagePercent;

    public BudgetSummaryDTO(
            String category,
            Double limit,
            Double spent,
            Double remaining,
            Double usagePercent
    ) {
        this.category = category;
        this.limit = limit;
        this.spent = spent;
        this.remaining = remaining;
        this.usagePercent = usagePercent;
    }

    public String getCategory() { return category; }
    public Double getLimit() { return limit; }
    public Double getSpent() { return spent; }
    public Double getRemaining() { return remaining; }
    public Double getUsagePercent() { return usagePercent; }
}
