<!-- 药物敏感性 -->
<template>
    <div>
        <Head />
        <el-container>
            <Menu />
            <el-main>
                <h1 class="page-title">Drug sensitivity</h1>
                <div class="divider"></div>
                <!-- 选择框 -->
                <Select @change="handleCancerTypeChange" />
                <!-- 输入框 -->
                <el-input
                    v-model="input"
                    placeholder="input drug name"
                    clearable
                    class="input"
                    @input="handleInputChange"
                >
                </el-input>
                <!-- 表格展示 -->
                <div v-if="tableData.length === 0 && selectedCancerType">
                  Loading data...
                </div>
                <el-table 
                    :data="paginatedData" 
                    class="table-container" 
                    v-else-if="tableData.length"
                    header-cell-style="{ textAlign: 'center' }">             
                    <el-table-column prop="sampleId" label="Sample ID" align="center" />
                    <el-table-column prop="drugName" label="Drug Name" align="center" />
                    <el-table-column prop="sensitivityScore" label="Sensitivity Score" align="center" />
                    <el-table-column prop="cancerType" label="Cancer Type" align="center" />
                </el-table>
                <div v-else>
                  <p v-html="Intro" class="intro-text"></p>
                </div>
                <!-- 分页组件 -->
                <div style="width: 1150px;">
                <el-pagination
                    v-if="tableData.length"
                    background
                    layout="prev, next, pager"
                    :total="tableData.length"
                    :page-size="pageSize"
                    :current-page="currentPage"
                    @current-change="handlePageChange"
                    style="width: 35px; margin-top: 20px; display: inline-flex !important; justify-content: center !important;"
                />
                </div>
            </el-main>
        </el-container>
    </div>
</template>

<script>
import Head from '../components/Head.vue';
import Menu from '../components/Menu.vue';
import Select from '../components/Select.vue';
import axios from 'axios';

export default {
    name: 'Drug',
    components: {
        Menu,
        Head,
        Select,
    },
    data() {
        return {
            selectedCancerType: '', // 初始化为空字符串
            input: '', // 输入框内容
            tableData: [], // 表格数据
            Intro:
              "Drug sensitivity refers to the responsiveness of cancer cells to specific therapeutic agents. Understanding drug sensitivity is crucial for personalized medicine, as it helps in selecting the most effective treatment for individual patients. In this DODA, drug sensitivity analysis is integrated into a user-friendly web platform, allowing users to easily visualize and interpret drug response data for their research and clinical applications.",
            currentPage: 1, // 当前页码
            pageSize: 9, // 每页显示的行数
        };
    },
    computed: {
        // 计算当前页的数据
        paginatedData() {
            const start = (this.currentPage - 1) * this.pageSize;
            const end = start + this.pageSize;
            return this.tableData.slice(start, end);
        },
    },
    methods: {
        // 处理选择框变化
        handleCancerTypeChange(value) {
            console.log('Selected Cancer Type:', value); // 调试日志
            this.selectedCancerType = value;
            this.fetchTableData(value, this.input); // 选择框变化时获取表格数据
        },
        
        // 处理输入框变化
        handleInputChange(value) {
            this.input = value;
            if (this.selectedCancerType) {
                this.fetchTableData(this.selectedCancerType, value);
            }
        },

        async fetchTableData(cancerType, drugName = '') {
            try {
                const response = await axios.get(
                    `http://localhost:8081/api/drug-predictions`,
                    // `http://114.55.32.145:8081/api/drug-predictions`,
                    {
                        params: {
                            cancerType,
                            drugName
                        }
                    }
                );
                console.log('Fetched data:', response.data); // 调试日志
                this.tableData = response.data; // 使用扩展运算符创建新数组
                this.currentPage = 1; // 重置到第一页
                console.log('Table data length:', this.tableData.length); // 确认赋值是否正确
            } catch (error) {
                console.error('Failed to fetch table data:', error);
                this.tableData = []; // 如果请求失败，确保表格数据为空
            }
        },

        // 处理分页变化
        handlePageChange(page) {
            this.currentPage = page;
        },
    },
};
</script>

<style scoped>
.el-main{
  overflow: hidden;
  padding-bottom: 20px;;
}

.el-input{
  height: 30px; /* 设置输入框宽度 */
  margin-bottom: 20px; /* 设置输入框与表格之间的间距 */
}

.intro-text {
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.12); /* 阴影效果 */
  border: 2px solid #ffffff;
  border-radius: 10px;
  color: #333333; /* 字体颜色 */
  padding: 10px;
    letter-spacing: 0.5px; /* 字间距 */
    line-height: 1.7; /* 行间距 */
    font-size: 24px; /* 调整字体大小 */
    text-align: justify; /* 让文本对齐（可选） */
    text-indent: 2em; /* 首行缩进2字符 */
    justify-content: center; /* 水平居中（可选，根据需求） */
    height: auto; /* 确保容器高度 */
}
</style>

<style>
.el-input .el-input__icon.el-icon-circle-close {
  color: #ff0000; /* 设置清除按钮的颜色为红色 */
  font-size: 24px!important; /* 调整清除按钮的大小 */
  cursor: pointer; /* 鼠标悬停时显示为手型 */
}
.el-table__header-wrapper colgroup col {
  display: none;
}

/* 调整分页箭头按钮的样式 */
.el-pagination .btn-next .el-icon, 
.el-pagination .btn-prev .el-icon{
    display: flex !important;
    font-size: 14px !important;
    font-weight: 1700;
    align-items: center;
    justify-content: center;
}

.el-pagination.is-background .btn-next, 
.el-pagination.is-background .btn-prev, 
.el-pagination.is-background .el-pager li {
    margin: 0 5px;
    background-color: #d4d4d7;
    color: #1d1d1e;
    min-width: 30px;
    border-radius: 2px;
    font-size: 14px !important;
}

.table-container {
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.12); /* 阴影效果 */
  border: 2px solid #ffffff;
  /* border-radius: 10px; */
  height: auto;
  overflow-y: hidden; /* 启用垂直滚动条 */
}

.el-table__header-wrapper {
    height: auto;
}
/* 表头样式 */
.el-table th {
  color: #000000; /* 表头字体颜色 */
  font-weight: bold; /* 表头字体加粗 */
  font-size: 20px;
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