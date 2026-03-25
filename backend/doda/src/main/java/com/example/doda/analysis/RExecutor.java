package com.example.doda.analysis;

import org.springframework.util.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RExecutor {

    private final String execMode; // auto | direct | conda
    private final String condaPath; // optional
    private final String condaEnv; // optional
    private final String rscriptCmd; // e.g. Rscript.exe
    private final String wslDistro; // optional
    private final boolean wslUseCondaRun;

    public RExecutor(String execMode,
                      String condaPath,
                      String condaEnv,
                      String rscriptCmd,
                      String wslDistro,
                      boolean wslUseCondaRun) {
        this.execMode = execMode;
        this.condaPath = condaPath;
        this.condaEnv = condaEnv;
        this.rscriptCmd = rscriptCmd;
        this.wslDistro = wslDistro;
        this.wslUseCondaRun = wslUseCondaRun;
    }

    public int execute(List<String> command, File logFile, long timeoutMs) throws Exception {
        if (logFile.getParentFile() != null) logFile.getParentFile().mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, StandardCharsets.UTF_8, false))) {
            bw.write("COMMAND: " + String.join(" ", command));
            bw.newLine();
        }

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));
        Process p = pb.start();

        if (timeoutMs > 0) {
            boolean finished = p.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
            if (!finished) {
                p.destroyForcibly();
                appendTimeoutLog(logFile, timeoutMs);
                return -1;
            }
            return p.exitValue();
        }

        return p.waitFor();
    }

    private void appendTimeoutLog(File logFile, long timeoutMs) {
        try (BufferedWriter bw = Files.newBufferedWriter(
            logFile.toPath(),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        )) {
            bw.write("ERROR: timeout exceeded (ms=" + timeoutMs + ")");
            bw.newLine();
        } catch (IOException ignored) {
            // no-op
        }
    }

    public void executeRscript(String scriptPath,
                                List<String> scriptArgs,
                                File logFile,
                                long timeoutMs) throws Exception {
        if (!new File(scriptPath).exists()) {
            throw new IllegalArgumentException("R script not found: " + scriptPath);
        }

        List<String> cmd = new ArrayList<>();

        String mode = execMode == null ? "auto" : execMode.trim().toLowerCase();
        if ("wsl".equals(mode) || "wsl2".equals(mode)) {
            String wslScript = toWslPath(scriptPath);
            List<String> wslArgs = new ArrayList<>();
            for (String a : scriptArgs) {
                wslArgs.add(toWslArg(a));
            }

            String wslScriptAndArgs = escapeSingleQuotes(wslScript);
            for (String a : wslArgs) {
                wslScriptAndArgs += " " + escapeSingleQuotes(a);
            }

            String runCmd;
            if (wslUseCondaRun && condaEnv != null && !condaEnv.isBlank()) {
                runCmd = "conda run -n " + escapeSingleQuotes(condaEnv) + " " + rscriptCmd + " " + wslScriptAndArgs;
            } else {
                runCmd = rscriptCmd + " " + wslScriptAndArgs;
            }

            // 使用 bash -lc 让 shell 加载初始化环境（有些环境 conda 需要）
            // 不显式指定 cd：脚本里会按传入路径访问；base_dir 也会在参数里传入
            List<String> full = new ArrayList<>();
            full.add("wsl");
            if (wslDistro != null && !wslDistro.isBlank()) {
                full.add("-d");
                full.add(wslDistro);
            }
            full.add("bash");
            full.add("-lc");
            full.add(runCmd);

            int exit = execute(full, logFile, timeoutMs);
            if (exit != 0) throw new RuntimeException("WSL Rscript failed, exitCode=" + exit);
            return;
        }

        if ("direct".equals(mode)) {
            cmd.add(rscriptCmd);
            cmd.add(scriptPath);
            cmd.addAll(scriptArgs);
            int exit = execute(cmd, logFile, timeoutMs);
            if (exit != 0) throw new RuntimeException("Rscript failed, exitCode=" + exit);
            return;
        }

        // default: auto/conda
        if (("auto".equals(mode) || "conda".equals(mode)) && StringUtils.hasText(condaPath) && StringUtils.hasText(condaEnv)) {
            // conda run -n <env> <rscriptCmd> <scriptPath> ...
            cmd.addAll(Arrays.asList(condaPath, "run", "-n", condaEnv, rscriptCmd, scriptPath));
            cmd.addAll(scriptArgs);
            try {
                int exit = execute(cmd, logFile, timeoutMs);
                if (exit != 0) throw new RuntimeException("conda run failed, exitCode=" + exit);
                return;
            } catch (Exception e) {
                // fallback to direct mode
                if ("conda".equals(mode)) throw e;
            }
        }

        // fallback direct
        cmd.clear();
        cmd.add(rscriptCmd);
        cmd.add(scriptPath);
        cmd.addAll(scriptArgs);
        int exit = execute(cmd, logFile, timeoutMs);
        if (exit != 0) throw new RuntimeException("Rscript failed, exitCode=" + exit);
    }

    // Convert "E:\a\b" -> "/mnt/e/a/b" (best-effort)
    private static String toWslPath(String windowsPath) {
        if (windowsPath == null) return "";
        String p = windowsPath.replace('\\', '/');
        if (p.length() >= 2 && p.charAt(1) == ':') {
            String drive = String.valueOf(Character.toLowerCase(p.charAt(0)));
            String rest = p.substring(2);
            if (rest.startsWith("/")) rest = rest.substring(1);
            return "/mnt/" + drive + "/" + rest;
        }
        // already looks like unix path
        return p;
    }

    // Keep non-path args as-is; for simplicity we also convert backslashes to slashes
    private static String toWslArg(String arg) {
        return toWslPath(arg);
    }

    private static String escapeSingleQuotes(String s) {
        if (s == null) return "''";
        // escape for single-quoted shell string: ' -> '"'"'
        return "'" + s.replace("'", "'\"'\"'") + "'";
    }
}
