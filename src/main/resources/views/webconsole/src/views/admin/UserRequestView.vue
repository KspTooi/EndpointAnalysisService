<template>
  <div class="request-viewer-container">
    <RequestTree style="width: 350px;" />
    <RequestEditor style="flex: 1;" />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import RequestTree from "@/components/user-request-view/RequestTree.vue";
import RequestEditor from "@/components/user-request-view/RequestEditor.vue";


</script>

<style scoped>
.request-viewer-container {
  display: flex;
  flex-direction: row;
  flex: 1;
  overflow: hidden;
  min-height: 0;
  width: 100%;
}
</style>