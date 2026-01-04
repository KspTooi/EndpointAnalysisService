<template>
  <div class="h-full w-full flex">
    <RdbgCollectionTree style="width: 350px" :data="listData" :loading="listLoading" :loadList="loadList" />

    <RdbgEditor style="flex: 1" />
  </div>
</template>

<script setup lang="ts">
import RdbgCollectionTree from "./components/RdbgCollectionTree.vue";
import RdbgEditor from "@/views/requestdebug/components/RdbgEditor.vue";
import RdbgModalCollectionCreate from "./components/RdbgModalCollectionCreate.vue";
import { computed, ref } from "vue";
import { useRdbgStore } from "@/views/requestdebug/service/RdbgStore";
import RdbgWorkSpaceService from "./service/RdbgWorkSpaceService";

//列表功能打包
const { listData, listTotal, listFilter, listLoading, loadList } = RdbgWorkSpaceService.useCollectionList();

const rdbgStore = useRdbgStore();
const isRequestSelected = computed(() => {});
const isCollectionSelected = computed(() => {});
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
