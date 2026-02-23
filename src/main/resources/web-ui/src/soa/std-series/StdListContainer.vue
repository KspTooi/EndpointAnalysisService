<template>
  <div class="std-list-container" :class="{ 'no-padding': nopadding }">
    <slot></slot>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  nopadding?: boolean
}>()
</script>

<style scoped>
.std-list-container {
  padding: 20px;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.std-list-container.no-padding {
  padding: 0;
}
</style>
