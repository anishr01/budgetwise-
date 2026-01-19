package com.budgetwise.backend.service.impl;

import com.budgetwise.backend.model.Expense;
import com.budgetwise.backend.repository.ExpenseRepository;
import com.budgetwise.backend.service.ReportService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final ExpenseRepository expenseRepository;

    public ReportServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public byte[] generateCsvReport(Long userId, String monthYear) {

        List<Expense> expenses =
                expenseRepository.findByUserIdAndMonthYear(userId, monthYear);

        StringBuilder sb = new StringBuilder();
        sb.append("Date,Type,Category,Amount,Note\n");

        for (Expense e : expenses) {
            sb.append(e.getExpenseDate()).append(",")
                    .append(e.getType()).append(",")
                    .append(e.getCategory()).append(",")
                    .append(e.getAmount()).append(",")
                    .append(e.getNote() == null ? "" : e.getNote())
                    .append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] generatePdfReport(Long userId, String monthYear) {
        // SIMPLE VERSION (PDF lib can be added later)
        String content = "BudgetWise Report\nMonth: " + monthYear +
                "\n\n(Use CSV for now – PDF enhancement optional)";

        return content.getBytes(StandardCharsets.UTF_8);
    }
}
