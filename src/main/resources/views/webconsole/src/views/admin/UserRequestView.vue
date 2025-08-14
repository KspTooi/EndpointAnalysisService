<template>
  <div class="request-viewer-container">
    <RequestTree style="width: 350px;" @select-request="handleSelectRequest" />
    <RequestBuilder :request-id="requestId" style="width: 100%" v-if="requestId"/>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import RequestTree from "@/components/user-request-view/RequestTree.vue";
import RequestBuilder from "@/components/user-request-view/RequestBuilder.vue";

const requestId = ref<string | null>(null)

const handleSelectRequest = (id: string) => {
  requestId.value = id
}

</script>

<style scoped>
.request-viewer-container {
  display: flex;
  flex-direction: row;
  height: 100%;
}
</style>