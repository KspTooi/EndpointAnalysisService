<script setup lang="ts">
import { ref } from "vue";
import FlowableDesigner from "@/views/qf/sfc_private/flowable-designer/FlowableDesigner.vue";

const xml = ref("");

function onUpdateXml(next: string): void {
  xml.value = next;
}

function onSave(next: string): void {
  xml.value = next;
}
</script>

<template>
  <div class="qf-model-deginer-page w-full h-full">
    <FlowableDesigner :initial-xml="xml" @update:xml="onUpdateXml" @save="onSave" />
  </div>
</template>

<style scoped>
.qf-model-deginer-page {
  height: calc(100vh - 96px);
  min-height: 560px;
  box-sizing: border-box;
}
</style>
