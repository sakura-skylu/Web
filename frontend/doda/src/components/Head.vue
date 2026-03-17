<template>
  <el-header height="76px" class="app-header">
    <div><strong>DODA</strong> · Disease Omics Data Analysis Platform</div>
    <el-tag type="info">Scientific UI</el-tag>
  </el-header>
</template>

<script>
export default { name: 'Head' };
</script>

<style scoped>
.app-header {
  background: #2c3e50;
  color: #fff;
  text-align: left;
  line-height: 76px;
  font-size: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
