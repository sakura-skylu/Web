-- 创建 drug_predictions 表
CREATE TABLE IF NOT EXISTS drug_predictions (
    sampleId VARCHAR(50),
    drugName VARCHAR(100),
    sensitivityScore DOUBLE,
    cancerType VARCHAR(10),
    PRIMARY KEY (sampleId, drugName),
    INDEX idx_cancer_type (cancerType),
    INDEX idx_drug_name (drugName)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
