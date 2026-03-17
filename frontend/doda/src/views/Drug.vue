<template>
  <div>
    <Head />
    <el-container>
      <Menu />
      <el-main class="drug-main">
        <h1 class="page-title">Drug Sensitivity</h1>
        <div class="toolbar">
          <div class="field">
            <span>Cancer Type</span>
            <Select @change="handleCancerTypeChange" />
          </div>
          <div class="field">
            <span>Drug Name</span>
            <el-input v-model="input" clearable placeholder="支持模糊检索" @input="onKeywordInput" />
          </div>
          <el-button type="primary" @click="fetchTableData">查询</el-button>
        </div>

        <el-skeleton v-if="loading" :rows="6" animated />
        <div v-else class="table-container">
          <el-table :data="tableData" border>
            <el-table-column prop="sample_id" label="sample_id" />
            <el-table-column prop="drug_name" label="drug_name" />
            <el-table-column prop="sensitivity_score" label="sensitivity_score" />
            <el-table-column prop="cancer_type" label="cancer_type" />
            <el-table-column prop="data_source" label="data_source" />
          </el-table>
        </div>

        <el-pagination
          background
          layout="prev, pager, next, total"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          @current-change="handlePageChange" />
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
  components: { Menu, Head, Select },
  data() {
    return {
      selectedCancerType: '',
      input: '',
      tableData: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      timer: null
    };
  },
  methods: {
    handleCancerTypeChange(value) {
      this.selectedCancerType = value;
      this.currentPage = 1;
      this.fetchTableData();
    },
    onKeywordInput() {
      clearTimeout(this.timer);
      this.timer = setTimeout(() => {
        this.currentPage = 1;
        this.fetchTableData();
      }, 350);
    },
    async fetchTableData() {
      if (!this.selectedCancerType) return;
      this.loading = true;
      try {
        const response = await axios.get('http://localhost:8081/api/v1/drug-sensitivity', {
          params: {
            cancerType: this.selectedCancerType,
            drugName: this.input,
            page: this.currentPage,
            page_size: this.pageSize
          }
        });
        const payload = response.data.data || {};
        this.tableData = payload.items || [];
        this.total = payload.total || this.tableData.length;
      } catch (error) {
        this.tableData = [];
        this.total = 0;
      } finally {
        this.loading = false;
      }
    },
    handlePageChange(page) {
      this.currentPage = page;
      this.fetchTableData();
    }
  }
};
</script>

<style scoped>
.drug-main { background: #f4f7f6; }
.page-title { text-align: left; margin-top: 0; color: #2c3e50; }
.toolbar { display: grid; grid-template-columns: 280px 1fr 120px; gap: 12px; align-items: end; margin-bottom: 14px; }
.field span { display: block; text-align: left; margin-bottom: 6px; color: #2c3e50; }
.table-container { background: #fff; padding: 10px; border-radius: 10px; margin-bottom: 14px; }
</style>
