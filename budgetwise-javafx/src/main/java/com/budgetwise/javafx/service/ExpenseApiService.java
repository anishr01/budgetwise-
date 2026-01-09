package com.budgetwise.javafx.service;

import com.budgetwise.javafx.dto.CategoryExpenseDTO;
import com.budgetwise.javafx.dto.MonthlySummaryDTO;
import com.budgetwise.javafx.dto.MonthlyTrendDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ExpenseApiService {

    private static final String BASE_URL =
            "http://localhost:8080/api/expenses";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ================= 1️⃣ CATEGORY-WISE =================
    public List<CategoryExpenseDTO> getCategoryWiseExpense(Long userId) {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/category-wise/" + userId))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(
                        response.body(),
                        new TypeReference<List<CategoryExpenseDTO>>() {}
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return List.of();
    }

    // ================= 2️⃣ INCOME vs EXPENSE =================
    public MonthlySummaryDTO getMonthlySummary(Long userId, String monthYear) {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            BASE_URL + "/summary/" + userId + "?monthYear=" + monthYear
                    ))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(
                        response.body(),
                        MonthlySummaryDTO.class
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ================= 3️⃣ MONTHLY TREND =================
    public List<MonthlyTrendDTO> getMonthlyTrend(Long userId) {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            BASE_URL + "/monthly-trend/" + userId
                    ))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(
                        response.body(),
                        new TypeReference<List<MonthlyTrendDTO>>() {}
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return List.of();
    }
}
