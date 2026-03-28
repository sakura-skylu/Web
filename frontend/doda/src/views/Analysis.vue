<template>
  <div>
    <Head />
    <el-container class="analysis-page">
      <Menu />
      <el-main class="analysis-main">
        <div class="analysis-header">
          <h1 class="page-title">Expression Analysis</h1>
          <p class="page-subtitle">上传表达矩阵并运行分析，查看结果图与 DEG 列表</p>
        </div>

        <div class="analysis-layout">
          <aside class="panel-card input-panel">
            <h3>输入与参数</h3>

            <div class="field-block">
              <span class="field-label">上传表达矩阵 CSV（必传）</span>
              <el-upload
                drag
                class="upload-box"
                action="#"
                :auto-upload="false"
                :show-file-list="true"
                accept=".csv,.tsv"
                :on-change="handleMatrixChange"
                :on-remove="handleMatrixRemove"
              >
                <i class="el-icon-upload" />
                <div class="upload-text">拖拽或点击选择文件</div>
                <div class="upload-hint">第一列为基因名；其余列为样本ID；数值为表达量</div>
              </el-upload>
            </div>

            <div class="field-block">
              <span class="field-label">上传分组表 CSV（选传）</span>
              <el-upload
                drag
                class="upload-box"
                action="#"
                :auto-upload="false"
                :show-file-list="true"
                accept=".csv,.tsv"
                :on-change="handleGroupChange"
                :on-remove="handleGroupRemove"
              >
                <i class="el-icon-upload" />
                <div class="upload-text">拖拽或点击选择文件</div>
                <div class="upload-hint">第一列为样本ID；第二列为分组（Normal/Tumor 或 0/1/Control/Treat 等）</div>
              </el-upload>
            </div>

            <el-form label-position="top" class="param-form">
              <el-form-item label="分组策略（未上传分组 CSV 时生效）">
                <el-radio-group v-model="params.groupStrategy">
                  <el-radio-button label="MEDIAN" />
                  <el-radio-button label="QUARTILE" />
                </el-radio-group>
              </el-form-item>

              <el-form-item label="DEG log2FC 阈值（仅影响火山图）">
                <el-slider v-model="params.logfc" :min="0.5" :max="4" :step="0.1" show-input />
              </el-form-item>

              <el-form-item label="DEG Padj 阈值（仅影响火山图）">
                <el-input-number
                  v-model="params.padj"
                  :min="0.0001"
                  :max="0.1"
                  :step="0.001"
                  :precision="4"
                />
              </el-form-item>
            </el-form>

            <el-button type="primary" class="run-btn" :disabled="!matrixFile || loading" @click="runAnalysis">
              运行全部 5 种分析
            </el-button>
          </aside>

          <section class="panel-card canvas-panel">
            <el-skeleton :rows="8" animated v-if="loading" />

            <el-row v-else :gutter="16">
              <el-col
                v-for="chart in charts"
                :key="chart.typeId"
                :span="chart.span"
                class="chart-col"
              >
                <div class="chart-card">
                  <div class="chart-head">
                    <strong>{{ chart.title }}</strong>
                    <span>
                      <el-button type="text" :disabled="!hasImage(chart.typeId)" @click="openFull(chart.typeId)">
                        全屏
                      </el-button>
                      <el-button type="text" :disabled="!hasImage(chart.typeId)" @click="downloadChart(chart.typeId)">
                        下载
                      </el-button>
                    </span>
                  </div>

                  <div v-if="hasImage(chart.typeId)" class="img-wrap">
                    <img
                      :src="getResultImageUrl(chart.typeId)"
                      :alt="chart.title"
                      @click="openFull(chart.typeId)"
                      class="chart-img"
                    />
                  </div>
                  <div v-else class="img-placeholder">结果未生成（请检查分组或数据是否充分）</div>
                </div>
              </el-col>
            </el-row>
          </section>

          <aside class="panel-card deg-panel">
            <h3>DEG 结果（前 20 个）</h3>

            <el-table
              v-if="candidateGenes.length"
              :data="candidateGenes"
              height="260"
              @row-click="selectGene"
              size="mini"
              style="width: 100%"
            >
              <el-table-column prop="gene" label="Gene" width="120" />
              <el-table-column prop="log2fc" label="log2FC" />
              <el-table-column prop="padj" label="padj" />
            </el-table>

            <div v-if="!candidateGenes.length && !loading" class="gene-detail">
              DEG CSV 还没生成；或本次运行未生成 DEG 结果。
            </div>

            <div v-if="activeGene" class="gene-detail">
              <h4>{{ activeGene.gene }}</h4>
              <p>{{ activeGene.desc }}</p>
            </div>
          </aside>
        </div>
      </el-main>
    </el-container>

    <el-dialog :visible.sync="dialogVisible" width="70%" center :show-close="true">
      <img :src="dialogImgUrl" alt="Chart" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios';
import Head from '../components/Head.vue';
import Menu from '../components/Menu.vue';

export default {
  name: 'Analysis',
  components: { Head, Menu },
  data() {
    return {
      loading: false,
      matrixFile: null,
      groupFile: null,
      taskId: '',
      taskStatus: '',
      taskResults: {},
      params: {
        groupStrategy: 'MEDIAN',
        logfc: 1,
        padj: 0.05
      },
      charts: [
        { title: 'DE Volcano', typeId: 'deg', span: 12 },
        { title: 'ESTIMATE', typeId: 'estimate', span: 12 },
        { title: 'GSVA Score', typeId: 'gsva', span: 12 },
        { title: 'Immune Checkpoint', typeId: 'immune_checkpoint', span: 12 },
        { title: 'CIBERSORT', typeId: 'cibersort', span: 24 }
      ],
      candidateGenes: [],
      activeGene: null,
      dialogVisible: false,
      dialogImgUrl: ''
    };
  },
  methods: {
    handleMatrixChange(file) {
      this.matrixFile = file.raw || file;
    },
    handleMatrixRemove() {
      this.matrixFile = null;
    },
    handleGroupChange(file) {
      this.groupFile = file.raw || file;
    },
    handleGroupRemove() {
      this.groupFile = null;
    },

    async runAnalysis() {
      if (!this.matrixFile) return;

      this.loading = true;
      this.taskId = '';
      this.taskStatus = '';
      this.taskResults = {};
      this.candidateGenes = [];
      this.activeGene = null;

      try {
        const formData = new FormData();
        formData.append('matrixCsv', this.matrixFile);
        if (this.groupFile) formData.append('groupCsv', this.groupFile);
        formData.append('groupStrategy', this.params.groupStrategy);
        formData.append('logfc', this.params.logfc);
        formData.append('padj', this.params.padj);

        const runResp = await axios.post('http://localhost:8081/api/v1/analysis/run', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });

        this.taskId = runResp.data.taskId;

        const pollInterval = 2000;
        // eslint-disable-next-line no-undef
        const timer = setInterval(async () => {
          try {
            const infoResp = await axios.get(`http://localhost:8081/api/v1/analysis/tasks/${this.taskId}`);
            const info = infoResp.data || {};
            this.taskStatus = info.status || '';

            if (info.status === 'SUCCESS') {
              clearInterval(timer);
              this.loading = false;
              this.taskResults = info.results || {};
              await this.loadDegGenesIfAvailable();
            } else if (info.status === 'FAILED') {
              clearInterval(timer);
              this.loading = false;
              this.$message.error(info.message || '任务执行失败');
            }
          } catch (e) {
            clearInterval(timer);
            this.loading = false;
            this.$message.error('轮询任务状态失败');
          }
        }, pollInterval);
      } catch (e) {
        this.loading = false;
        this.$message.error('提交分析任务失败');
      }
    },

    hasImage(typeId) {
      const r = this.taskResults[typeId];
      return r && r.imageFile;
    },

    getResultImageUrl(typeId) {
      const r = this.taskResults[typeId];
      if (!r || !r.imageFile) return '';
      const filename = encodeURIComponent(r.imageFile);
      return `http://localhost:8081/api/v1/analysis/tasks/${this.taskId}/files/${typeId}/${filename}`;
    },

    openFull(typeId) {
      const url = this.getResultImageUrl(typeId);
      if (!url) return;
      this.dialogImgUrl = url;
      this.dialogVisible = true;
    },

    downloadChart(typeId) {
      const url = this.getResultImageUrl(typeId);
      if (!url) return;
      const r = this.taskResults[typeId];
      const downloadName = r && r.imageFile ? `${typeId}-${r.imageFile}` : `${typeId}.png`;
      const link = document.createElement('a');
      link.href = url;
      link.download = downloadName;
      link.click();
    },

    async loadDegGenesIfAvailable() {
      const deg = this.taskResults['deg'];
      if (!deg || !deg.csvFile) return;

      const csvResp = await axios.get(
        `http://localhost:8081/api/v1/analysis/tasks/${this.taskId}/files/deg/${encodeURIComponent(deg.csvFile)}`,
        { responseType: 'text' }
      );

      const genes = this.parseDegCsv(csvResp.data);
      this.candidateGenes = genes;
    },

    parseDegCsv(text) {
      if (!text) return [];
      const lines = text.trim().split(/\r?\n/);
      if (lines.length < 2) return [];

      const header = lines[0].split(',');
      const idxGene = header.indexOf('gene_symbol');
      const idxLog2fc = header.indexOf('logFC');
      const idxPadj = header.indexOf('adj.P.Val');

      if (idxGene === -1 || idxLog2fc === -1 || idxPadj === -1) return [];

      const rows = [];
      const limit = Math.min(20, lines.length - 1);
      for (let i = 1; i <= limit; i++) {
        const cols = lines[i].split(',');
        const gene = cols[idxGene];
        const log2fc = parseFloat(cols[idxLog2fc]);
        const padj = parseFloat(cols[idxPadj]);
        if (!gene) continue;
        rows.push({
          gene,
          log2fc: isNaN(log2fc) ? null : log2fc,
          padj: isNaN(padj) ? null : padj,
          desc: 'N/A'
        });
      }
      return rows;
    },

    selectGene(row) {
      this.activeGene = row;
    }
  }
};
</script>

<style scoped>
.analysis-page {
  background: transparent;
}

.analysis-main {
  padding: 16px 18px;
  overflow-y: auto;
}

.analysis-header {
  margin-bottom: 14px;
}

.page-title {
  margin: 0;
  color: var(--app-text);
  font-size: 32px;
  font-weight: 700;
  letter-spacing: 0.2px;
}

.page-subtitle {
  margin: 6px 0 0;
  color: var(--app-muted);
  font-size: 16px;
}

.analysis-layout {
  display: grid;
  grid-template-columns: 320px 1fr 360px;
  gap: 16px;
}

.panel-card {
  background: var(--app-surface);
  border-radius: var(--app-radius);
  padding: 14px;
  box-shadow: var(--app-shadow-soft);
  border: 1px solid var(--app-border);
}

.input-panel h3,
.deg-panel h3 {
  margin-top: 0;
  margin-bottom: 10px;
}

.field-block {
  margin-bottom: 14px;
}

.field-label {
  display: block;
  text-align: left;
  margin-bottom: 8px;
  color: var(--app-text);
  font-weight: 600;
}

.upload-text {
  margin-top: 6px;
}

.upload-hint {
  margin-top: 6px;
  color: var(--app-muted);
  font-size: 12px;
}

.param-form {
  margin: 8px 0 14px;
}

.run-btn {
  width: 100%;
  border-radius: 10px;
  font-weight: 600;
  box-shadow: 0 6px 16px rgba(31, 126, 163, 0.24);
}

.canvas-panel {
  min-height: 72vh;
}

.chart-card {
  border-radius: var(--app-radius-sm);
  padding: 12px;
  border: 1px solid var(--app-border);
  background: var(--app-surface-soft);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.chart-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--app-shadow-soft);
}

.chart-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.img-wrap {
  display: flex;
  justify-content: center;
}

.chart-img {
  width: 100%;
  height: 260px;
  object-fit: contain;
  cursor: zoom-in;
  border-radius: 10px;
  background: #ffffff;
  border: 1px solid rgba(23, 50, 72, 0.08);
}

.img-placeholder {
  min-height: 260px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--app-muted);
  text-align: center;
  padding: 12px;
}

.gene-detail {
  margin-top: 10px;
  text-align: left;
  color: var(--app-text);
}

@media (max-width: 1400px) {
  .analysis-layout {
    grid-template-columns: 260px 1fr;
  }
}
</style>

