<template>
  <div class="manager-container">
    <div class="sidebar">
      <SimpleFilterSideList @selectItem="onSelectItem" />
    </div>

    <div class="content-area">
      <div class="placeholder" v-if="!filterStore.getSelectedFilterId && !filterStore.isCreating">
        <el-empty description="请选择一个过滤器查看详情" />
      </div>

      <div class="filter-details" v-if="filterStore.getSelectedFilterId || filterStore.isCreating">
        <SimpleFilterEditor />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { ElMessage } from "element-plus";
import SimpleFilterSideList from "@/components/simple-filter/SimpleFilterSideList.vue";
import SimpleFilterEditor from "@/components/simple-filter/SimpleFilterEditor.vue";
import type { GetSimpleFilterListVo } from "@/views/rdbg/api/SimpleFilterApi.ts";
import { SimpleFilterStore } from "@/store/SimpleFilterStore.ts";

const selectedItem = ref<GetSimpleFilterListVo | null>(null);
const filterStore = SimpleFilterStore();

// 监听store中的选中状态变化
const selectedFilterId = computed(() => filterStore.getSelectedFilterId);

// 当选中的过滤器ID变化时，更新selectedItem
watch(selectedFilterId, (newId) => {
  if (!newId) {
    selectedItem.value = null;
  }
  // 如果需要根据ID获取完整的过滤器信息，可以在这里调用API
});

const onSelectItem = (item: GetSimpleFilterListVo) => {
  selectedItem.value = item;
};
</script>

<style scoped>
.manager-container {
  height: 100%;
  display: flex;
  width: 100%;
}

.sidebar {
  width: 350px;
  border-right: 1px solid #e4e7ed;
  background: #fafafa;
}

.content-area {
  flex: 1;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.placeholder {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.filter-details {
  flex: 1;
  height: 100%;
  overflow: hidden;
}
</style>
