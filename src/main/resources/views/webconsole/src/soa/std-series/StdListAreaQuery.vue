<template>
  <div class="std-list-area-query">
    <slot></slot>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped>
.std-list-area-query {
  border-bottom: 2px dashed var(--el-border-color);
  margin-bottom: 15px;
}
</style>
