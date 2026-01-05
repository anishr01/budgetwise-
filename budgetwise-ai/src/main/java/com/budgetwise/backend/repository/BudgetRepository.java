package com.budgetwise.backend.repository;

import com.budgetwise.backend.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserId(Long userId);

    List<Budget> findByUserIdAndMonthYear(Long userId,String monthYear);
}
