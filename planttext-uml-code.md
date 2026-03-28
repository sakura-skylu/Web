# PlantText UML 代码（DODA）

以下每段代码都可单独复制到 PlantText（[https://www.planttext.com/](https://www.planttext.com/)）直接生成 UML 图。

---

## 1) 系统分层组件图

```plantuml
@startuml
title DODA System Component Diagram
skinparam componentStyle rectangle
skinparam shadowing false

package "Frontend (Vue2)" {
  [Analysis.vue] as FE_ANALYSIS
  [Drug.vue] as FE_DRUG
}

package "Backend (Spring Boot)" {
  [DodaApplication] as APP
  [AnalysisTaskController] as ATC
  [DrugPredictionController] as DPC
  [ExpressionAnalysisService] as EAS
  [DrugPredictionService] as DPS
  [TaskRegistry] as TR
  [RExecutor] as RX
  [DrugPredictionMapper] as DPM
}

database "MySQL" as DB
folder "uploads/<taskId>" as FS
component "R Scripts\n(diff/estimate/gsva/icg/cibersort)" as RS

FE_ANALYSIS --> ATC : POST /api/v1/analysis/run
FE_ANALYSIS --> ATC : GET /api/v1/analysis/tasks/{taskId}
FE_ANALYSIS --> ATC : GET /api/v1/analysis/tasks/{taskId}/files/...

FE_DRUG --> DPC : GET /api/v1/drug-sensitivity

ATC --> EAS
EAS --> TR
EAS --> RX
RX --> RS
EAS --> FS

DPC --> DPS
DPS --> DPM
DPM --> DB

APP ..> ATC : CORS / MVC runtime
APP ..> DPC : CORS / MVC runtime
@enduml
```

---

## 2) 分析任务时序图（前端到 R 执行）

```plantuml
@startuml
title Analysis Task Sequence
autonumber

actor User
participant "Analysis.vue" as FE
participant "AnalysisTaskController" as ATC
participant "ExpressionAnalysisService" as EAS
participant "TaskRegistry" as TR
participant "GroupCsvGenerator" as GCG
participant "RExecutor" as RX
participant "R Script" as RS
collections "uploads/<taskId>" as FS

User -> FE : Upload matrix/group + click Run
FE -> ATC : POST /api/v1/analysis/run (multipart/form-data)
ATC -> EAS : submitAndRunAll(matrixCsv, groupCsv, strategy, logfc, padj)
EAS -> TR : create(taskId)
EAS -> FS : save matrix.csv

alt groupCsv is empty
  EAS -> GCG : generateGroupCsv(matrix.csv, strategy, group.csv)
  GCG --> EAS : group.csv
else groupCsv provided
  EAS -> FS : save group.csv
end

EAS --> ATC : AnalysisTaskInfoResponse(PENDING, taskId)
ATC --> FE : {taskId, status=PENDING}

loop every 2 seconds
  FE -> ATC : GET /api/v1/analysis/tasks/{taskId}
  ATC -> EAS : getTaskInfo(taskId)
  EAS -> TR : get(taskId)
  EAS --> ATC : status + results
  ATC --> FE : status + results
end

group Async Execution
  EAS -> RX : executeRscript(diff_analysis.R, args, log)
  RX -> RS : run DEG script
  RS --> RX : volcano_plot.png + diff_result.csv
  RX --> EAS : exit code/log

  EAS -> RX : executeRscript(estimate_analysis.R,...)
  RX -> RS : run ESTIMATE
  RS --> RX : estimate_plot.png + estimate_result.csv

  EAS -> RX : executeRscript(GSVA.R,...)
  RX -> RS : run GSVA
  RS --> RX : gsva image/csv

  EAS -> RX : executeRscript(immune_checkpoint_de.R,...)
  RX -> RS : run Immune Checkpoint
  RS --> RX : icg image/csv

  EAS -> RX : executeRscript(cibersort_analysis.R,...)
  RX -> RS : run CIBERSORT
  RS --> RX : cibersort image/csv
end

FE -> ATC : GET /api/v1/analysis/tasks/{taskId}/files/{type}/{filename}
ATC -> EAS : getTaskFile(taskId, type, filename)
EAS --> ATC : File
ATC --> FE : File stream
@enduml
```

---

## 3) 药敏查询时序图

```plantuml
@startuml
title Drug Sensitivity Query Sequence
autonumber

actor User
participant "Drug.vue" as FE
participant "DrugPredictionController" as DPC
participant "DrugPredictionService" as DPS
participant "DrugPredictionMapper" as DPM
database "MySQL" as DB

User -> FE : Select cancerType / input drugName / change page
FE -> DPC : GET /api/v1/drug-sensitivity\n(cancerType, drugName, page, page_size)
DPC -> DPC : sanitize page/page_size
DPC -> DPS : getDrugSensitivity(cancerType, drugName, safePage, safePageSize)

DPS -> DPS : normalizeKeyword(drugName)
DPS -> DPM : findDrugPage(cancerType, keyword, offset, limit)
DPM -> DB : SELECT ... LIMIT ... OFFSET ...
DB --> DPM : rows

DPS -> DPM : countDrug(cancerType, keyword)
DPM -> DB : SELECT COUNT(1) ...
DB --> DPM : total

DPM --> DPS : list + total
DPS --> DPC : PagedResult<DrugSensitivityItem>
DPC --> FE : ApiResponse.ok(data)
@enduml
```

---

## 4) 后端类图（分析模块）

```plantuml
@startuml
title Backend Class Diagram - Analysis Module
skinparam classAttributeIconSize 0
skinparam shadowing false

package "com.example.doda.analysis" {
  class AnalysisTaskController {
    +run(matrixCsv, groupCsv, groupStrategy, logfc, padj) : AnalysisTaskInfoResponse
    +taskInfo(taskId) : AnalysisTaskInfoResponse
    +getTaskFile(taskId, analysisTypeId, filename) : ResponseEntity
  }

  class ExpressionAnalysisService {
    +submitAndRunAll(matrixCsv, groupCsv, groupStrategy, logfc, padj) : AnalysisTaskInfoResponse
    +getTaskInfo(taskId) : AnalysisTaskInfoResponse
    +getTaskFile(taskId, analysisTypeId, filename) : File
    +shutdownExecutor() : void
    -runAll(task, matrixFile, groupFile, resultsDir, logfc, padj) : RunSummary
    -buildArgs(type, matrixFile, groupFile, outDir, engineRoot, logfc, padj) : List
    -getScriptFileName(type) : String
    -detectResultFiles(type, outDir) : AnalysisFileInfo
    -resolveResultFileName(dir, preferredNames, allowedExtensions) : String
    -imageCandidates(type) : List
    -csvCandidates(type) : List
    -resolveEngineRoot(configuredPath) : Path
    -resolveWorkRoot(configuredPath) : Path
    -safeMessage(throwable) : String
  }

  class TaskRegistry {
    +create(taskId) : AnalysisTask
    +get(taskId) : Optional
  }

  class AnalysisTask {
    -taskId : String
    -status : TaskStatus
    -message : String
    -createdAt : Instant
    -resultFiles : Map
    +getTaskId() : String
    +getStatus() : TaskStatus
    +setStatus(status) : void
    +getMessage() : String
    +setMessage(message) : void
    +getCreatedAt() : Instant
    +getResultFiles() : Map
    +setResultFile(analysisTypeId, fileInfo) : void
  }

  class GroupCsvGenerator {
    +generateGroupCsv(matrixCsv, strategy, groupOutCsv) : File
    -detectDelimiter(headerLine) : char
    -countChar(s, c) : int
    -cleanCell(cell) : String
    -split(line, delimiter) : String[]
    -assignTumor(scores, strategy) : boolean[]
    -percentile(sorted, p) : double
  }

  class RExecutor {
    +execute(command, logFile, timeoutMs) : int
    +executeRscript(scriptPath, scriptArgs, logFile, timeoutMs) : void
    -appendTimeoutLog(logFile, timeoutMs) : void
    -toWslPath(windowsPath) : String
    -toWslArg(arg) : String
    -escapeSingleQuotes(s) : String
  }

  enum AnalysisType {
    DEG
    ESTIMATE
    GSVA
    IMMUNE_CHECKPOINT
    CIBERSORT
    +fromId(id) : AnalysisType
  }

  enum TaskStatus {
    PENDING
    RUNNING
    SUCCESS
    FAILED
  }

  enum GroupStrategy {
    MEDIAN
    QUARTILE
  }
}

package "com.example.doda.analysis.dto" {
  class AnalysisTaskInfoResponse {
    +getTaskId() : String
    +getStatus() : TaskStatus
    +getMessage() : String
    +getCreatedAt() : Instant
    +getResults() : Map
  }
  class AnalysisFileInfo {
    +getImageFile() : String
    +getCsvFile() : String
  }
  class AnalysisRunResponse {
    +getTaskId() : String
  }
}

AnalysisTaskController --> ExpressionAnalysisService
ExpressionAnalysisService --> TaskRegistry
ExpressionAnalysisService --> RExecutor
ExpressionAnalysisService --> GroupCsvGenerator
TaskRegistry o-- AnalysisTask
AnalysisTask --> TaskStatus
AnalysisTask --> AnalysisFileInfo
AnalysisTaskController --> AnalysisTaskInfoResponse
AnalysisTaskInfoResponse --> TaskStatus
AnalysisTaskInfoResponse --> AnalysisFileInfo
ExpressionAnalysisService --> AnalysisType
ExpressionAnalysisService --> GroupStrategy
@enduml
```

---

## 5) 后端类图（药敏模块 + 通用实体）

```plantuml
@startuml
title Backend Class Diagram - Drug Module
skinparam classAttributeIconSize 0
skinparam shadowing false

package "com.example.doda.controller" {
  class DrugPredictionController {
    +getDrugPredictions(cancerType, drugName) : List
    +getDrugSensitivity(cancerType, drugName, page, pageSize) : ApiResponse
  }
}

package "com.example.doda.service" {
  class DrugPredictionService {
    +getPredictions(cancerType, drugName) : List
    +getDrugSensitivity(cancerType, drugName, page, pageSize) : PagedResult
    -normalizeKeyword(drugName) : String
  }
}

package "com.example.doda.mapper" {
  interface DrugPredictionMapper {
    +findDrugPage(cancerType, drugName, offset, limit) : List
    +countDrug(cancerType, drugName) : long
    +findDrug(cancerType, drugName) : List
  }
}

package "com.example.doda.entity" {
  class ApiResponse~T~ {
    +ok(data) : ApiResponse
    +getCode() : int
    +getMessage() : String
    +getData() : T
  }
  class PagedResult~T~ {
    +getItems() : List
    +getTotal() : long
    +getPage() : int
    +getPageSize() : int
  }
  class DrugPrediction {
    +getSampleId() : String
    +setSampleId(sampleId) : void
    +getDrugName() : String
    +setDrugName(drugName) : void
    +getSensitivityScore() : float
    +setSensitivityScore(score) : void
    +getCancerType() : String
    +setCancerType(cancerType) : void
  }
  class DrugSensitivityItem {
    +getSampleId() : String
    +getDrugName() : String
    +getSensitivityScore() : float
    +getCancerType() : String
    +getDataSource() : String
  }
}

database "MySQL (drug_predictions)" as DB

DrugPredictionController --> DrugPredictionService
DrugPredictionService --> DrugPredictionMapper
DrugPredictionService --> DrugPrediction
DrugPredictionService --> DrugSensitivityItem
DrugPredictionController --> ApiResponse
DrugPredictionController --> PagedResult
DrugPredictionMapper --> DB
@enduml
```

---

## 6) 任务状态机图

```plantuml
@startuml
title Analysis Task Status State Machine

[*] --> PENDING : submitAndRunAll()
PENDING --> RUNNING : executorService.submit()
RUNNING --> SUCCESS : successCount > 0
RUNNING --> FAILED : all failed / exception
PENDING --> FAILED : queue rejected

note right of SUCCESS
  可能是全部成功
  也可能是部分成功（message 标明失败项）
end note
@enduml
```

