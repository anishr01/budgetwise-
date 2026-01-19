package com.budgetwise.backend.repository;

import com.budgetwise.backend.dto.CategoryExpenseDTO;
import com.budgetwise.backend.dto.MonthlyTrendDTO;
import com.budgetwise.backend.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // =============================
    // LIST EXPENSES
    // =============================
    List<Expense> findByUserIdAndMonthYear(
            Long userId,
            String monthYear
    );

    List<Expense> findByUserIdAndCategoryAndMonthYear(
            Long userId,
            String category,
            String monthYear
    );

    // =============================
    // TOTAL INCOME
    // =============================
    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.userId = :userId
          AND e.monthYear = :monthYear
          AND e.type = 'INCOME'
    """)
    Double getTotalIncome(
            @Param("userId") Long userId,
            @Param("monthYear") String monthYear
    );

    // =============================
    // TOTAL EXPENSE
    // =============================
    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.userId = :userId
          AND e.monthYear = :monthYear
          AND e.type = 'EXPENSE'
    """)
    Double getTotalExpense(
            @Param("userId") Long userId,
            @Param("monthYear") String monthYear
    );

    // =============================
    // CATEGORY-WISE EXPENSE
    // =============================
    @Query("""
        SELECT new com.budgetwise.backend.dto.CategoryExpenseDTO(
            e.category,
            SUM(e.amount)
        )
        FROM Expense e
        WHERE e.userId = :userId
          AND e.type = 'EXPENSE'
        GROUP BY e.category
    """)
    List<CategoryExpenseDTO> getCategoryWiseExpense(
            @Param("userId") Long userId
    );

    // =============================
    // MONTHLY TREND
    // =============================
    @Query("""
        SELECT new com.budgetwise.backend.dto.MonthlyTrendDTO(
            e.monthYear,
            SUM(e.amount)
        )
        FROM Expense e
        WHERE e.userId = :userId
          AND e.type = 'EXPENSE'
        GROUP BY e.monthYear
        ORDER BY e.monthYear ASC
    """)
    List<MonthlyTrendDTO> getMonthlyTrend(
            @Param("userId") Long userId
    );
}

