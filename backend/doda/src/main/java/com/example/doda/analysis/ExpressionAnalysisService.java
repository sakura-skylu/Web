package com.example.doda.analysis;

import com.example.doda.analysis.dto.AnalysisFileInfo;
import com.example.doda.analysis.dto.AnalysisTaskInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import jakarta.annotation.PreDestroy;

@Service
public class ExpressionAnalysisService {

    private final TaskRegistry registry;
    private final ThreadPoolExecutor executorService;

    private final Path engineRootPath;
    private final Path workRootPath;

    private final String execMode;
    private final String condaPath;
    private final String condaEnv;
    private final String rscriptCmd;
    private final String wslDistro;
    private final boolean wslUseCondaRun;
    private final long scriptTimeoutMs;

    public ExpressionAnalysisService(TaskRegistry registry,
                                      @Value("${doda.engine.baseDir:engine/doda}") String engineBaseDir,
                                      @Value("${doda.work.rootDir:uploads}") String workRootDir,
                                      @Value("${doda.exec.mode:auto}") String execMode,
                                      @Value("${doda.exec.condaPath:conda}") String condaPath,
                                      @Value("${doda.exec.condaEnv:r_doda}") String condaEnv,
                                      @Value("${doda.exec.rscriptCmd:Rscript}") String rscriptCmd,
                                      @Value("${doda.exec.wslDistro:}") String wslDistro,
                                      @Value("${doda.exec.wslUseCondaRun:true}") boolean wslUseCondaRun,
                                      @Value("${doda.task.pool-size:2}") int taskPoolSize,
                                      @Value("${doda.task.queue-capacity:20}") int taskQueueCapacity,
                                      @Value("${doda.task.script-timeout-ms:1800000}") long scriptTimeoutMs) {
        this.registry = registry;
        this.execMode = execMode;
        this.condaPath = condaPath;
        this.condaEnv = condaEnv;
        this.rscriptCmd = rscriptCmd;
        this.wslDistro = wslDistro;
        this.wslUseCondaRun = wslUseCondaRun;
        this.scriptTimeoutMs = Math.max(0, scriptTimeoutMs);

        int poolSize = Math.max(1, taskPoolSize);
        int queueCap = Math.max(1, taskQueueCapacity);
        this.executorService = new ThreadPoolExecutor(
            poolSize,
            poolSize,
            0L,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(queueCap),
            new ThreadPoolExecutor.AbortPolicy()
        );

        this.engineRootPath = resolveEngineRoot(engineBaseDir);
        this.workRootPath = resolveWorkRoot(workRootDir);
        try {
            Files.createDirectories(this.workRootPath);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create work root directory: " + this.workRootPath, e);
        }
    }

    @PreDestroy
    public void shutdownExecutor() {
        executorService.shutdown();
    }

    public AnalysisTaskInfoResponse submitAndRunAll(MultipartFile matrixCsv,
                                                       MultipartFile groupCsv,
                                                       GroupStrategy groupStrategy,
                                                       Double logfcCutoff,
                                                       Double padjCutoff) throws Exception {
        if (matrixCsv == null || matrixCsv.isEmpty()) throw new IllegalArgumentException("matrixCsv is required");
        if (groupStrategy == null) groupStrategy = GroupStrategy.MEDIAN;
        if (logfcCutoff == null) logfcCutoff = 1.0;
        if (padjCutoff == null) padjCutoff = 0.05;

        final double finalLogfcCutoff = logfcCutoff;
        final double finalPadjCutoff = padjCutoff;

        String taskId = UUID.randomUUID().toString().replace("-", "");
        AnalysisTask task = registry.create(taskId);
        task.setStatus(TaskStatus.PENDING);

        Path taskDir = workRootPath.resolve(taskId).normalize();
        Path inputDir = taskDir.resolve("input");
        Path resultsDir = taskDir.resolve("results");
        Files.createDirectories(inputDir);
        Files.createDirectories(resultsDir);

        File matrixFile = inputDir.resolve("matrix.csv").toFile();
        // Defensive: ensure parent directory exists before transferTo()
        Files.createDirectories(matrixFile.toPath().getParent());
        matrixCsv.transferTo(matrixFile);

        File groupFile;
        if (groupCsv != null && !groupCsv.isEmpty()) {
            groupFile = inputDir.resolve("group.csv").toFile();
            Files.createDirectories(groupFile.toPath().getParent());
            groupCsv.transferTo(groupFile);
        } else {
            groupFile = inputDir.resolve("group.csv").toFile();
            Files.createDirectories(groupFile.toPath().getParent());
            new GroupCsvGenerator().generateGroupCsv(matrixFile, groupStrategy, groupFile);
        }

        // run async
        try {
            executorService.submit(() -> {
                try {
                    task.setStatus(TaskStatus.RUNNING);
                    task.setMessage("running...");
                    RunSummary summary = runAll(task, matrixFile, groupFile, resultsDir, finalLogfcCutoff, finalPadjCutoff);
                    if (summary.successCount > 0) {
                        task.setStatus(TaskStatus.SUCCESS);
                    } else {
                        task.setStatus(TaskStatus.FAILED);
                    }
                    task.setMessage(summary.buildMessage());
                } catch (Exception e) {
                    task.setStatus(TaskStatus.FAILED);
                    task.setMessage("Task failed: " + safeMessage(e));
                }
            });
        } catch (RejectedExecutionException e) {
            task.setStatus(TaskStatus.FAILED);
            task.setMessage("Server is busy. Please retry later.");
            throw new IllegalStateException("Server is busy. Please retry later.");
        }

        Map<String, AnalysisFileInfo> empty = Collections.emptyMap();
        return new AnalysisTaskInfoResponse(taskId, TaskStatus.PENDING, "", task.getCreatedAt(), empty);
    }

    private RunSummary runAll(AnalysisTask task,
                         File matrixFile,
                         File groupFile,
                         Path resultsDir,
                         double logfcCutoff,
                         double padjCutoff) throws Exception {

        RunSummary summary = new RunSummary();

        RExecutor executor = new RExecutor(execMode, condaPath, condaEnv, rscriptCmd, wslDistro, wslUseCondaRun);
        File engineRoot = engineRootPath.toFile();

        for (AnalysisType type : AnalysisType.values()) {
            Path outDir = resultsDir.resolve(type.id);
            Files.createDirectories(outDir);

            File logFile = outDir.resolve("analysis.log").toFile();

            try {
                List<String> scriptArgs = buildArgs(type, matrixFile, groupFile, outDir.toFile(), engineRoot, logfcCutoff, padjCutoff);
                File scriptPath = new File(engineRoot, "Rscripts/" + getScriptFileName(type));

                executor.executeRscript(scriptPath.getAbsolutePath(), scriptArgs, logFile, scriptTimeoutMs);

                AnalysisFileInfo result = detectResultFiles(type, outDir);
                task.setResultFile(type.id, result);
                if (result.getImageFile() != null || result.getCsvFile() != null) {
                    summary.successCount++;
                } else {
                    summary.failedByType.put(type.id, "completed but no result files found");
                }
            } catch (Exception e) {
                task.setResultFile(type.id, new AnalysisFileInfo(null, null));
                summary.failedByType.put(type.id, safeMessage(e));
            }
        }
        return summary;
    }

    private List<String> buildArgs(AnalysisType type,
                                   File matrixFile,
                                   File groupFile,
                                   File outDir,
                                   File engineRoot,
                                   double logfcCutoff,
                                   double padjCutoff) {
        // Return args to Rscript after <scriptPath>
        switch (type) {
            case DEG:
                // diff_analysis.R: matrix output group pCutoff fcCutoff
                return List.of(
                    matrixFile.getAbsolutePath(),
                    outDir.getAbsolutePath(),
                    groupFile.getAbsolutePath(),
                    String.valueOf(padjCutoff),
                    String.valueOf(logfcCutoff)
                );
            case ESTIMATE:
                // estimate_analysis.R: matrix output group
                return List.of(
                    matrixFile.getAbsolutePath(),
                    outDir.getAbsolutePath(),
                    groupFile.getAbsolutePath()
                );
            case GSVA:
                // GSVA.R: matrix output group baseDir gmtFile(optional)
                return List.of(
                    matrixFile.getAbsolutePath(),
                    outDir.getAbsolutePath(),
                    groupFile.getAbsolutePath(),
                    engineRoot.getAbsolutePath(),
                    "" // let R auto-pick a GMT
                );
            case IMMUNE_CHECKPOINT:
                // immune_checkpoint_de.R: matrix output group baseDir
                return List.of(
                    matrixFile.getAbsolutePath(),
                    outDir.getAbsolutePath(),
                    groupFile.getAbsolutePath(),
                    engineRoot.getAbsolutePath()
                );
            case CIBERSORT:
                // cibersort_analysis.R: matrix output group baseDir
                return List.of(
                    matrixFile.getAbsolutePath(),
                    outDir.getAbsolutePath(),
                    groupFile.getAbsolutePath(),
                    engineRoot.getAbsolutePath()
                );
            default:
                throw new IllegalArgumentException("Unsupported type: " + type.id);
        }
    }

    private String getScriptFileName(AnalysisType type) {
        switch (type) {
            case DEG:
                return "diff_analysis.R";
            case ESTIMATE:
                return "estimate_analysis.R";
            case GSVA:
                return "GSVA.R";
            case IMMUNE_CHECKPOINT:
                return "immune_checkpoint_de.R";
            case CIBERSORT:
                return "cibersort_analysis.R";
            default:
                throw new IllegalArgumentException("Unsupported type: " + type.id);
        }
    }

    public AnalysisTaskInfoResponse getTaskInfo(String taskId) {
        AnalysisTask task = registry.get(taskId).orElse(null);
        if (task == null) return null;

        Map<String, AnalysisFileInfo> results = new HashMap<>(task.getResultFiles());

        return new AnalysisTaskInfoResponse(taskId, task.getStatus(), task.getMessage(), task.getCreatedAt(), results);
    }

    public File getTaskFile(String taskId, String analysisTypeId, String filename) {
        if (filename == null || filename.isBlank()) return null;
        AnalysisTask task = registry.get(taskId).orElse(null);
        if (task == null) return null;
        AnalysisType type;
        try {
            type = AnalysisType.fromId(analysisTypeId);
        } catch (IllegalArgumentException e) {
            return null;
        }

        Path taskDir = workRootPath.resolve(taskId).normalize();
        Path resultDir = taskDir.resolve("results").resolve(type.id).normalize();
        Path target = resultDir.resolve(filename).normalize();
        if (!target.startsWith(resultDir)) return null;
        File file = target.toFile();
        return file.exists() && file.isFile() ? file : null;
    }

    private AnalysisFileInfo detectResultFiles(AnalysisType type, Path outDir) {
        String imageFile = resolveResultFileName(outDir, imageCandidates(type), List.of(".png", ".jpg", ".jpeg", ".webp", ".svg"));
        String csvFile = resolveResultFileName(outDir, csvCandidates(type), List.of(".csv", ".tsv"));
        return new AnalysisFileInfo(imageFile, csvFile);
    }

    private String resolveResultFileName(Path dir, List<String> preferredNames, List<String> allowedExtensions) {
        for (String name : preferredNames) {
            if (name == null || name.isBlank()) continue;
            Path candidate = dir.resolve(name);
            if (Files.isRegularFile(candidate)) return name;
        }

        try (Stream<Path> stream = Files.list(dir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(fileName -> hasExtension(fileName, allowedExtensions))
                    .sorted()
                    .findFirst()
                    .orElse(null);
        } catch (IOException ignored) {
            return null;
        }
    }

    private boolean hasExtension(String fileName, List<String> extensions) {
        String lower = fileName.toLowerCase();
        for (String ext : extensions) {
            if (lower.endsWith(ext)) return true;
        }
        return false;
    }

    private List<String> imageCandidates(AnalysisType type) {
        List<String> candidates = new ArrayList<>();
        candidates.add(type.defaultImageFile);
        switch (type) {
            case GSVA:
                candidates.add("gsva_heatmap.png");
                break;
            case CIBERSORT:
                candidates.add("cibersort_heatmap.png");
                break;
            default:
                break;
        }
        return candidates;
    }

    private List<String> csvCandidates(AnalysisType type) {
        List<String> candidates = new ArrayList<>();
        candidates.add(type.defaultCsvFile);
        switch (type) {
            case CIBERSORT:
                candidates.add("cibersort_result_full.csv");
                break;
            case GSVA:
                candidates.add("gsva_diff_sig.csv");
                candidates.add("gsva_diff_all.csv");
                break;
            default:
                break;
        }
        return candidates;
    }

    private Path resolveEngineRoot(String configuredPath) {
        List<Path> candidates = new ArrayList<>();
        Path userDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();

        if (configuredPath != null && !configuredPath.isBlank()) {
            Path configured = Paths.get(configuredPath);
            candidates.add(configured);
            if (!configured.isAbsolute()) {
                candidates.add(userDir.resolve(configured));
            }
        }

        candidates.add(userDir.resolve("engine").resolve("doda"));
        candidates.add(userDir.resolve("backend").resolve("doda").resolve("engine").resolve("doda"));
        candidates.add(userDir.resolve("..").resolve("engine").resolve("doda").normalize());

        for (Path candidate : candidates) {
            Path normalized = candidate.toAbsolutePath().normalize();
            if (Files.isDirectory(normalized)) return normalized;
        }

        if (!candidates.isEmpty()) {
            return candidates.get(0).toAbsolutePath().normalize();
        }
        return userDir.resolve("engine").resolve("doda");
    }

    private Path resolveWorkRoot(String configuredPath) {
        String finalPath = (configuredPath == null || configuredPath.isBlank()) ? "uploads" : configuredPath;
        Path configured = Paths.get(finalPath);
        if (configured.isAbsolute()) return configured.normalize();
        return Paths.get(System.getProperty("user.dir")).resolve(configured).toAbsolutePath().normalize();
    }

    private String safeMessage(Throwable throwable) {
        if (throwable == null) return "unknown error";
        String msg = throwable.getMessage();
        if (msg != null && !msg.isBlank()) return msg;
        Throwable cause = throwable.getCause();
        if (cause != null && cause != throwable) return safeMessage(cause);
        return throwable.getClass().getSimpleName();
    }

    private static final class RunSummary {
        private int successCount = 0;
        private final Map<String, String> failedByType = new LinkedHashMap<>();

        private String buildMessage() {
            if (successCount == 0 && failedByType.isEmpty()) {
                return "all analyses failed";
            }
            if (failedByType.isEmpty()) {
                return "success";
            }
            String failedPart = String.join("; ", failedByType.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .toList());
            if (successCount == 0) {
                return "all analyses failed: " + failedPart;
            }
            return "partial success, failed analyses: " + failedPart;
        }
    }
}
