package com.example.doda.service;

import com.example.doda.entity.DrugPrediction;
import com.example.doda.entity.DrugSensitivityItem;
import com.example.doda.entity.PagedResult;
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
        return drugPredictionMapper.findDrug(cancerType, normalizeKeyword(drugName));
    }

    public PagedResult<DrugSensitivityItem> getDrugSensitivity(String cancerType, String drugName, int page, int pageSize) {
        String keyword = normalizeKeyword(drugName);
        int offset = (page - 1) * pageSize;

        List<DrugSensitivityItem> items = drugPredictionMapper.findDrugPage(cancerType, keyword, offset, pageSize)
                .stream()
                .map(item -> new DrugSensitivityItem(
                        item.getSampleId(),
                        item.getDrugName(),
                        item.getSensitivityScore(),
                        item.getCancerType(),
                        "DODA"
                ))
                .toList();

        long total = drugPredictionMapper.countDrug(cancerType, keyword);
        return new PagedResult<>(items, total, page, pageSize);
    }

    private String normalizeKeyword(String drugName) {
        return drugName == null ? "" : drugName.trim();
    }
}
