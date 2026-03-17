package com.example.doda.service;

import com.example.doda.entity.DrugPrediction;
import com.example.doda.mapper.DrugPredictionMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrugPredictionService {
    private final DrugPredictionMapper drugPredictionMapper;

    public DrugPredictionService(DrugPredictionMapper drugPredictionMapper) {
        this.drugPredictionMapper = drugPredictionMapper;
    }

    public List<DrugPrediction> getPredictions(String cancerType, String drugName) {
        return drugPredictionMapper.findDrug(cancerType, drugName == null ? "" : drugName);
    }
}