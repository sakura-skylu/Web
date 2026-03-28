<template>
    <el-aside width="200px">
        <el-row class="tac">
            <el-col>
    <el-menu
      :default-active="$route.path"
      class="el-menu-vertical-demo"
      @open="handleOpen"
      @close="handleClose"
      background-color="#D3DCE6"
      text-color="#333"
      active-text-color="#409EFF">
      <router-link to="/">
        <el-menu-item index="/">
          <span class="menu-item-text"><i class="fa fa-home fa-lg fa-fw"></i>Home</span>
        </el-menu-item>
      </router-link>

      <router-link to="/analysis">
        <el-menu-item index="/analysis">
          <span slot="title" class="menu-item-text"><i class="fa fa-tasks fa-lg fa-fw"></i>Analysis</span>
        </el-menu-item>
      </router-link>

      <router-link to="/expression">
        <el-menu-item index="/expression">
          <span slot="title" class="menu-item-text"><i class="fa fa-bar-chart fa-lg fa-fw"></i>Expression</span>
        </el-menu-item>
      </router-link>

      <router-link to="/mutation">
        <el-menu-item index="/mutation">
          <span slot="title" class="menu-item-text"><i class="fa fa-th fa-lg fa-fw"></i>Mutation</span>
        </el-menu-item>
      </router-link>

      <router-link to="/immune">
        <el-menu-item index="/immune">
          <span slot="title" class="menu-item-text"><i class="fa fa-heartbeat fa-lg fa-fw"></i>Immune</span>
        </el-menu-item>
      </router-link>

      <router-link to="/drug">
        <el-menu-item index="/drug">
          <span slot="title" class="menu-item-text"><i class="fa fa-medkit fa-lg fa-fw"></i>Drug</span>
        </el-menu-item>
      </router-link>

      <router-link to="/help">
      <el-menu-item index="/help">
        <span slot="title" class="menu-item-text"><i class="fa fa-question-circle-o fa-lg fa-fw"></i>Help</span>
      </el-menu-item>
      </router-link>
    </el-menu>
            </el-col>
        </el-row>
    </el-aside>
</template>

<script>
export default{
    name:'Menu',

    methods: {
      handleOpen(key, keyPath) {
        console.log(key, keyPath);
      },
      handleClose(key, keyPath) {
        console.log(key, keyPath);
      }
    }
}
</script>

<style>
a {
  text-decoration: none;
  color: inherit;
}
</style>

<style scoped>
.fa {
  color: #2e475f !important;
  margin-right: 10px;
}

.menu-item-text {
  font-size: 18px;
  vertical-align: middle;
  font-weight: 600;
}

.el-menu-item {
  text-align: left;
}

.el-aside {
  background: linear-gradient(180deg, #e6eff5 0%, #d4e3ee 100%);
  color: #2e475f;
  text-align: center;
  line-height: 200px;
  height: 100%;
  border-right: 1px solid rgba(23, 50, 72, 0.08);
}

.tac {
  height: 100%;
}

::v-deep .el-menu {
  border-right: none;
  background: transparent !important;
  padding: 10px 8px;
}

::v-deep .el-menu-item {
  margin: 6px 0;
  border-radius: 10px;
  height: 48px;
  line-height: 48px;
  transition: all 0.18s ease;
}

::v-deep .el-menu-item:hover {
  background-color: rgba(31, 126, 163, 0.12) !important;
}

::v-deep .el-menu-item.is-active {
  background: linear-gradient(90deg, rgba(31, 126, 163, 0.22), rgba(31, 126, 163, 0.08)) !important;
  color: #1b5f81 !important;
  box-shadow: inset 3px 0 0 #1f7ea3;
}

::v-deep .el-menu-item.is-active .fa {
  color: #1b5f81 !important;
}
</style>
