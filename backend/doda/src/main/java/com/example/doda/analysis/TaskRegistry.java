package com.example.doda.analysis;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

@Component
public class TaskRegistry {
    private final ConcurrentMap<String, AnalysisTask> tasks = new ConcurrentHashMap<>();

    public AnalysisTask create(String taskId) {
        AnalysisTask task = new AnalysisTask(taskId);
        tasks.put(taskId, task);
        return task;
    }

    public Optional<AnalysisTask> get(String taskId) {
        return Optional.ofNullable(tasks.get(taskId));
    }
}

