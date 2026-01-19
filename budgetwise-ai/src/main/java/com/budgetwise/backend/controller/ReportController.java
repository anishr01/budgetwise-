package com.budgetwise.backend.controller;

import com.budgetwise.backend.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // ===== PDF REPORT =====
    @GetMapping("/pdf/{userId}")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable Long userId,
            @RequestParam String monthYear
    ) {

        byte[] pdf = reportService.generatePdfReport(userId, monthYear);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=budget-report-" + monthYear + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // ===== CSV REPORT =====
    @GetMapping("/csv/{userId}")
    public ResponseEntity<byte[]> downloadCsv(
            @PathVariable Long userId,
            @RequestParam String monthYear
    ) {

        byte[] csv = reportService.generateCsvReport(userId, monthYear);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=budget-report-" + monthYear + ".csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csv);
    }
}
