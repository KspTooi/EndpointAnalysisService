<template>
  <StdListContainer>
    <StdListAreaQuery v-if="$slots.query">
      <slot name="query"></slot>
    </StdListAreaQuery>

    <StdListAreaAction v-if="$slots.actions">
      <slot name="actions"></slot>
    </StdListAreaAction>

    <StdListAreaTable v-if="$slots.table">
      <slot name="table"></slot>
    </StdListAreaTable>
  </StdListContainer>
</template>

<script setup lang="ts">
import StdListContainer from "./StdListContainer.vue";
import StdListAreaQuery from "./StdListAreaQuery.vue";
import StdListAreaAction from "./StdListAreaAction.vue";
import StdListAreaTable from "./StdListAreaTable.vue";
</script>

<style scoped></style>
