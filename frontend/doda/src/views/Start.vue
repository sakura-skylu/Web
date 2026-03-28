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
.el-main {
  box-shadow: var(--app-shadow-soft);
  border: 1px solid var(--app-border);
  border-radius: var(--app-radius);
  background-color: var(--app-surface);
  color: var(--app-text);
  text-align: start;
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
  margin: 12px 12px 0 0;
  padding: 18px 22px;
}

.el-main h2 {
  margin: 0 0 8px;
  font-size: 28px;
  color: var(--app-text);
}

.image-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 14px;
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  border-radius: var(--app-radius);
  box-shadow: var(--app-shadow-soft);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.image-wrapper:hover {
  transform: translateY(-2px);
  box-shadow: var(--app-shadow);
}

.image-caption {
  margin-top: 10px;
  font-size: 22px;
  font-weight: 700;
  color: var(--app-primary);
  text-align: center;
  height: 96px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.footer-img {
  width: 100%;
  height: auto;
  max-height: 400px;
  display: block;
  margin: auto;
  object-fit: contain;
  border-radius: 10px;
}

.intro-text {
  letter-spacing: 0.3px;
  line-height: 1.62;
  font-size: 18px;
  text-align: justify;
  height: auto;
  color: var(--app-text);
  margin: 0;
}

.el-footer {
  flex: 1;
  background-color: transparent;
  color: #333;
  text-align: center;
  padding: 12px 6px 0;
}

</style>
