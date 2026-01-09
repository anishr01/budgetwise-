package com.budgetwise.backend.controller;

import com.budgetwise.backend.dto.CategoryExpenseDTO;
import com.budgetwise.backend.dto.ExpenseRequestDTO;
import com.budgetwise.backend.dto.ExpenseResponseDTO;
import com.budgetwise.backend.dto.MonthlySummaryDTO;
import com.budgetwise.backend.dto.MonthlyTrendDTO;
import com.budgetwise.backend.model.Expense;
import com.budgetwise.backend.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // =================================================
    // ADD EXPENSE
    // =================================================
    @PostMapping("/add/{userId}")
    public ResponseEntity<?> addExpense(
            @PathVariable Long userId,
            @RequestBody ExpenseRequestDTO dto
    ) {

        if (dto.getType() == null || dto.getType().isBlank()) {
            return ResponseEntity.badRequest().body("type is required");
        }
        if (dto.getCategory() == null || dto.getCategory().isBlank()) {
            return ResponseEntity.badRequest().body("category is required");
        }
        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            return ResponseEntity.badRequest().body("amount must be greater than 0");
        }
        if (dto.getExpenseDate() == null) {
            return ResponseEntity.badRequest().body("expenseDate is required");
        }
        if (dto.getMonthYear() == null || dto.getMonthYear().isBlank()) {
            return ResponseEntity.badRequest().body("monthYear is required");
        }

        Expense saved = expenseService.addExpense(userId, dto);

        ExpenseResponseDTO response = new ExpenseResponseDTO(
                saved.getId(),
                saved.getType(),
                saved.getCategory(),
                saved.getAmount(),
                saved.getExpenseDate(),
                saved.getMonthYear(),
                saved.getNote()
        );

        return ResponseEntity.ok(response);
    }

    // =================================================
    // GET EXPENSES (MONTH + OPTIONAL CATEGORY)
    // =================================================
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByUser(
            @PathVariable Long userId,
            @RequestParam String monthYear,
            @RequestParam(required = false) String category
    ) {

        List<Expense> expenses =
                expenseService.getExpensesByUserId(userId, category, monthYear);

        List<ExpenseResponseDTO> response = expenses.stream()
                .map(e -> new ExpenseResponseDTO(
                        e.getId(),
                        e.getType(),
                        e.getCategory(),
                        e.getAmount(),
                        e.getExpenseDate(),
                        e.getMonthYear(),
                        e.getNote()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // =================================================
    // MONTHLY SUMMARY (INCOME vs EXPENSE – BAR CHART)
    // =================================================
    @GetMapping("/summary/{userId}")
    public ResponseEntity<MonthlySummaryDTO> getMonthlySummary(
            @PathVariable Long userId,
            @RequestParam String monthYear
    ) {
        return ResponseEntity.ok(
                expenseService.getMonthlySummary(userId, monthYear)
        );
    }

    // =================================================
    // CATEGORY-WISE EXPENSE (PIE CHART)
    // =================================================
    @GetMapping("/category-wise/{userId}")
    public ResponseEntity<List<CategoryExpenseDTO>> getCategoryWiseExpense(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                expenseService.getCategoryWiseExpense(userId)
        );
    }

    // =================================================
    // MONTHLY SPENDING TREND (LINE CHART)
    // =================================================
    @GetMapping("/monthly-trend/{userId}")
    public ResponseEntity<List<MonthlyTrendDTO>> getMonthlyTrend(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                expenseService.getMonthlyExpenseTrend(userId)
        );
    }

    // =================================================
    // DELETE EXPENSE
    // =================================================
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok().build();
    }
}
