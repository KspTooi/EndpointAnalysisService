<template>
  <div class="std-list-container">
    <slot></slot>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped>
.std-list-container {
  padding: 20px;
  max-width: 100%;
  overflow-x: auto;
  width: 100%;
  box-sizing: border-box;
}
</style>
