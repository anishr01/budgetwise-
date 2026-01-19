package com.budgetwise.backend.controller;

import com.budgetwise.backend.model.SavingGoal;
import com.budgetwise.backend.service.SavingGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/saving-goals")
@CrossOrigin
public class SavingGoalController {

    @Autowired
    private SavingGoalService savingGoalService;

    // ADD GOAL
    @PostMapping("/add")
    public SavingGoal addGoal(@RequestBody SavingGoal goal) {
        return savingGoalService.addGoal(goal);
    }

    // GET GOALS
    @GetMapping("/user/{userId}")
    public List<SavingGoal> getGoals(@PathVariable Long userId) {
        return savingGoalService.getGoalsByUser(userId);
    }

    // ADD SAVED AMOUNT
    @PutMapping("/{id}/save")
    public SavingGoal addSavings(
            @PathVariable Long id,
            @RequestBody Map<String, Double> body
    ) {
        return savingGoalService.updateSavedAmount(id, body.get("amount"));
    }

    // DELETE GOAL
    @DeleteMapping("/{id}")
    public void deleteGoal(@PathVariable Long id) {
        savingGoalService.deleteGoal(id);
    }
}
