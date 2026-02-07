<template>
  <div class="std-list-area-table">
    <slot></slot>
    <div v-if="$slots.pagination" class="std-pagination-container">
      <slot name="pagination"></slot>
    </div>
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

.std-pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 15px;
  padding-bottom: 15px;
  flex-shrink: 0;
}
</style>
