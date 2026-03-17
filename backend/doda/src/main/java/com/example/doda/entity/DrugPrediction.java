package com.example.doda.entity;

public class DrugPrediction {
    private String sampleId;
    private String drugName;
    private float sensitivityScore;
    private String cancerType;

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public float getSensitivityScore() {
        return sensitivityScore;
    }

    public void setSensitivityScore(float sensitivityScore) {
        this.sensitivityScore = sensitivityScore;
    }

    public String getCancerType() {
        return cancerType;
    }

    public void setCancerType(String cancerType) {
        this.cancerType = cancerType;
    }
}
