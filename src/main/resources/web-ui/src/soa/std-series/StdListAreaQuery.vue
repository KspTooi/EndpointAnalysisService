<template>
  <div class="std-list-area-query">
    <div class="std-query-indicators">
      <StdIndicatorQueryPersist v-if="showPersistTip" />
      <StdIndicatorTutorial
        v-if="hasTutorial"
        @click="showTutorialContent = !showTutorialContent"
        :active="showTutorialContent"
      />
    </div>
    <div v-if="hasTutorial && showTutorialContent" class="std-query-tutorial-content">
      <slot name="tutorial"></slot>
    </div>
    <slot></slot>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import StdIndicatorQueryPersist from "@/soa/std-series/StdIndicatorQueryPersist.vue";
import StdIndicatorTutorial from "@/soa/std-series/StdIndicatorTutorial.vue";

defineProps<{
  showPersistTip?: boolean;
  hasTutorial?: boolean;
}>();

const showTutorialContent = ref(false);
</script>

<style scoped>
.std-list-area-query {
  border-bottom: 2px dashed var(--el-border-color);
  margin-bottom: 15px;
  position: relative;
}

.std-query-indicators {
  position: absolute;
  top: -18px;
  left: 0;
  display: flex;
  gap: 8px;
  align-items: center;
  pointer-events: none;
}

.std-query-indicators > * {
  pointer-events: auto;
}

.std-query-tutorial-content {
  margin-bottom: 12px;
}
</style>
