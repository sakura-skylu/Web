package com.example.doda.analysis;

import com.example.doda.analysis.dto.AnalysisFileInfo;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AnalysisTask {
    private final String taskId;
    private volatile TaskStatus status;
    private volatile String message;
    private final Instant createdAt;
    private final Map<String, AnalysisFileInfo> resultFiles = new ConcurrentHashMap<>();

    public AnalysisTask(String taskId) {
        this.taskId = taskId;
        this.status = TaskStatus.PENDING;
        this.message = "";
        this.createdAt = Instant.now();
    }

    public String getTaskId() {
        return taskId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Map<String, AnalysisFileInfo> getResultFiles() {
        return resultFiles;
    }

    public void setResultFile(String analysisTypeId, AnalysisFileInfo fileInfo) {
        if (analysisTypeId == null || analysisTypeId.isBlank()) return;
        if (fileInfo == null) return;
        resultFiles.put(analysisTypeId, fileInfo);
    }
}

