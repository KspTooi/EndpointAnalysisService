<script setup lang="ts"></script>

<template>
  <!-- 查询持久化提示 -->
  <div class="std-indicator">
    <el-tooltip
      content="查询持久化指示器:  此页面已启用查询条件持久化，重新加载页面后会自动恢复上次的查询条件"
      placement="left"
    >
      <IClarityBlockSolidAlerted class="color" />
    </el-tooltip>
  </div>
</template>

<style scoped>
.std-indicator {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.color {
  color: #0d9488;
}
</style>
