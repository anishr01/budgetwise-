package com.budgetwise.backend.controller;

import com.budgetwise.backend.dto.BudgetSummaryDTO;
import com.budgetwise.backend.model.Budget;
import com.budgetwise.backend.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budget")
@CrossOrigin
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    // =====================================================
    // DASHBOARD SUMMARY (used in dashboard charts/cards)
    // =====================================================
    @GetMapping("/summary")
    public List<BudgetSummaryDTO> getBudgetSummary(
            @RequestParam Long userId,
            @RequestParam String monthYear
    ) {
        return budgetService.getBudgetSummary(userId, monthYear);
    }

    // =====================================================
    // GET ALL BUDGETS (used in Budget CRUD UI)
    // =====================================================
    @GetMapping("/user/{userId}")
    public List<Budget> getBudgetsByUser(@PathVariable Long userId) {
        return budgetService.getBudgetsByUser(userId);
    }

    // =====================================================
    // ADD BUDGET
    // =====================================================
    @PostMapping("/add")
    public Budget addBudget(@RequestBody Budget budget) {
        return budgetService.addBudget(budget);
    }

    // =====================================================
    // UPDATE BUDGET (Edit Monthly Limit)
    // =====================================================
    @PutMapping("/{id}")
    public Budget updateBudget(
            @PathVariable Long id,
            @RequestBody Map<String, Double> body
    ) {
        Double newLimit = body.get("monthlyLimit");
        return budgetService.updateBudget(id, newLimit);
    }

    // =====================================================
    // DELETE BUDGET
    // =====================================================
    @DeleteMapping("/{id}")
    public void deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
    }
}


