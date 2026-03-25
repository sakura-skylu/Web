package com.example.doda.analysis.dto;

public class AnalysisFileInfo {
    private final String imageFile;
    private final String csvFile;

    public AnalysisFileInfo(String imageFile, String csvFile) {
        this.imageFile = imageFile;
        this.csvFile = csvFile;
    }

    public String getImageFile() {
        return imageFile;
    }

    public String getCsvFile() {
        return csvFile;
    }
}

