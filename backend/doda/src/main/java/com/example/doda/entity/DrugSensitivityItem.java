package com.example.doda.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DrugSensitivityItem {
    @JsonProperty("sample_id")
    private String sampleId;

    @JsonProperty("drug_name")
    private String drugName;

    @JsonProperty("sensitivity_score")
    private float sensitivityScore;

    @JsonProperty("cancer_type")
    private String cancerType;

    @JsonProperty("data_source")
    private String dataSource;

    public DrugSensitivityItem(String sampleId, String drugName, float sensitivityScore, String cancerType, String dataSource) {
        this.sampleId = sampleId;
        this.drugName = drugName;
        this.sensitivityScore = sensitivityScore;
        this.cancerType = cancerType;
        this.dataSource = dataSource;
    }

    public String getSampleId() {
        return sampleId;
    }

    public String getDrugName() {
        return drugName;
    }

    public float getSensitivityScore() {
        return sensitivityScore;
    }

    public String getCancerType() {
        return cancerType;
    }

    public String getDataSource() {
        return dataSource;
    }
}
