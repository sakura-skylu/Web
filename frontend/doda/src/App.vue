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
:root {
  --app-bg: #edf4f8;
  --app-bg-accent: #dce9f3;
  --app-surface: #ffffff;
  --app-surface-soft: #f7fbfe;
  --app-primary: #1f7ea3;
  --app-primary-strong: #155f81;
  --app-text: #173248;
  --app-muted: #5f7588;
  --app-border: rgba(23, 50, 72, 0.12);
  --app-shadow: 0 14px 30px rgba(23, 50, 72, 0.1);
  --app-shadow-soft: 0 8px 20px rgba(23, 50, 72, 0.08);
  --app-radius: 14px;
  --app-radius-sm: 10px;
}

* {
  box-sizing: border-box;
}

html,
body {
  margin: 0;
  min-height: 100%;
  background:
    radial-gradient(circle at 20% 0%, rgba(31, 126, 163, 0.09), transparent 36%),
    linear-gradient(180deg, var(--app-bg) 0%, var(--app-bg-accent) 100%);
}

#app {
  overflow: hidden;
  min-height: 100vh;
  font-family: 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  font-variant-numeric: tabular-nums;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: var(--app-text);
}

a {
  color: inherit;
}

.el-main {
  position: relative;
}

.el-dialog {
  border-radius: var(--app-radius-sm);
  overflow: hidden;
}
</style>
