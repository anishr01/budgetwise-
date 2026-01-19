package com.budgetwise.backend.service;

import com.budgetwise.backend.dto.CategoryExpenseDTO;
import com.budgetwise.backend.dto.ExpenseRequestDTO;
import com.budgetwise.backend.dto.MonthlySummaryDTO;
import com.budgetwise.backend.dto.MonthlyTrendDTO;
import com.budgetwise.backend.model.Expense;
import com.budgetwise.backend.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    // =================================================
    // CATEGORY-WISE EXPENSE (PIE CHART)
    // =================================================
    public List<CategoryExpenseDTO> getCategoryWiseExpense(Long userId) {
        return expenseRepository.getCategoryWiseExpense(userId);
    }

    // =================================================
    // ADD EXPENSE
    // =================================================
    public Expense addExpense(Long userId, ExpenseRequestDTO dto) {

        Expense expense = new Expense();
        expense.setUserId(userId);
        expense.setCategory(dto.getCategory());
        expense.setAmount(dto.getAmount());
        expense.setExpenseDate(dto.getExpenseDate());
        expense.setMonthYear(dto.getMonthYear());
        expense.setType(dto.getType());
        expense.setNote(dto.getNote());

        return expenseRepository.save(expense);
    }

    // =================================================
    // GET EXPENSES (LIST)
    // =================================================
    public List<Expense> getExpensesByUserId(
            Long userId,
            String category,
            String monthYear
    ) {
        if (category == null || category.isBlank()) {
            return expenseRepository.findByUserIdAndMonthYear(userId, monthYear);
        }
        return expenseRepository.findByUserIdAndCategoryAndMonthYear(
                userId, category, monthYear
        );
    }

    // =================================================
    // DELETE EXPENSE
    // =================================================
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    // =================================================
    // ✅ INCOME vs EXPENSE (BAR CHART) — FIXED
    // =================================================
    public MonthlySummaryDTO getMonthlySummary(Long userId, String monthYear) {

        Double income = expenseRepository.getTotalIncome(userId, monthYear);
        Double expense = expenseRepository.getTotalExpense(userId, monthYear);

        if (income == null) income = 0.0;
        if (expense == null) expense = 0.0;

        System.out.println("SERVICE → Income = " + income);
        System.out.println("SERVICE → Expense = " + expense);

        return new MonthlySummaryDTO(income, expense);
    }

    // =================================================
    // MONTHLY SPENDING TREND (LINE CHART)
    // =================================================
    public List<MonthlyTrendDTO> getMonthlyExpenseTrend(Long userId) {
        return expenseRepository.getMonthlyTrend(userId);
    }
}
