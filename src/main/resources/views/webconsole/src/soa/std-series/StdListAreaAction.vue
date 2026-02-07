<template>
  <div class="std-list-area-action">
    <slot></slot>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped>
.std-list-area-action {
  margin-bottom: 15px;
  border-top: 2px dashed var(--el-border-color);
  padding-top: 15px;
}
</style>
