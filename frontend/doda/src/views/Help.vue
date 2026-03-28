<template>
    <div>
        <Head />
        <el-container>
            <Menu />
            <el-main>
                <div class="text-container">
                  <h2 style="height: auto; text-align: start;">Introduction</h2>
                  <p v-html="Intro" class="text-style"></p>
                </div>
                <div class="text-container">
                  <h2 style="height: auto; text-align: start;">Expression</h2>
                  <p v-html="Expression" class="text-style"></p>
                </div>
                <div class="text-container">
                  <h2 style="height: auto; text-align: start;">Mutation</h2>
                  <p v-html="Mutation" class="text-style"></p>
                </div>
                <div class="text-container">
                  <h2 style="height: auto; text-align: start;">Immune</h2>
                  <p v-html="Immune" class="text-style"></p>
                </div>
                <div class="text-container">
                  <h2 style="height: auto; text-align: start;">Drug</h2>
                  <p v-html="Drug" class="text-style"></p>
                </div>
            </el-main>
        </el-container>        
    </div>
</template>

<script>
import Head from '../components/Head.vue';
import Menu from '../components/Menu.vue';

export default{
    name:'Help',
    components:{
        Menu, Head
    },
    data() {
        return {
            Intro: "Disease Omics Data Analysis(DODA) is an integrated platform for genomic, pharmacogenomic, and immunogenomic gene set cancer analysis. In this DODA, multiple disease-related data processing and analysis are integrated into a user-friendly web platform, so that users can easily apply existing algorithms for data analysis and clinical applications, efficiently predict disease-related biomolecules, analyze prognosis, and formulate treatment strategies, improving the efficiency and quality of disease research and clinical diagnosis and treatment.<br>The platform dynamically loads the analysis results of different cancer types through the selection box, combined with graphs and charts to visualize the data and help users quickly understand the relationship between gene expression and cancer.<br>All cancer sample data were obtained from the GDC TCGA database, including RNA-seq, CNV data, clinical data, etc.",
            Expression: "The Expression page shows the results of cancer-related gene expression analysis, including the following analysis methods and their functions:<br>1. DE Volcano<br>Uses a standardized method to process expression data, combined with the difference analysis tool limma to calculate Fold Change and P-value. shows the distribution of differentially expressed genes, helping users quickly identify significantly different genes.  <br>2. Cox Risk<br>Calculates risk coefficients using Cox regression models to screen for significant genes. Evaluates the relationship between gene expression and patient survival time and demonstrates the risk coefficient of a gene.  <br>3. Survival Curve<br>Compare the difference in survival between patients in the high and low expression groups to assess the effect of genes on prognosis.  <br>4. Calibration Curve<br>Construct a column-line graphical model, validate and plot the calibration curve using the Bootstrap method. Evaluate the accuracy of the predictive model and compare how well the model predictions match the actual values.  <br>5. GSVA Score<br>Calculate gene set activity scores using the GSVA algorithm to demonstrate differences between subgroups. Evaluate the activity level of a gene set across samples to reveal changes in functional pathways.",
            Mutation:"The Mutation page is mainly used to display the results of analyzing Copy Number Variation (CNV), which refers to the gain or loss of DNA fragments in the genome, and may lead to changes in gene dosage and expression levels, which are closely related to the occurrence and development of many diseases (including cancer).<br>In this page, users can dynamically load the corresponding CNV data by selecting different cancer types and display them in a visualized form. The page provides intuitive graphs (CNV lollipop charts) to help users quickly understand the distribution and impact of genomic variants.",
            Immune:"The Immune page shows the results of analysis of the level of immune cell infiltration. Immune cell infiltration is the extent to which immune cells enter the tissue or tumor microenvironment, which is important in disease progression, prognosis, and response to therapy. Immune cells (e.g., T-cells, macrophages, and B-cells) are key elements of the body's defense against infection and disease, recognizing and destroying pathogens and aberrant cells (e.g., cancer cells).<br>In this page, users can dynamically load the corresponding immune cell infiltration data by selecting different cancer types and presenting them in graphical form. The page analyzes the infiltration levels of 6 different human immune cells in high and low risk groups, calculated using the CIBERSORT algorithm.",
            Drug:"The Drug page is primarily used to present the results of drug sensitivity analysis. Drug sensitivity refers to the degree of response of cancer cells to a specific therapeutic drug. Understanding drug sensitivity is crucial for personalized medicine and helps to select the most effective treatment plan for patients.<br>In this page, users can dynamically load the corresponding drug sensitivity data by selecting different cancer types and display them in a table. The table contains information such as sample ID, drug name, sensitivity score and cancer type, which helps users to visualize and analyze the effect of drugs on different cancer types."        
        }
    }
}
</script>

<style scoped>
.text-container {
  box-shadow: var(--app-shadow-soft);
  border: 1px solid var(--app-border);
  border-radius: var(--app-radius);
  background: var(--app-surface);
  color: var(--app-text);
  padding: 14px;
  margin-bottom: 20px;
  height: auto;
}

.text-container h2 {
  margin: 0 0 8px;
  color: var(--app-primary-strong);
  font-size: 28px;
}

.text-style {
  margin: 0;
  letter-spacing: 0.25px;
  line-height: 1.66;
  font-size: 18px;
  text-align: justify;
  justify-content: center;
}

.el-main {
  max-height: 90vh;
  overflow-y: auto;
}
</style>
