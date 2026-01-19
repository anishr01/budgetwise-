package com.budgetwise.backend.service.impl;

import com.budgetwise.backend.model.Expense;
import com.budgetwise.backend.repository.ExpenseRepository;
import com.budgetwise.backend.service.AiInsightService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AiInsightServiceImpl implements AiInsightService {

    private final ExpenseRepository expenseRepository;

    public AiInsightServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public String generateInsights(Long userId, String monthYear) {

        List<Expense> expenses =
                expenseRepository.findByUserIdAndMonthYear(userId, monthYear);

        if (expenses.isEmpty()) {
            return "No expenses found for the selected month.";
        }

        Map<String, Double> categoryTotals = new HashMap<>();
        double totalExpense = 0;

        for (Expense e : expenses) {
            if ("EXPENSE".equalsIgnoreCase(e.getType())) {
                categoryTotals.merge(
                        e.getCategory(),
                        e.getAmount(),
                        Double::sum
                );
                totalExpense += e.getAmount();
            }
        }

        if (categoryTotals.isEmpty()) {
            return "No expense data available for AI analysis.";
        }

        Map.Entry<String, Double> highest =
                Collections.max(
                        categoryTotals.entrySet(),
                        Map.Entry.comparingByValue()
                );

        double percentage =
                (highest.getValue() / totalExpense) * 100;

        double savingSuggestion =
                highest.getValue() * 0.10;

        return """
                🤖 AI Spending Insights
                -----------------------
                • Total Expense: ₹%.2f
                • Highest Spending Category: %s
                • %.1f%% of your expenses were spent on %s
                • Suggestion: Reduce %s spending by 10%% to save ₹%.2f
                """.formatted(
                totalExpense,
                highest.getKey(),
                percentage,
                highest.getKey(),
                highest.getKey(),
                savingSuggestion
        );
    }
}
