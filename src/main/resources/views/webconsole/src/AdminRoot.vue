<template>
  <router-view v-if="isBlankLayout" />
  <console-framework v-if="!isBlankLayout" />
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRoute } from "vue-router";
import ConsoleFramework from "@/soa/console-framework/ConsoleFramework.vue";

const route = useRoute();

const isBlankLayout = computed(() => {
  if (route.meta?.layout === "blank") {
    return true;
  }
  return false;
});
</script>

<style>
/* 全局样式，禁止页面滚动 */
html,
body,
#app {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
  overflow: hidden;
}

/* 按钮内图标样式 */
.icon-in-button {
  margin-right: 4px;
}
</style>
