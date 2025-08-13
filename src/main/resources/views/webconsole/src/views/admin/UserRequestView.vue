<template>
  <div class="request-viewer-container">
    <RequestTree style="width: 350px;" @select-request="handleSelectRequest" />
  </div>
</template>

<script setup lang="ts">

import RequestTree from "@/components/user-request-view/RequestTree.vue";

const handleSelectRequest = (requestId: string) => {
  console.log(requestId);
}

</script>

<style scoped>
.request-viewer-container {
  display: flex;
  flex-direction: row;
  height: 100%;
}
</style>