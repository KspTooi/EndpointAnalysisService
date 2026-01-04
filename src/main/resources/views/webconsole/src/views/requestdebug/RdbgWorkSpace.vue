<template>
  <div class="h-full w-full flex">
    <RdbgCollectionTree style="width: 350px" :data="listData" :loading="listLoading" :loadList="loadList" />

    <RdbgEditor
      style="flex: 1"
      v-show="isSelectedRequest"
      :details="details"
      :loading="detailsLoading"
      @on-details-change="saveDetails"
    />
  </div>
</template>

<script setup lang="ts">
import RdbgCollectionTree from "./components/RdbgCollectionTree.vue";
import RdbgEditor from "@/views/requestdebug/components/RdbgEditor.vue";
import { computed, watch } from "vue";
import { useRdbgStore } from "@/views/requestdebug/service/RdbgStore";
import RdbgWorkSpaceService from "@/views/requestdebug/service/RdbgWorkSpaceService";

const rdbgStore = useRdbgStore();

//列表功能打包
const { listData, listLoading, loadList } = RdbgWorkSpaceService.useCollectionList();

//集合详情功能打包
const { details, detailsLoading, saveDetails } = RdbgWorkSpaceService.useCollectionDetails(loadList);

const isSelectedRequest = computed(() => {
  return rdbgStore.getActiveCollection?.kind === 0;
});
const isSelectedGroup = computed(() => {
  return rdbgStore.getActiveCollection?.kind === 1;
});
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
