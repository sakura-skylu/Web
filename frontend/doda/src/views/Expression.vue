<!-- DE火山图，
    cox风险图，生存曲线图（列线图），校准曲线
    GSVA
-->
<template>
  <div>
    <Head />
    <el-container>
      <Menu />
        <el-main>
          <h1 class="page-title">Expression</h1>
          <div class="divider"></div>
          <!-- 选择框 -->
          <Select @change="handleCancerTypeChange" />

          <el-row :gutter="20">
            <el-col v-for="chart in charts" :key="chart.type" :span="chart.span" class="chart-col">
              <div class="chart-container">
                <h3>{{ chart.title }}</h3>
                <template v-if="selectedCancerType">
                  <img
                    :src="getChartUrl(chart.type)"
                    :alt="chart.title"
                    @click="handleImgClick(chart.type)"
                    class="chart-image"
                  />
                </template>
                <template v-else>
                  <div class="example-placeholder">
                    <img :src="chart.exampleUrl" :alt="chart.title + ' example'" />
                    <h3>(e.g.)</h3>
                  </div>
                </template>
              </div>
            </el-col>
          </el-row>

        </el-main>
    </el-container>

    <!-- 放大弹窗 -->
    <el-dialog :visible.sync="dialogVisible" width="65%" :show-close="true" center>
      <img :src="dialogImgUrl" alt="Chart" style="width: 100%; height: auto; display: block; margin: 0 auto;" />
    </el-dialog>
  </div>
</template>

<script>
import Head from '../components/Head.vue';
import Menu from '../components/Menu.vue';
import Select from '../components/Select.vue';

export default {
  name: 'Expression',
  components: {
    Menu,
    Head,
    Select
  },
  data() {
    return {
      selectedCancerType: '', // 初始化为空字符串
      charts: [
        { title: 'DE Volcano', type: 'volcano', span: 12, exampleUrl: '/charts/example/volcano.png' },
        { title: 'Cox Risk', type: 'cox', span: 12, exampleUrl: '/charts/example/coxrisk.png' },
        { title: 'Survival', type: 'survival', span: 12, exampleUrl: '/charts/example/survival.png' },
        { title: 'Calibration', type: 'calibration', span: 12, exampleUrl: '/charts/example/calibration.png' },
        { title: 'GSVA Score', type: 'gsva', span: 24, exampleUrl: '/charts/example/gsva.png' }
      ],
      dialogVisible: false,   // 控制弹窗显示
      dialogImgUrl: ''       // 当前放大的图片地址
    };
  },
  methods: {
    // 处理选择框变化
    handleCancerTypeChange(value) {
      console.log('Selected Cancer Type:', value);
      this.selectedCancerType = value;
    },
    // 根据图表类型和选择的 cancer type 获取图表 URL
    getChartUrl(chartType) {
      const url = `/charts/expression/${this.selectedCancerType}/${chartType}.png`;
      console.log('Generated URL:', url);
      return url;
    },
    // 点击图片放大
    handleImgClick(chartType) {
      this.dialogImgUrl = this.getChartUrl(chartType);
      this.dialogVisible = true;
      console.log('dialogVisible:', this.dialogVisible, 'dialogImgUrl:', this.dialogImgUrl);
    }
  }
};
</script>

<style scoped>
::v-deep .el-dialog {
  height: auto;
  position: relative;
  margin: 15vh auto 50px;
  background: #fff;
  border-radius: 2px;
  box-shadow: 0 1px 3px rgb(0 0 0 / 30%);
  box-sizing: border-box;
}
.chart-col {
  height: 500px;
  margin-bottom: 20px;
}
.el-main {
  max-height: 88vh; /* 设置最大高度，允许内容区滚动 */
  overflow-y: auto; /* 当内容超出时显示垂直滚动条 */
}
/* 图表容器样式 */
.chart-container {
    box-shadow: 0 0 20px rgba(0, 0, 0, 0.12); /* 阴影效果 */
  border: 2px solid #ddd;
  padding: 10px;
  border-radius: 10px;
  text-align: center;
  background-color: #ffffff;
  height: 500px; /* 设置固定高度 */
  display: flex; /* 使用 flex 布局 */
  flex-direction: column; /* 垂直排列内容 */
  justify-content: center; /* 垂直居中 */
  align-items: center; /* 水平居中 */
  overflow: hidden; /* 防止图片溢出容器 */
}

.chart-container h3 {
  height: 5%;
  margin-bottom: 10px;
  font-size: 20px;
  color: #333333;
}

.chart-image {
  cursor: zoom-in;
  max-width: 100%;
  height: 90%;
  object-fit: contain; /* 保持图片比例，适应容器 */
  display: block;
  margin: 0 auto;
}

.example-placeholder {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 90%;
}

.example-placeholder img {
  max-width: 100%;
  max-height: 90%;
  object-fit: contain;
  display: block;
  margin: 0 auto;
}

/* 页面标题样式 */
.page-title {
  height: auto;
  font-size: 32px; /* 放大字体 */
  font-weight: bold;
  color: #333333;
  margin: 0; /* 去掉默认外边距 */;
  text-align: left; /* 靠左对齐 */
}

/* 分隔线样式 */
.divider {
  width: 100%;
  height: 1px;
  background-color: #000000; /* 分隔线颜色 */
  margin: 0px 0 10px 0; /* 设置上下间距 */
}
</style>