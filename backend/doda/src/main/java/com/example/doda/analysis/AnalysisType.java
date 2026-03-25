package com.example.doda.analysis;

public enum AnalysisType {
    DEG("deg", "DE Volcano", "volcano_plot.png", "diff_result.csv"),
    ESTIMATE("estimate", "Estimate", "estimate_plot.png", "estimate_result.csv"),
    GSVA("gsva", "GSVA", "gsva_barplot.png", "gsva_scores.csv"),
    IMMUNE_CHECKPOINT("immune_checkpoint", "Immune Checkpoint", "icg_plot.png", "icg_expression.csv"),
    CIBERSORT("cibersort", "CIBERSORT", "cibersort_plot.png", "cibersort_result.csv");

    public final String id;
    public final String title;
    public final String defaultImageFile;
    public final String defaultCsvFile;

    AnalysisType(String id, String title, String defaultImageFile, String defaultCsvFile) {
        this.id = id;
        this.title = title;
        this.defaultImageFile = defaultImageFile;
        this.defaultCsvFile = defaultCsvFile;
    }

    public static AnalysisType fromId(String id) {
        for (AnalysisType t : values()) {
            if (t.id.equalsIgnoreCase(id)) return t;
        }
        throw new IllegalArgumentException("Unknown analysisType: " + id);
    }
}

