<template>
  <div class="std-list-area-query">
    <QueryPersistTip v-if="showPersistTip" class="mt-2" />
    <slot></slot>
  </div>
</template>

<script setup lang="ts">
import QueryPersistTip from "@/components/common/QueryPersistTip.vue";

defineProps<{
  showPersistTip?: boolean;
}>();
</script>

<style scoped>
.std-list-area-query {
  border-bottom: 2px dashed var(--el-border-color);
  margin-bottom: 15px;
  position: relative;
}
</style>
