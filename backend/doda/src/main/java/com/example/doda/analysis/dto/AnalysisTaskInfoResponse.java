package com.example.doda.analysis.dto;

import com.example.doda.analysis.TaskStatus;

import java.time.Instant;
import java.util.Map;

public class AnalysisTaskInfoResponse {
    private final String taskId;
    private final TaskStatus status;
    private final String message;
    private final Instant createdAt;
    private final Map<String, AnalysisFileInfo> results;

    public AnalysisTaskInfoResponse(String taskId,
                                    TaskStatus status,
                                    String message,
                                    Instant createdAt,
                                    Map<String, AnalysisFileInfo> results) {
        this.taskId = taskId;
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
        this.results = results;
    }

    public String getTaskId() {
        return taskId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Map<String, AnalysisFileInfo> getResults() {
        return results;
    }
}

