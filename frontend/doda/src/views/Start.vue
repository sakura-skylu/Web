<template>
  <div>
      <Head />
      <el-container>
        <Menu />
        <el-container>
          <el-main>
            <h2 style="height: auto;">Introduction</h2>
            <p v-html="Intro" class="intro-text"></p>
          </el-main>

          <el-footer>
            <el-row justify="space-between">
              <el-col :span="8">
                <router-link to="/expression">
                <div class="image-wrapper">
                  <img :src="require('@/assets/差异表达分析火山图.png')" alt="火山图" class="footer-img" />
                  <div class="image-caption">Differential expression</div>
                </div>
                </router-link>
              </el-col>
              <el-col :span="8">
                <router-link to="/immune">
                <div class="image-wrapper">
                  <img :src="require('@/assets/immune.png')" alt="immune" class="footer-img" />
                  <div class="image-caption">Immune infiltration</div>
                </div>
                </router-link>
              </el-col>
              <el-col :span="8">
                <router-link to="/mutation">
                <div class="image-wrapper">
                  <img :src="require('@/assets/CNV棒棒糖.png')" alt="CNV棒棒糖图" class="footer-img" />
                  <div class="image-caption">CNV</div>
                </div>
                </router-link>
              </el-col>
            </el-row>
          </el-footer>

        </el-container>
      </el-container>    
  </div>
</template>

<script>
import Head from '../components/Head.vue';
import Menu from '../components/Menu.vue';

export default {
  name: 'Start',
  components: {
    Menu, Head
  },
  data: function () {
    return {
      Intro:
        "Disease Omics Data Analysis(DODA) is an integrated platform for genomic, pharmacogenomic, and immunogenomic gene set cancer analysis.<br><br>With the continuous development of bioinformatics technology, more and more advanced algorithms are being applied to predict disease-related biomolecular markers and perform a series of downstream prognostic or treatment-related analyses.<br><br>In this DODA, multiple disease-related data processing and analysis are integrated into a user-friendly web platform, so that users can easily apply existing algorithms for data analysis and clinical applications, efficiently predict disease-related biomolecules, analyze prognosis, and formulate treatment strategies, improving the efficiency and quality of disease research and clinical diagnosis and treatment."
    };
  }
};
</script>

<style scoped>

.image-wrapper {
  flex: 1; /* 填充列高度 */
  display: flex;
  flex-direction: column;
  justify-content: space-between; /* 图片在上，caption 在下 */
  padding: 10px;
}

.image-caption {
  margin-top: 8px;
  font-size: 24px;
  font-weight: 500;
  color: #30a2ff;
  text-align: center;
  height: 120px; /* 固定文字区域高度 */
}

.footer-img {
  width: 100%; /* 让图片适应列宽 */
  height: auto;
  max-height: 400px;
  /* max-width: 150px; 限制图片最大宽度 */
  display: block;
  margin: auto; /* 确保图片居中 */
  object-fit: contain; /* 保持图片比例 */
}

.intro-text {
  letter-spacing: 0.8px; /* 字间距 */
  line-height: 1.35; /* 行间距 */
  font-size: 19px; /* 调整字体大小 */
  text-align: justify; /* 让文本对齐（可选） */
  height: auto;
}

.el-main {
  box-shadow: inset 0 4px 20px rgba(0, 0, 0, 0.12); /* 阴影效果 */
  max-height: 32%;
  background-color: #ffffff;
  color: #000000;
  text-align: start;
  overflow-y: auto; /* 允许滚动，但隐藏滚动条 */
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.el-footer {
  flex: 1;
  background-color: #ffffff;
  color: #333;
  text-align: center;
  /* line-height: 100px; */
}

</style>