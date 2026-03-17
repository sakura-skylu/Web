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
</style>
