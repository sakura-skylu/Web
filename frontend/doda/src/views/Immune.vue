<!-- 免疫浸润，免疫评分 -->
<template>
    <div>
        <Head v-if="!embedded" />
        <el-container>
            <Menu v-if="!embedded" />
            <el-main>
                <h1 class="page-title">Immune</h1>
                <div class="divider"></div>
                <Select @change="handleCancerTypeChange" />

          <el-row :gutter="20" v-if="selectedCancerType">
            <el-col :span="8" v-for="chart in charts" :key="chart.name" class="chart-col">
              <div class="chart-container">
                <h3>{{ chart.name }}</h3>
                <img :src="getChartUrl(chart.name)" :alt="chart.name"
                     @click="handleImgClick(chart.name)"
                     class="chart-image"/>
              </div>
            </el-col>
          </el-row>
                
          <div v-else class="chart-container">
            <el-row>
              <el-col :span="12" style="text-align: center;">
                <img style="margin-top: 20px;" src="/charts/example/immune.png" alt="immune example" />
                <h3>(e.g.)</h3>
              </el-col>
              <el-col :span="12" style="text-align: center;">
                <p v-html="Intro" class="intro-text"></p>
              </el-col>
            </el-row>
          </div>
            </el-main>
          <!-- 放大弹窗 -->
          <el-dialog :visible.sync="dialogVisible" width="45%" :show-close="true" center>
            <img :src="dialogImgUrl" alt="Immune Chart" style="width: 100%; height: auto; object-fit: contain;" />
          </el-dialog>
        </el-container>
    </div>
</template>

<script>
import Head from '../components/Head.vue';
import Menu from '../components/Menu.vue';
import Select from '../components/Select.vue';

export default{
    name:'Immune',
    components:{
        Menu, Head, Select
    },
    props: {
      // 当被 Analysis 嵌入时，隐藏外层 Head/Menu，避免重复
      embedded: {
        type: Boolean,
        default: false
      }
    },
    data() {
    return {
      selectedCancerType: '',  // 初始化为空字符串
      charts: [
        { name: 'B cells naive' },
        { name: 'Macrophages M2' },
        { name: 'Macrophages M0' },
        { name: 'NK cells resting' },
        { name: 'T cells follicular helper' },
        { name: 'T cells CD4 memory resting' }
      ],
      Intro:
        'The level of immune cell infiltration refers to the extent to which immune cells enter and are present in the tissue or tumor microenvironment, which is of great significance for disease progression, prognosis, and response to therapy. Immune cells, including T-cells, macrophages, and B-cells, are key elements in the body\'s defense against infection and disease, and they recognize and destroy pathogens and abnormal cells, such as cancer cells.<br>&nbsp&nbsp&nbsp&nbspIn this DODA, the infiltration levels of 6 different human immune cells were analyzed by the CIBERSORT algorithm in the high- and low-risk groups.',
      dialogVisible: false, // 控制弹窗显示
      dialogImgUrl: '',     // 当前放大的图片地址
    }
  },
  methods: {
    // 处理选择框变化
    handleCancerTypeChange(value) {
      this.selectedCancerType = value;
    },
    // 根据图表类型和选择的 cancer type 获取图表 URL
    getChartUrl(chartType) {
      // chartType 里包含空格（如 "B cells naive"），URL 里需要编码避免静态资源路径匹配失败
      const safeChartType = encodeURIComponent(chartType);
      return `/charts/immune/${this.selectedCancerType}/${safeChartType}.png`;
    },
    // 点击图片放大
    handleImgClick(chartType) {
      this.dialogImgUrl = this.getChartUrl(chartType);
      this.dialogVisible = true;
    }
  } 
}
</script>

<style scoped>
.chart-col {
  margin-bottom: 20px;
}
.chart-image {
  cursor: zoom-in;
  width: 100%;
  height: auto;
}
::v-deep .el-dialog {
  height: auto;
  position: relative;
  margin: 15vh auto 50px;
  background: #fff;
  border-radius: 2px;
  box-shadow: 0 1px 3px rgb(0 0 0 / 30%);
  box-sizing: border-box;
}
.intro-text {
    padding: 10px;
    letter-spacing: 0.5px; /* 字间距 */
    line-height: 1.6; /* 行间距 */
    font-size: 24px; /* 调整字体大小 */
    text-align: justify; /* 让文本对齐（可选） */
    text-indent: 2em; /* 首行缩进2字符 */
    display: flex; /* 使用 flex 布局 */
    align-items: center; /* 垂直居中 */
    justify-content: center; /* 水平居中（可选，根据需求） */
    height: 100%; /* 确保容器高度 */
}
.el-main {
  max-height: 88vh; /* 设置最大高度，允许内容区滚动 */
  overflow-y: auto; /* 当内容超出时显示垂直滚动条 */
}
.el-row{
  height: 500px;
  margin-bottom: 20px; /* 设置底部间距 */
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

.chart-container img {
  max-width: 100%;
  height: 90%;
  object-fit: contain; /* 保持图片比例，适应容器 */
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