package com.budgetwise.backend.service;

import com.budgetwise.backend.dto.ExpenseRequestDTO;
import com.budgetwise.backend.dto.MonthlySummaryDTO;
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

    // ===== ADD EXPENSE =====
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

    // ===== GET EXPENSES =====
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

    public Expense updateExpense(Long id, ExpenseRequestDTO dto) {

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        expense.setType(dto.getType());
        expense.setCategory(dto.getCategory());
        expense.setAmount(dto.getAmount());
        expense.setExpenseDate(dto.getExpenseDate());
        expense.setMonthYear(dto.getMonthYear());
        expense.setNote(dto.getNote());

        return expenseRepository.save(expense); // 🔥 DB UPDATE
    }


    // ===== DELETE EXPENSE =====
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    // ===== MONTHLY SUMMARY =====
    public MonthlySummaryDTO getMonthlySummary(Long userId, String monthYear) {

        Double totalIncome =
                expenseRepository.getTotalIncome(userId, monthYear);

        Double totalExpense =
                expenseRepository.getTotalExpense(userId, monthYear);

        return new MonthlySummaryDTO(totalIncome, totalExpense);
    }

}

