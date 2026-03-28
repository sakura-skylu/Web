<template>
  <el-header height="76px" class="app-header">
    <div class="brand-title"><strong>DODA</strong> · Disease Omics Data Analysis Platform</div>
    <el-tag type="info" class="header-tag">Scientific UI</el-tag>
  </el-header>
</template>

<script>
export default { name: 'Head' };
</script>

<style scoped>
.app-header {
  background: linear-gradient(110deg, #184a67 0%, #1f7ea3 52%, #1b93a9 100%);
  color: #fff;
  text-align: left;
  font-size: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 8px 22px rgba(23, 50, 72, 0.24);
  position: relative;
  z-index: 10;
}

.app-header::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(140deg, rgba(255, 255, 255, 0.22), transparent 38%);
  pointer-events: none;
}

.brand-title {
  letter-spacing: 0.3px;
  font-size: 22px;
  line-height: 1.2;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.header-tag {
  border: none;
  color: #155f81;
  background: rgba(255, 255, 255, 0.92);
  font-weight: 600;
}
</style>
