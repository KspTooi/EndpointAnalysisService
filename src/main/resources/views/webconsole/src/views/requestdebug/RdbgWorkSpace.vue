<template>
  <div class="h-full w-full flex">
    <RdbgCollectionTree style="width: 350px" :data="listData" :loading="listLoading" :loadList="loadList" />

    <RdbgEditor style="flex: 1" v-show="isSelectedRequest" />
  </div>
</template>

<script setup lang="ts">
import RdbgCollectionTree from "./components/RdbgCollectionTree.vue";
import RdbgEditor from "@/views/requestdebug/components/RdbgEditor.vue";
import { computed } from "vue";
import { useRdbgStore } from "@/views/requestdebug/service/RdbgStore";
import RdbgWorkSpaceService from "./service/RdbgWorkSpaceService";

//列表功能打包
const { listData, listLoading, loadList } = RdbgWorkSpaceService.useCollectionList();

const rdbgStore = useRdbgStore();

const isSelectedRequest = computed(() => {
  //最后一个被选中的节点是否是请求
  return rdbgStore.getSelectedCollections[rdbgStore.getSelectedCollections.length - 1]?.kind === 0;
});
const isSelectedGroup = computed(() => {
  //最后一个被选中的节点是否是分组
  return rdbgStore.getSelectedCollections[rdbgStore.getSelectedCollections.length - 1]?.kind === 1;
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
