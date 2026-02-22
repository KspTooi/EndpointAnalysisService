<script setup lang="ts">
import { InfoFilled } from "@element-plus/icons-vue";
</script>

<template>
  <!-- 查询持久化提示 -->
  <div class="query-persist-tip-container">
    <div class="query-persist-tip">
      <el-tooltip content="查询持久化指示器:  此页面已启用查询条件持久化，重新加载页面后会自动恢复上次的查询条件" placement="right">
        <IClarityBlockSolidAlerted class="color" />
      </el-tooltip>
    </div>
  </div>
</template>

<style scoped>
.query-persist-tip {
  position: absolute;
  top: -18px;
  left: 0;
  z-index: 10;
  cursor: pointer;
}
.query-persist-tip-container {
  position: relative;
}
.color {
  color: #0d9488;
}
</style>
