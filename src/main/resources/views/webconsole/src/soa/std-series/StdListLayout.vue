<template>
  <StdListContainer>
    <StdListAreaQuery v-if="$slots.query" :show-persist-tip="showPersistTip">
      <slot name="query"></slot>
    </StdListAreaQuery>

    <StdListAreaAction v-if="$slots.actions">
      <slot name="actions"></slot>
    </StdListAreaAction>

    <StdListAreaTable v-if="$slots.table || $slots.pagination">
      <slot name="table"></slot>
      <template #pagination v-if="$slots.pagination">
        <slot name="pagination"></slot>
      </template>
    </StdListAreaTable>
  </StdListContainer>
</template>

<script setup lang="ts">
import StdListContainer from "./StdListContainer.vue";
import StdListAreaQuery from "./StdListAreaQuery.vue";
import StdListAreaAction from "./StdListAreaAction.vue";
import StdListAreaTable from "./StdListAreaTable.vue";

defineProps<{
  showPersistTip?: boolean;
}>();
</script>

<style scoped></style>
