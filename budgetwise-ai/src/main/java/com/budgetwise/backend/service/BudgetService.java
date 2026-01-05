package com.budgetwise.backend.service;

import com.budgetwise.backend.model.Budget;
import com.budgetwise.backend.model.Expense;
import com.budgetwise.backend.repository.BudgetRepository;
import com.budgetwise.backend.repository.ExpenseRepository;
import com.budgetwise.backend.dto.BudgetSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    // ================= SUMMARY (Dashboard) =================
    public List<BudgetSummaryDTO> getBudgetSummary(Long userId, String monthYear) {

        List<Budget> budgets =
                budgetRepository.findByUserIdAndMonthYear(userId, monthYear);

        List<BudgetSummaryDTO> summaryList = new ArrayList<>();

        for (Budget budget : budgets) {

            List<Expense> expenses =
                    expenseRepository.findByUserIdAndCategoryAndMonthYear(
                            userId,
                            budget.getCategory(),
                            monthYear
                    );

            double spent = expenses.stream()
                    .mapToDouble(Expense::getAmount)
                    .sum();

            double limit = budget.getMonthlyLimit();
            double remaining = limit - spent;

            double usagePercent =
                    limit == 0 ? 0 : (spent / limit) * 100;

            summaryList.add(
                    new BudgetSummaryDTO(
                            budget.getCategory(),
                            limit,
                            spent,
                            remaining,
                            usagePercent
                    )
            );
        }

        return summaryList;
    }

    // ================= GET ALL (CRUD UI) =================
    public List<Budget> getBudgetsByUser(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    // ================= ADD =================
    public Budget addBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    // ================= UPDATE =================
    public Budget updateBudget(Long id, Double monthlyLimit) {

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        budget.setMonthlyLimit(monthlyLimit);
        return budgetRepository.save(budget);
    }

    // ================= DELETE =================
    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }
}


