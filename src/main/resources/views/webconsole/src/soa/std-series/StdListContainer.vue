<template>
  <div class="std-list-container">
    <slot></slot>
  </div>
</template>

<script setup lang="ts"></script>

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
</style>
