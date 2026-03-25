<template>
  <div>
    <Head />
    <el-container class="expr-page">
      <Menu />
      <el-main>
        <div class="expr-header">
          <h1 class="page-title">Expression</h1>
          <p class="page-subtitle">选择癌种，展示对应的示例图（charts/expression 下）</p>
        </div>

        <div class="selector-wrap">
          <Select @change="handleCancerTypeChange" />
        </div>

        <el-row :gutter="16" class="charts-grid">
          <el-col v-for="chart in charts" :key="chart.typeId" :span="chart.span" class="chart-col">
            <div class="chart-card">
              <div class="chart-card-head">
                <strong>{{ chart.title }}</strong>
                <span class="chart-actions">
                  <el-button type="text" @click="openFull(chart.typeId)">全屏</el-button>
                  <el-button type="text" @click="downloadChart(chart.typeId)">下载</el-button>
                </span>
              </div>

              <div class="img-wrap">
                <img
                  :src="getChartUrl(chart.fileName)"
                  :alt="chart.title"
                  class="chart-img"
                  @click="openFull(chart.typeId)"
                />
              </div>
            </div>
          </el-col>
        </el-row>
      </el-main>
    </el-container>

    <el-dialog :visible.sync="dialogVisible" width="70%" center :show-close="true">
      <img :src="dialogImgUrl" alt="Chart" style="width: 100%;" />
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
      dialogVisible: false,
      dialogImgUrl: '',
      charts: [
        { title: 'DE Volcano', typeId: 'deg', fileName: 'volcano.png', span: 12 },
        { title: 'ESTIMATE', typeId: 'estimate', fileName: 'cox.png', span: 12 },
        { title: 'GSVA Score', typeId: 'gsva', fileName: 'gsva.png', span: 12 },
        { title: 'Immune Checkpoint', typeId: 'immune_checkpoint', fileName: 'survival.png', span: 12 },
        { title: 'CIBERSORT', typeId: 'cibersort', fileName: 'calibration.png', span: 24 }
      ]
    };
  },
  methods: {
    handleCancerTypeChange(value) {
      this.selectedCancerType = value;
    },
    getChartUrl(fileName) {
      if (this.selectedCancerType) {
        return `/charts/expression/${this.selectedCancerType}/${fileName}`;
      }
      return `/charts/example/${fileName}`;
    },
    openFull(typeId) {
      const chart = this.charts.find((c) => c.typeId === typeId);
      if (!chart) return;
      this.dialogImgUrl = this.getChartUrl(chart.fileName);
      this.dialogVisible = true;
    },
    downloadChart(typeId) {
      const chart = this.charts.find((c) => c.typeId === typeId);
      if (!chart) return;
      const url = this.getChartUrl(chart.fileName);
      const link = document.createElement('a');
      link.href = url;
      link.download = chart.fileName;
      link.click();
    }
  }
};
</script>

<style scoped>
.expr-page {
  background: #f4f7f6;
}

.expr-header {
  margin-bottom: 14px;
}

.page-title {
  margin: 0;
  color: #2c3e50;
  font-size: 30px;
  font-weight: 700;
}

.page-subtitle {
  margin: 6px 0 0;
  color: #6b7b8c;
  font-size: 16px;
}

.selector-wrap {
  background: #fff;
  border-radius: 10px;
  padding: 12px 16px;
  box-shadow: 0 8px 20px rgba(44, 62, 80, 0.06);
  margin-bottom: 16px;
}

.charts-grid {
  margin-bottom: 16px;
}

.chart-card {
  background: #fff;
  border-radius: 12px;
  padding: 12px 12px 10px;
  box-shadow: 0 8px 20px rgba(44, 62, 80, 0.06);
  border: 1px solid rgba(44, 62, 80, 0.08);
}

.chart-card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.chart-actions .el-button {
  padding: 0 6px;
}

.img-wrap {
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.chart-img {
  width: 100%;
  height: 260px;
  object-fit: contain;
  cursor: zoom-in;
  border-radius: 10px;
}

.chart-col {
  margin-bottom: 14px;
}

@media (max-width: 1400px) {
  .chart-img {
    height: 220px;
  }
}
</style>
