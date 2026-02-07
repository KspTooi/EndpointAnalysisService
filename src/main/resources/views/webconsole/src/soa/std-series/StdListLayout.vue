<template>
  <StdListContainer>
    <!-- 文档说明 -->
    <StdListAreaQuery v-if="$slots.query" :show-persist-tip="showPersistTip" :has-tutorial="hasTutorial">
      <template #tutorial v-if="$slots.tutorial">
        <slot name="tutorial"></slot>
      </template>
      <slot name="query"></slot>
    </StdListAreaQuery>

    <!-- 操作按钮 -->
    <StdListAreaAction v-if="$slots.actions">
      <slot name="actions"></slot>
    </StdListAreaAction>

    <!-- 表格 -->
    <StdListAreaTable v-if="$slots.table || $slots.pagination">
      <slot name="table"></slot>

      <!-- 分页 -->
      <template #pagination v-if="$slots.pagination">
        <slot name="pagination"></slot>
      </template>
    </StdListAreaTable>

    <!-- 模态框等其他不可见组件 -->
    <slot name="modal"></slot>
  </StdListContainer>
</template>

<script setup lang="ts">
import StdListContainer from "./StdListContainer.vue";
import StdListAreaQuery from "./StdListAreaQuery.vue";
import StdListAreaAction from "./StdListAreaAction.vue";
import StdListAreaTable from "./StdListAreaTable.vue";

withDefaults(
  defineProps<{
    showPersistTip?: boolean;
    hasTutorial?: boolean;
  }>(),
  {
    showPersistTip: false,
    hasTutorial: false,
  }
);
</script>

<style scoped></style>
