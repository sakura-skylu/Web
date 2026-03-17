package com.example.doda.controller;

import com.example.doda.entity.DrugPrediction;
import com.example.doda.service.DrugPredictionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DrugPredictionController {
    private final DrugPredictionService drugPredictionService;

    public DrugPredictionController(DrugPredictionService drugPredictionService) {
        this.drugPredictionService = drugPredictionService;
    }

    @GetMapping("/api/drug-predictions")
    public List<DrugPrediction> getDrugPredictions(
            @RequestParam String cancerType,
            @RequestParam(required = false, defaultValue = "") String drugName) {
        System.out.println("Received cancerType: " + cancerType + ", drugName: " + drugName);
        return drugPredictionService.getPredictions(cancerType, drugName);
    }
}