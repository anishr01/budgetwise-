package com.budgetwise.backend.service;

import com.budgetwise.backend.model.SavingGoal;
import com.budgetwise.backend.repository.SavingGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavingGoalService {

    @Autowired
    private SavingGoalRepository savingGoalRepository;

    public SavingGoal addGoal(SavingGoal goal) {
        goal.setSavedAmount(0); // start fresh
        return savingGoalRepository.save(goal);
    }

    public List<SavingGoal> getGoalsByUser(Long userId) {
        return savingGoalRepository.findByUserId(userId);
    }

    public SavingGoal updateSavedAmount(Long id, double amount) {
        SavingGoal goal = savingGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        goal.setSavedAmount(goal.getSavedAmount() + amount);
        return savingGoalRepository.save(goal);
    }

    public void deleteGoal(Long id) {
        savingGoalRepository.deleteById(id);
    }
}


