<template>
  <el-select v-model="value" clearable placeholder="select cancer type" 
  @change="$emit('change', value)" style="size: mini;">
    <el-option
      v-for="item in options"
      :key="item.value"
      :label="item.label"
      :value="item.value">
    </el-option>
  </el-select>
</template>

<script>
export default {
  name: 'Select',
  data() {
    return {
      options: [
        { value: 'BLCA', label: 'BLCA' },
        { value: 'CHOL', label: 'CHOL' },
        { value: 'ESCA', label: 'ESCA' },
        { value: 'KICH', label: 'KICH' },
        { value: 'LIHC', label: 'LIHC' },
        { value: 'PAAD', label: 'PAAD' },
        { value: 'PCPG', label: 'PCPG' },
        { value: 'READ', label: 'READ' },
        { value: 'SARC', label: 'SARC' },
        { value: 'THYM', label: 'THYM' }
      ],
      value: ''
    };
  }
};
</script>

<style>
.el-select{
  height: 50px;
}
/* 自定义清除按钮样式 */
.el-select .el-input__icon.el-icon-circle-close {
  color: #ff0000; /* 设置清除按钮的颜色为红色 */
  font-size: 24px!important; /* 调整清除按钮的大小 */
  cursor: pointer; /* 鼠标悬停时显示为手型 */
}

.el-select .el-input__icon.el-icon-circle-close:hover {
  color: #ff5555; /* 鼠标悬停时改变清除按钮颜色 */
}
.el-input__inner::placeholder {
  color: #9e9e9e !important; /* 设置占位符字体颜色 */
  font-size: 18px; /* 设置占位符字体大小 */
  font-style: italic; /* 设置占位符字体样式（斜体） */
  opacity: 1; /* 确保占位符完全可见 */
}
/* 设置下拉框的宽度 */
.el-select-dropdown {
  width: 200px !important; /* 设置下拉框宽度 */
  max-height: 200px; /* 限制下拉框的最大高度 */
  overflow-y: auto; /* 如果选项过多，允许滚动 */
  overflow-x: hidden; /* 禁止水平滚动 */
  font-size: 16px; /* 设置下拉框字体大小 */
  line-height: 30px; /* 设置下拉框行高，确保选项垂直居中 */
  background-color: #ffffff; /* 设置下拉框背景颜色 */
}

/* 设置下拉选项的高度和字体大小 */
.el-option {
  font-size: 18px; /* 缩小字体大小 */
  height: 50px; /* 设置选项高度 */
  line-height: 30px; /* 垂直居中选项内容 */
}
</style>