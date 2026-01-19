package com.budgetwise.backend.service;

public interface ReportService {
    byte[] generatePdfReport(Long userId, String monthYear);
    byte[] generateCsvReport(Long userId, String monthYear);
}
