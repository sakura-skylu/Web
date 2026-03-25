package com.example.doda.analysis.dto;

public class AnalysisRunResponse {
    private final String taskId;

    public AnalysisRunResponse(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }
}

