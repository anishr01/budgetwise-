package com.budgetwise.backend.repository;

import com.budgetwise.backend.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // 🔥 REQUIRED IMPORT

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserIdAndCategoryAndMonthYear(
            Long userId,
            String category,
            String monthYear
    );

    List<Expense> findByUserIdAndMonthYear(
            Long userId,
            String monthYear
    );

    // ===== TOTAL INCOME =====
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

    // ===== TOTAL EXPENSE =====
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
}
