<template>
  <div class="manager-container">
    <div class="sidebar">
      <SimpleFilterSideList @selectItem="handleSelectItem" @addFilter="handleAddFilter" />
    </div>

    <div class="content-area">
      <div class="placeholder" v-if="!selectedItem">
        <el-empty description="请选择一个过滤器查看详情" />
      </div>

      <div class="filter-details" v-if="selectedItem">
        <h3>{{ selectedItem.name }}</h3>
        <p>过滤器详情将在这里显示...</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { ElMessage } from "element-plus";
import SimpleFilterSideList from "@/components/simple-filter/SimpleFilterSideList.vue";
import type { GetSimpleFilterListVo } from "@/api/SimpleFilterApi";
import { SimpleFilterStore } from "@/store/SimpleFilterStore";

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

const handleSelectItem = (item: GetSimpleFilterListVo) => {
  selectedItem.value = item;
};

const handleAddFilter = () => {
  ElMessage.info("新增过滤器功能开发中...");
};
</script>

<style scoped>
.manager-container {
  height: 100%;
  display: flex;
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
}

.placeholder {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.filter-details {
  padding: 20px;
  flex: 1;
}
</style>
