<template>
  <div id="app" class="overall">
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'App',
}
</script>

<style>
#app {
  overflow: hidden;
  font-family: Inter, Roboto, 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
  font-variant-numeric: tabular-nums;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
</style>
