<!-- CNV棒棒糖 -->
<template>
    <div>
        <Head />
        <el-container>
            <Menu />
            <el-main>
                <h1 class="page-title">Mutation</h1>
                <div class="divider"></div>
                <!-- 选择框 -->
                <Select @change="handleCancerTypeChange" />
                <div class="chart-container">
                    <h3>Copy Number Variation</h3>
                    <img
                        v-if="selectedCancerType"
                        :src="getChartUrl('cnv')"
                        alt="CNV"
                        @click="handleImgClick('cnv')"
                        style="cursor: zoom-in;"
                    />
                    <div v-else class="chart-container">
                        <el-row>
                            <el-col :span="12">
                                <img src="\charts\example\cnv.png" alt="CNV" /><h3>(e.g.)</h3>
                            </el-col>
                            <el-col :span="12">
                                <p v-html="Intro" class="intro-text"></p>
                            </el-col>
                        </el-row>
                    </div>
                </div>
            </el-main>
        </el-container>   
        <!-- 放大弹窗 -->
        <el-dialog :visible.sync="dialogVisible" width="60%" :show-close="true" center >
          <img :src="dialogImgUrl" alt="CNV" style="width: 100%; height: auto; display: block; margin: 0 auto;" />
        </el-dialog>     
    </div>
</template>

<script>
import Head from '../components/Head.vue';
import Menu from '../components/Menu.vue';
import Select from '../components/Select.vue';

export default{
    name:'Mutation',
    components:{
        Menu, Head, Select
    },
    data() {
      return {
        selectedCancerType: '',// 初始化为空字符串
        Intro:
          "Copy number variation (CNV) refers to the gain or loss of DNA segments in the genome, which can lead to changes in gene dosage and expression levels. CNVs are associated with various diseases, including cancer, where they can contribute to tumorigenesis and progression. In this DODA, CNV analysis is integrated into a user-friendly web platform, allowing users to easily visualize and interpret CNV data for their research and clinical applications.",
        dialogVisible: false, // 控制弹窗显示
        dialogImgUrl: ''    // 当前放大的图片地址
      }
    },
  methods: {
    // 处理选择框变化
    handleCancerTypeChange(value) {
      console.log('Selected Cancer Type:', value); // 调试日志
      this.selectedCancerType = value;
    },
    // 根据图表类型和选择的 cancer type 获取图表 URL
    getChartUrl(chartType) {
      const url = `/charts/mutation/${this.selectedCancerType}/${chartType}.png`;
      console.log('Generated URL:', url); // 调试日志
      return url;
    },
    // 点击图片放大
    handleImgClick(chartType) {
      this.dialogImgUrl = this.getChartUrl(chartType);
      this.dialogVisible = true;
      console.log('dialogVisible:', this.dialogVisible, 'dialogImgUrl:', this.dialogImgUrl);
    }
  }
}
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

.intro-text {
    padding: 10px;
    letter-spacing: 0.8px; /* 字间距 */
    line-height: 1.7; /* 行间距 */
    font-size: 24px; /* 调整字体大小 */
    text-align: justify; /* 让文本对齐（可选） */
    text-indent: 2em; /* 首行缩进2字符 */
    display: flex; /* 使用 flex 布局 */
    align-items: center; /* 垂直居中 */
    justify-content: center; /* 水平居中（可选，根据需求） */
    height: 100%; /* 确保容器高度 */
}
/* 图表容器样式 */
.chart-container {
    min-height: 77%;
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
 /* overflow: hidden;  防止图片溢出容器 */
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