package com.budgetwise.backend.controller;

import com.budgetwise.backend.service.AiInsightService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiController {

    private final AiInsightService aiInsightService;

    public AiController(AiInsightService aiInsightService) {
        this.aiInsightService = aiInsightService;
    }

    @GetMapping("/insights/{userId}")
    public String getAiInsights(
            @PathVariable Long userId,
            @RequestParam String monthYear
    ) {
        return aiInsightService.generateInsights(userId, monthYear);
    }
}
