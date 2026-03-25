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
  a {/* 去掉导航栏中 router-link 的下划线 */
    text-decoration: none;
  }
</style>

<style scoped>
  .fa {/* 强制设置图标为黑色 */
    color: black !important; 
    margin-right: 10px;
  }

  /* 统一菜单项文字样式 */
  .menu-item-text {
    font-size: 20px;
    vertical-align: middle;
  }

  .el-menu-item{
    text-align: left;
  }

  .el-aside {
    background-color: #D3DCE6;
    color: #333;
    text-align: center;
    line-height: 200px;
    height:100%;
  }
</style>