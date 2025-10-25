<template>
  <div class="container">
    <div class="filter-search">
      <div class="more-query-toggle" @click="showMoreQuery = !showMoreQuery">
        <IMdiLightChevronDoubleUp v-show="!showMoreQuery" class="toggle-icon" />
        <IMdiLightChevronDoubleDown v-show="showMoreQuery" class="toggle-icon" />
      </div>
      <el-input v-model="searchForm.name" placeholder="输入任意字符查询" size="small" clearable @keyup.enter="loadData" />
      <el-button type="primary" @click="loadData(true)" size="small" :loading="loading">加载</el-button>
      <el-button type="primary" @click="handleAddFilter" size="small">新过滤器</el-button>
    </div>

    <div class="filter-list">
      <div class="filter-controls" v-show="showMoreQuery">
        <el-select v-model="searchForm.direction" placeholder="过滤器方向" size="small" clearable>
          <el-option label="请求过滤器" :value="0" />
          <el-option label="响应过滤器" :value="1" />
        </el-select>
        <el-select v-model="searchForm.status" placeholder="状态" size="small" clearable>
          <el-option label="启用" :value="0" />
          <el-option label="禁用" :value="1" />
        </el-select>
      </div>

      <div class="list-content" v-loading="loading" @scroll="handleScroll" ref="listContentRef">
        <SimpleFilterSideItem v-for="item in filterList" :key="item.id" :item="item" @click="handleItemClick(item)" @delete="handleDelete(item)" />

        <div class="empty-state" v-if="!loading && filterList.length === 0">
          <el-empty description="暂无数据" />
        </div>

        <div class="loading-more" v-if="isLoadingMore">
          <el-icon class="is-loading">
            <Loading />
          </el-icon>
          <span>加载中...</span>
        </div>

        <div class="no-more" v-if="!hasMore && filterList.length > 0">
          <span>没有更多数据了</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, nextTick, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Loading } from "@element-plus/icons-vue";
import SimpleFilterApi, { type GetSimpleFilterListDto, type GetSimpleFilterListVo } from "@/api/requestdebug/SimpleFilterApi.ts";
import SimpleFilterSideItem from "./SimpleFilterSideItem.vue";
import { SimpleFilterStore } from "@/store/SimpleFilterStore";

//显示更多查询条件
const showMoreQuery = ref(false);

const loading = ref(false);
const filterList = ref<GetSimpleFilterListVo[]>([]);
const filterStore = SimpleFilterStore();

const searchForm = reactive<GetSimpleFilterListDto>({
  name: null,
  direction: null,
  status: null,
  pageNum: 1,
  pageSize: 10,
});

const hasMore = ref(true);
const isLoadingMore = ref(false);
const listContentRef = ref<HTMLElement>();

const emit = defineEmits<{
  selectItem: [item: GetSimpleFilterListVo];
  addFilter: [];
}>();

const loadData = async (isReset = true) => {
  if (loading.value || isLoadingMore.value) {
    return;
  }

  if (isReset) {
    loading.value = true;
    searchForm.pageNum = 1;
    hasMore.value = true;
  }

  if (!isReset && !hasMore.value) {
    return;
  }

  if (!isReset) {
    isLoadingMore.value = true;
  }

  try {
    const result = await SimpleFilterApi.getSimpleFilterList(searchForm);
    const newData = result.data || [];

    if (isReset) {
      filterList.value = newData;
    }
    if (!isReset) {
      filterList.value = [...filterList.value, ...newData];
    }

    hasMore.value = newData.length === searchForm.pageSize;

    if (!isReset) {
      searchForm.pageNum++;
    }
  } catch (error) {
    ElMessage.error("加载数据失败");
    console.error("加载过滤器列表失败:", error);
  } finally {
    if (isReset) {
      loading.value = false;
    }
    if (!isReset) {
      isLoadingMore.value = false;
    }
  }
};

const handleItemClick = (item: GetSimpleFilterListVo) => {
  filterStore.setSelectedFilterId(item.id);
  emit("selectItem", item);
};

const handleAddFilter = () => {
  emit("addFilter");
  filterStore.setIsCreating(true);
  ElMessage.primary("开始创建新基本过滤器,请在编辑器中进行编辑");
};

const handleDelete = async (item: GetSimpleFilterListVo) => {
  try {
    await ElMessageBox.confirm(`确定要删除过滤器 "${item.name}" 吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await SimpleFilterApi.removeSimpleFilter({ id: item.id });

    //如果删除的是当前选中的过滤器，则清空选中
    if (filterStore.getSelectedFilterId === item.id) {
      filterStore.setSelectedFilterId(null);
    }

    ElMessage.success("删除成功");
    await loadData();
  } catch {
    // 用户取消删除
  }
};

const handleScroll = (event: Event) => {
  if (!hasMore.value || isLoadingMore.value) {
    return;
  }

  const target = event.target as HTMLElement;
  if (!target) {
    return;
  }

  const { scrollTop, scrollHeight, clientHeight } = target;
  const scrollPercent = scrollTop / (scrollHeight - clientHeight);

  if (scrollPercent >= 0.5) {
    loadData(false);
  }
};

onMounted(async () => {
  await loadData();

  await nextTick();

  if (listContentRef.value) {
    listContentRef.value.addEventListener("scroll", handleScroll);
  }
});

onUnmounted(() => {
  if (listContentRef.value) {
    listContentRef.value.removeEventListener("scroll", handleScroll);
  }
});

//退出编辑器时，重新加载数据
watch(
  () => filterStore.isCreating,
  (isCreating) => {
    if (!isCreating) {
      loadData();
    }
  },
  { immediate: true }
);

//监听是否需要重新加载数据
watch(
  () => filterStore.isNeedReloadList,
  (isNeedReloadList) => {
    if (isNeedReloadList) {
      loadData();
    }
  },
  { immediate: true }
);
</script>

<style scoped>
.container {
  height: 100%;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);

  position: relative;
  display: flex;
  flex-direction: column;
}

.filter-search {
  padding: 10px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-around;
  gap: 5px;
  position: sticky;
  top: 0;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  z-index: 1000;
  border-bottom: 1px solid #e0e0e0;
}

.filter-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.filter-controls {
  padding: 10px;
  display: flex;
  gap: 10px;
  border-bottom: 1px solid #e0e0e0;
}

.list-content {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.list-content::-webkit-scrollbar {
  width: 4px;
}

.list-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 2px;
}

.list-content::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 2px;
}

.list-content::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.loading-more {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  gap: 8px;
  color: #606266;
  font-size: 14px;
}

.no-more {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  color: #909399;
  font-size: 12px;
}

.el-button + .el-button {
  margin-left: 0;
}

.el-select {
  min-width: 120px;
}

.more-query-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  background: rgba(64, 158, 255, 0.1);
  border: 1px solid rgba(64, 158, 255, 0.2);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;
}

.more-query-toggle:hover {
  background: rgba(64, 158, 255, 0.15);
  border-color: rgba(64, 158, 255, 0.3);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.25);
}

.more-query-toggle:active {
  transform: translateY(0);
  box-shadow: 0 1px 4px rgba(64, 158, 255, 0.2);
}

.toggle-icon {
  width: 24px;
  height: 24px;
  color: #409eff;
  transition: all 0.2s ease;
}
</style>
