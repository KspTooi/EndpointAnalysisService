<template>
  <div class="h-full w-full flex">
    <RdbgCollectionTree style="width: 350px" @on-create-collection="modalCollectionCreateRef?.openModal" />
    <RdbgEditor style="flex: 1" />
  </div>
</template>

<script setup lang="ts">
import RdbgCollectionTree from "./components/RdbgCollectionTree.vue";
import RdbgEditor from "@/views/requestdebug/components/RdbgEditor.vue";
import RdbgModalCollectionCreate from "./components/RdbgModalCollectionCreate.vue";
import { ref } from "vue";

const modalCollectionCreateRef = ref<InstanceType<typeof RdbgModalCollectionCreate>>();
</script>

<style scoped>
:deep(.modal-centered) {
  margin: 0 auto;
  top: 50%;
  transform: translateY(-50%);
}

:deep(.el-overlay) {
  user-select: none;
}

:deep(.el-overlay) * {
  user-select: none;
}

.modal-content {
  padding: 20px 0;
}
</style>
