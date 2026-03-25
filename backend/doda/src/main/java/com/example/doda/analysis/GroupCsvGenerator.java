package com.example.doda.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupCsvGenerator {

    public File generateGroupCsv(File matrixCsv, GroupStrategy strategy, File groupOutCsv) throws Exception {
        if (matrixCsv == null || !matrixCsv.exists()) throw new IllegalArgumentException("matrixCsv not found");
        if (strategy == null) throw new IllegalArgumentException("group strategy is required");
        if (groupOutCsv == null) throw new IllegalArgumentException("groupOutCsv is required");

        // Stream-parse matrix to compute per-sample mean score
        String header;
        try (BufferedReader br = Files.newBufferedReader(matrixCsv.toPath(), StandardCharsets.UTF_8)) {
            header = br.readLine();
        }
        if (header == null || header.trim().isEmpty()) throw new IllegalArgumentException("Empty matrix CSV");

        char delimiter = detectDelimiter(header);
        String[] headerCells = split(header, delimiter);
        if (headerCells.length < 2) throw new IllegalArgumentException("Matrix CSV must have at least 2 columns");

        List<String> sampleIds = new ArrayList<>();
        for (int i = 1; i < headerCells.length; i++) {
            sampleIds.add(cleanCell(headerCells[i]));
        }
        int nSamples = sampleIds.size();
        double[] sum = new double[nSamples];
        int[] count = new int[nSamples];

        try (BufferedReader br = Files.newBufferedReader(matrixCsv.toPath(), StandardCharsets.UTF_8)) {
            // skip header
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] cells = split(line, delimiter);
                if (cells.length < 2) continue;

                // cells[0] = gene
                int max = Math.min(nSamples, cells.length - 1);
                for (int j = 0; j < max; j++) {
                    String raw = cleanCell(cells[j + 1]);
                    if (raw == null || raw.isEmpty()) continue;
                    if (raw.equalsIgnoreCase("NA") || raw.equalsIgnoreCase("NaN")) continue;
                    try {
                        double v = Double.parseDouble(raw);
                        sum[j] += v;
                        count[j] += 1;
                    } catch (NumberFormatException ignored) {
                        // skip non-numeric value
                    }
                }
            }
        }

        double[] scores = new double[nSamples];
        for (int i = 0; i < nSamples; i++) {
            scores[i] = count[i] > 0 ? (sum[i] / count[i]) : 0.0;
        }

        boolean[] isTumor = assignTumor(scores, strategy);

        // ensure both groups have at least 2 samples; otherwise fallback to median
        int tumorCount = 0;
        int normalCount = 0;
        for (boolean b : isTumor) {
            if (b) tumorCount++; else normalCount++;
        }
        if (tumorCount < 2 || normalCount < 2) {
            boolean[] fallback = assignTumor(scores, GroupStrategy.MEDIAN);
            tumorCount = 0;
            normalCount = 0;
            for (boolean b : fallback) {
                if (b) tumorCount++; else normalCount++;
            }
            if (tumorCount < 2 || normalCount < 2) {
                throw new IllegalArgumentException("Grouping failed: cannot create Normal/Tumor with enough samples");
            }
            isTumor = fallback;
        }

        // Write group CSV expected by R:
        // col1: sample id, col2: group label (Normal/Tumor)
        if (groupOutCsv.getParentFile() != null) {
            Files.createDirectories(groupOutCsv.getParentFile().toPath());
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(groupOutCsv, StandardCharsets.UTF_8))) {
            bw.write("sample_id,group");
            bw.newLine();
            for (int i = 0; i < nSamples; i++) {
                String label = isTumor[i] ? "Tumor" : "Normal";
                bw.write(sampleIds.get(i));
                bw.write(",");
                bw.write(label);
                bw.newLine();
            }
        }

        return groupOutCsv;
    }

    private static char detectDelimiter(String headerLine) {
        // If header contains a tab more than commas, treat as TSV.
        int tabs = countChar(headerLine, '\t');
        int commas = countChar(headerLine, ',');
        return tabs > commas ? '\t' : ',';
    }

    private static int countChar(String s, char c) {
        int cnt = 0;
        for (int i = 0; i < s.length(); i++) if (s.charAt(i) == c) cnt++;
        return cnt;
    }

    private static String cleanCell(String cell) {
        if (cell == null) return "";
        String s = cell.trim();
        if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length() - 1);
        }
        // strip BOM from first cell
        if (!s.isEmpty() && s.charAt(0) == '\uFEFF') s = s.substring(1);
        return s;
    }

    private static String[] split(String line, char delimiter) {
        // Simple split: matrix values are numeric without embedded delimiter in typical input.
        // This is good enough for CSV/TSV from most tools.
        String regex = delimiter == '\t' ? "\t" : ",";
        return line.split(regex, -1);
    }

    private static boolean[] assignTumor(double[] scores, GroupStrategy strategy) {
        double[] sorted = scores.clone();
        Arrays.sort(sorted);

        double threshold;
        if (strategy == GroupStrategy.MEDIAN) {
            threshold = percentile(sorted, 0.5);
        } else {
            threshold = percentile(sorted, 0.75); // upper quartile
        }

        boolean[] tumor = new boolean[scores.length];
        for (int i = 0; i < scores.length; i++) {
            tumor[i] = scores[i] >= threshold;
        }
        return tumor;
    }

    // percentile by linear interpolation, using sorted array index space [0..n-1]
    private static double percentile(double[] sorted, double p) {
        if (sorted.length == 0) return 0.0;
        if (sorted.length == 1) return sorted[0];
        double pos = p * (sorted.length - 1);
        int lo = (int) Math.floor(pos);
        int hi = (int) Math.ceil(pos);
        if (lo == hi) return sorted[lo];
        double w = pos - lo;
        return sorted[lo] * (1.0 - w) + sorted[hi] * w;
    }
}

