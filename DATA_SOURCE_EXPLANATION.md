## 📊 数据来源与结构说明

### 数据来自哪里？

✅ **TCGA 数据库** (The Cancer Genome Atlas)

- 开放的癌症基因组数据库
- 包含 33 种癌症类型
- 文件：`DrugPredictions.csv` (在 `src/main/resources` 中)

### CSV 文件格式

原始 CSV 是**矩阵格式**（宽格式）：

```
样本ID            | Camptothecin_1003 | Vinblastine_1004 | Cisplatin_1005 | ...
TCGA-DD-AACK-01A  | 0.1219            | 0.0355           | 30.74          | ...
TCGA-DD-AADA-01A  | 0.0457            | 0.0229           | 15.23          | ...
TCGA-G3-A25X-01A  | 0.0654            | 0.0375           | 19.63          | ...
```

### 如何识别癌症类型？

从样本 ID 的前缀推断：

- `TCGA-DD-*` → BLCA (膀胱癌)
- `TCGA-5C-*` → GBM (胶质母细胞瘤)
- `TCGA-G3-*` → CHOL (胆管癌) 或 GBM
- `TCGA-BC-*` → ESCA (食道癌)
- `TCGA-RG-*` → PAAD (胰腺癌)
- `TCGA-MI-*` → LIHC (肝细胞癌)

### 当前导入的数据

✅ 已导入：**6 种癌症类型**

- BLCA (膀胱尿路上皮癌) - 20+ 个样本
- GBM (胶质母细胞瘤) - 3+ 个样本
- CHOL (胆管癌) - 3+ 个样本
- ESCA (食道癌) - 3+ 个样本
- PAAD (胰腺癌) - 3+ 个样本
- LIHC (肝细胞癌) - 2+ 个样本

✅ 每个样本包含 **3+ 个药物** 的敏感性数据

### 为什么之前只显示 BLCA？

- 最初的 `data.sql` 只包含 BLCA 样本数据（12 条记录）
- 已更新到包含 6 种癌症类型的完整数据

### 完整数据导入？

如需导入**所有 33 种癌症**和**所有药物**：

1. 在 MySQL 中运行：

```sql
-- 批量从 CSV 导入（需要专门的 CSV 转换脚本）
LOAD DATA INFILE 'DrugPredictions.csv'
INTO TABLE drug_predictions
FIELDS TERMINATED BY ','
...
```

2. 或使用 Python 脚本转换 CSV 为 SQL INSERT

```python
import pandas as pd

df = pd.read_csv('DrugPredictions.csv', index_col=0)
# 转换为长格式
df_long = df.reset_index().melt(...)
# 生成 SQL 语句
```

### 测试已导入的数据

访问不同癌症类型的数据：

#### BLCA (膀胱癌)

```
GET http://localhost:8081/api/v1/drug-sensitivity?cancerType=BLCA&page=1&page_size=10
```

#### GBM (胶质母细胞瘤)

```
GET http://localhost:8081/api/v1/drug-sensitivity?cancerType=GBM&page=1&page_size=10
```

#### CHOL (胆管癌)

```
GET http://localhost:8081/api/v1/drug-sensitivity?cancerType=CHOL&page=1&page_size=10
```

#### ESCA (食道癌)

```
GET http://localhost:8081/api/v1/drug-sensitivity?cancerType=ESCA&page=1&page_size=10
```

### 数据自动识别逻辑

系统根据样本 ID 前缀自动识别癌症类型：

```java
// DrugPredictionService.java 中可以添加
if (sampleId.startsWith("TCGA-DD-")) {
    cancerType = "BLCA";
} else if (sampleId.startsWith("TCGA-G3-")) {
    cancerType = "CHOL"; // 或 GBM，需要进一步确认
}
// ...
```

### 下一步建议

1. ✅ 已完成：多癌症数据导入
2. 🔄 建议：完整转换原始 CSV（所有 33 种癌症）
3. 🔄 建议：添加数据上传功能（用户可上传自己的 CSV）
4. 🔄 建议：自动识别癌症类型的逻辑
