<template>
  <div class="std-list-area-table">
    <slot></slot>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped>
.std-list-area-table {
  flex: 1;
  width: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}

.std-list-area-table :deep(.el-table) {
  flex: 1;
  overflow: hidden;
}
</style>
