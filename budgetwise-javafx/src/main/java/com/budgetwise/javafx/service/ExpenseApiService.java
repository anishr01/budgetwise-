package com.budgetwise.javafx.service;

import com.budgetwise.javafx.dto.CategoryExpenseDTO;
import com.budgetwise.javafx.dto.MonthlySummaryDTO;
import com.budgetwise.javafx.dto.MonthlyTrendDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ExpenseApiService {

    private static final String BASE_URL =
            "http://localhost:8080/api/expenses";

    private final ObjectMapper mapper = new ObjectMapper();

    // ================= PIE CHART =================
    public List<CategoryExpenseDTO> getCategoryExpenseSummary(Long userId) {

        try {
            URL url = new URL(BASE_URL + "/category-wise/" + userId);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(con.getInputStream()));

            return mapper.readValue(
                    reader,
                    new TypeReference<List<CategoryExpenseDTO>>() {}
            );

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // ================= BAR CHART =================
    public MonthlySummaryDTO getMonthlySummary(
            Long userId,
            String monthYear
    ) {

        try {
            URL url = new URL(
                    BASE_URL + "/summary/" + userId + "?monthYear=" + monthYear
            );

            HttpURLConnection con =
                    (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(con.getInputStream()));

            return mapper.readValue(reader, MonthlySummaryDTO.class);

        } catch (Exception e) {
            e.printStackTrace();
            return new MonthlySummaryDTO(0.0, 0.0);
        }
    }

    // ================= LINE CHART =================
    public List<MonthlyTrendDTO> getMonthlyTrend(Long userId) {

        try {
            URL url = new URL(BASE_URL + "/monthly-trend/" + userId);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(con.getInputStream()));

            return mapper.readValue(
                    reader,
                    new TypeReference<List<MonthlyTrendDTO>>() {}
            );

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}

