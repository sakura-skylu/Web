package com.example.doda.analysis;

import com.example.doda.analysis.dto.AnalysisTaskInfoResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Collections;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/analysis")
public class AnalysisTaskController {

    private final ExpressionAnalysisService analysisService;

    public AnalysisTaskController(ExpressionAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping(path = "/run", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AnalysisTaskInfoResponse run(
            @RequestParam("matrixCsv") MultipartFile matrixCsv,
            @RequestParam(value = "groupCsv", required = false) MultipartFile groupCsv,
            @RequestParam(value = "groupStrategy", required = false, defaultValue = "MEDIAN") String groupStrategy,
            @RequestParam(value = "logfc", required = false, defaultValue = "1") Double logfc,
            @RequestParam(value = "padj", required = false, defaultValue = "0.05") Double padj) throws Exception {

        GroupStrategy strategy;
        if (groupStrategy == null) strategy = GroupStrategy.MEDIAN;
        else if ("quartile".equalsIgnoreCase(groupStrategy) || "QUARTILE".equalsIgnoreCase(groupStrategy)) strategy = GroupStrategy.QUARTILE;
        else strategy = GroupStrategy.MEDIAN;

        return analysisService.submitAndRunAll(matrixCsv, groupCsv, strategy, logfc, padj);
    }

    @GetMapping("/tasks/{taskId}")
    public AnalysisTaskInfoResponse taskInfo(@PathVariable("taskId") String taskId) {
        AnalysisTaskInfoResponse res = analysisService.getTaskInfo(taskId);
        if (res != null) return res;
        return new AnalysisTaskInfoResponse(taskId, TaskStatus.FAILED, "task not found", Instant.now(), Collections.emptyMap());
    }

    @GetMapping("/tasks/{taskId}/files/{analysisTypeId}/{filename}")
    public ResponseEntity<FileSystemResource> getTaskFile(
            @PathVariable("taskId") String taskId,
            @PathVariable("analysisTypeId") String analysisTypeId,
            @PathVariable("filename") String filename) {

        File file = analysisService.getTaskFile(taskId, analysisTypeId, filename);
        if (file == null || !file.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = "application/octet-stream";
        try {
            String probed = Files.probeContentType(file.toPath());
            if (probed != null && !probed.isBlank()) contentType = probed;
        } catch (Exception ignored) {
            // fallback to octet-stream
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(new FileSystemResource(file));
    }
}

