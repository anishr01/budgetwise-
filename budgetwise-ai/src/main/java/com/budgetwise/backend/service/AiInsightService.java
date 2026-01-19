package com.budgetwise.backend.service;

public interface AiInsightService {
    String generateInsights(Long userId, String monthYear);
}
