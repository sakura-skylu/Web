<template>
  <el-select
    v-model="value"
    filterable
    clearable
    placeholder="Select TCGA cancer type"
    @change="$emit('change', value)">
    <el-option
      v-for="item in options"
      :key="item.value"
      :label="item.label"
      :value="item.value" />
  </el-select>
</template>

<script>
export default {
  name: 'Select',
  data() {
    const tcga = [
      'ACC','BLCA','BRCA','CESC','CHOL','COAD','DLBC','ESCA','GBM','HNSC','KICH','KIRC','KIRP',
      'LAML','LGG','LIHC','LUAD','LUSC','MESO','OV','PAAD','PCPG','PRAD','READ','SARC','SKCM',
      'STAD','TGCT','THCA','THYM','UCEC','UCS','UVM'
    ];
    return {
      options: tcga.map((item) => ({ value: item, label: item })),
      value: ''
    };
  }
};
</script>

<style>
.el-select {
  width: 100%;
}

.el-select .el-input__inner {
  border-radius: 10px;
  border-color: rgba(23, 50, 72, 0.16);
  background: #fbfdff;
  color: #173248;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.el-select .el-input__inner:focus,
.el-select .el-input.is-focus .el-input__inner {
  border-color: #1f7ea3;
  box-shadow: 0 0 0 3px rgba(31, 126, 163, 0.16);
}
</style>
