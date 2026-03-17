<template>
  <div>
    <Head />
    <el-container class="workbench">
      <Menu />
      <el-main>
        <div class="page-title-wrap">
          <h1 class="page-title">Differential Expression Workbench</h1>
          <p>筛选数据 → 运行算法 → 查看结果</p>
        </div>

        <div class="tri-layout">
          <aside class="left-panel panel-card">
            <h3>参数配置</h3>
            <Select @change="handleCancerTypeChange" />
            <el-form label-position="top" class="param-form">
              <el-form-item label="log2FC 阈值">
                <el-slider v-model="params.logfc" :min="0.5" :max="4" :step="0.1" show-input />
              </el-form-item>
              <el-form-item label="Padj 阈值">
                <el-input-number v-model="params.padj" :min="0.0001" :max="0.1" :step="0.001" :precision="4" />
              </el-form-item>
              <el-form-item label="风险分组策略">
                <el-radio-group v-model="params.group">
                  <el-radio-button label="Median" />
                  <el-radio-button label="Quartile" />
                </el-radio-group>
              </el-form-item>
            </el-form>
            <el-button type="primary" :disabled="!selectedCancerType" @click="runAnalysis">运行分析</el-button>
          </aside>

          <section class="canvas panel-card">
            <el-skeleton :rows="5" animated v-if="loading" />
            <el-row v-else :gutter="16">
              <el-col v-for="chart in charts" :key="chart.type" :span="chart.span" class="chart-col">
                <div class="chart-card">
                  <div class="chart-head">
                    <strong>{{ chart.title }}</strong>
                    <span>
                      <el-button type="text" @click="openFull(chart.type)">全屏</el-button>
                      <el-button type="text" @click="downloadChart(chart.type)">下载</el-button>
                    </span>
                  </div>
                  <img :src="getChartUrl(chart.type)" :alt="chart.title" @click="openFull(chart.type)" />
                </div>
              </el-col>
            </el-row>
          </section>

          <aside class="detail panel-card">
            <h3>详情面板</h3>
            <el-table :data="candidateGenes" height="260" @row-click="selectGene" size="mini">
              <el-table-column prop="gene" label="Gene" width="120"/>
              <el-table-column prop="log2fc" label="log2FC"/>
              <el-table-column prop="padj" label="padj"/>
            </el-table>
            <div v-if="activeGene" class="gene-detail">
              <h4>{{ activeGene.gene }}</h4>
              <p>{{ activeGene.desc }}</p>
            </div>
            <div v-else class="gene-detail">点击左侧图或下方表格中的基因查看注释信息。</div>
          </aside>
        </div>
      </el-main>
    </el-container>

    <el-dialog :visible.sync="dialogVisible" width="70%" center>
      <img :src="dialogImgUrl" alt="Chart" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script>
import Head from '../components/Head.vue';
import Menu from '../components/Menu.vue';
import Select from '../components/Select.vue';

export default {
  name: 'Expression',
  components: { Menu, Head, Select },
  data() {
    return {
      selectedCancerType: '',
      loading: false,
      params: { logfc: 1, padj: 0.05, group: 'Median' },
      charts: [
        { title: 'DE Volcano', type: 'volcano', span: 12 },
        { title: 'Cox Risk', type: 'coxrisk', span: 12 },
        { title: 'Survival', type: 'survival', span: 12 },
        { title: 'Calibration', type: 'calibration', span: 12 },
        { title: 'GSVA Score', type: 'gsva', span: 24 }
      ],
      candidateGenes: [
        { gene: 'TP53', log2fc: 2.1, padj: 0.0003, desc: '抑癌基因，参与 DNA 损伤应答。' },
        { gene: 'EGFR', log2fc: 1.8, padj: 0.0011, desc: '受体酪氨酸激酶，常见于实体瘤增殖通路。' },
        { gene: 'CD274', log2fc: -1.2, padj: 0.0046, desc: 'PD-L1 编码基因，关联免疫逃逸。' }
      ],
      activeGene: null,
      dialogVisible: false,
      dialogImgUrl: ''
    };
  },
  methods: {
    handleCancerTypeChange(value) {
      this.selectedCancerType = value;
    },
    runAnalysis() {
      this.loading = true;
      setTimeout(() => {
        this.loading = false;
      }, 900);
    },
    getChartUrl(chartType) {
      if (!this.selectedCancerType) {
        return `/charts/example/${chartType === 'coxrisk' ? 'coxrisk' : chartType}.png`;
      }
      return `/charts/expression/${this.selectedCancerType}/${chartType}.png`;
    },
    openFull(chartType) {
      this.dialogImgUrl = this.getChartUrl(chartType);
      this.dialogVisible = true;
    },
    downloadChart(chartType) {
      const link = document.createElement('a');
      link.href = this.getChartUrl(chartType);
      link.download = `${this.selectedCancerType || 'example'}-${chartType}.png`;
      link.click();
    },
    selectGene(row) {
      this.activeGene = row;
    }
  }
};
</script>

<style scoped>
.workbench { background: #f4f7f6; }
.page-title-wrap { text-align: left; margin-bottom: 12px; }
.page-title { margin: 0; color: #2c3e50; }
.tri-layout { display: grid; grid-template-columns: 300px 1fr 320px; gap: 16px; }
.panel-card { background: #fff; border-radius: 10px; padding: 14px; box-shadow: 0 8px 20px rgba(44,62,80,.08); }
.canvas { min-height: 72vh; }
.chart-col { margin-bottom: 14px; }
.chart-card img { width: 100%; height: 260px; object-fit: contain; cursor: zoom-in; }
.chart-head { display: flex; justify-content: space-between; margin-bottom: 6px; }
.gene-detail { margin-top: 10px; text-align: left; color: #2c3e50; }
.param-form { margin: 12px 0; }
@media (max-width: 1400px) {
  .tri-layout { grid-template-columns: 260px 1fr; }
  .detail { grid-column: 1 / -1; }
}
</style>
