package com.example.doda.controller;

import com.example.doda.entity.ApiResponse;
import com.example.doda.entity.DrugPrediction;
import com.example.doda.entity.DrugSensitivityItem;
import com.example.doda.entity.PagedResult;
import com.example.doda.service.DrugPredictionService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class DrugPredictionController {
    private final DrugPredictionService drugPredictionService;

    public DrugPredictionController(DrugPredictionService drugPredictionService) {
        this.drugPredictionService = drugPredictionService;
    }

    @GetMapping("/api/drug-predictions")
    public List<DrugPrediction> getDrugPredictions(
            @RequestParam String cancerType,
            @RequestParam(required = false, defaultValue = "") String drugName) {
        return drugPredictionService.getPredictions(cancerType, drugName);
    }

    @GetMapping("/api/v1/drug-sensitivity")
    public ApiResponse<PagedResult<DrugSensitivityItem>> getDrugSensitivity(
            @RequestParam String cancerType,
            @RequestParam(required = false, defaultValue = "") String drugName,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10", name = "page_size") int pageSize) {
        int safePage = Math.max(page, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 100);
        PagedResult<DrugSensitivityItem> data = drugPredictionService.getDrugSensitivity(
                cancerType,
                drugName,
                safePage,
                safePageSize
        );
        return ApiResponse.ok(data);
    }
}
