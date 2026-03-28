<!-- CNV棒棒糖 -->
<template>
    <div>
        <Head v-if="!embedded" />
        <el-container>
            <Menu v-if="!embedded" />
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
                                <img src="/charts/example/cnv.png" alt="CNV" /><h3>(e.g.)</h3>
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
    props: {
      // 当被 Analysis 嵌入时，隐藏外层 Head/Menu，避免重复
      embedded: {
        type: Boolean,
        default: false
      }
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
  margin: 10vh auto 40px;
  border-radius: var(--app-radius-sm);
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(23, 50, 72, 0.25);
}

.intro-text {
  padding: 14px;
  letter-spacing: 0.3px;
  line-height: 1.72;
  font-size: 20px;
  text-align: justify;
  text-indent: 2em;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--app-text);
}

.chart-container {
  min-height: 77%;
  box-shadow: var(--app-shadow-soft);
  border: 1px solid var(--app-border);
  padding: 14px;
  border-radius: var(--app-radius);
  text-align: center;
  background-color: var(--app-surface);
  height: 500px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.chart-container h3 {
  height: 5%;
  margin-bottom: 10px;
  font-size: 22px;
  color: var(--app-text);
}

.chart-container img {
  max-width: 100%;
  height: 90%;
  object-fit: contain;
  display: block;
  margin: 0 auto;
  border-radius: 10px;
  background: var(--app-surface-soft);
  border: 1px solid rgba(23, 50, 72, 0.08);
}

.page-title {
  font-size: 32px;
  font-weight: bold;
  color: var(--app-text);
  margin: 0;
  text-align: left;
  letter-spacing: 0.2px;
}

.divider {
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, #1f7ea3, rgba(31, 126, 163, 0));
  margin: 4px 0 14px 0;
}
</style>
