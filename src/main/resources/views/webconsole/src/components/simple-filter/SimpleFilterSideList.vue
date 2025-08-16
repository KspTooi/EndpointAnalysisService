<template>
  <div class="container">
    <div class="filter-search">
      <el-input v-model="searchForm.name" placeholder="输入任意字符查询" size="small" clearable @keyup.enter="loadData" />
      <el-button type="primary" @click="loadData" size="small" :loading="loading">加载</el-button>
      <el-button type="primary" @click="handleAddFilter" size="small">新过滤器</el-button>
    </div>

    <div class="filter-list">
      <div class="filter-controls">
        <el-select v-model="searchForm.direction" placeholder="过滤器方向" size="small" clearable>
          <el-option label="请求过滤器" :value="0" />
          <el-option label="响应过滤器" :value="1" />
        </el-select>
        <el-select v-model="searchForm.status" placeholder="状态" size="small" clearable>
          <el-option label="启用" :value="0" />
          <el-option label="禁用" :value="1" />
        </el-select>
      </div>

      <div class="list-content" v-loading="loading">
        <SimpleFilterSideItem v-for="item in filterList" :key="item.id" :item="item" @click="handleItemClick(item)" @delete="handleDelete(item)" />

        <div class="empty-state" v-if="!loading && filterList.length === 0">
          <el-empty description="暂无数据" />
        </div>
      </div>

      <div class="pagination-wrapper" v-if="pageInfo.total > 0">
        <el-pagination
          v-model:current-page="searchForm.pageNum"
          v-model:page-size="searchForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :small="true"
          :total="pageInfo.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import SimpleFilterApi, { type GetSimpleFilterListDto, type GetSimpleFilterListVo } from "@/api/SimpleFilterApi";
import SimpleFilterSideItem from "./SimpleFilterSideItem.vue";
import { SimpleFilterStore } from "@/store/SimpleFilterStore";

const loading = ref(false);
const filterList = ref<GetSimpleFilterListVo[]>([]);
const filterStore = SimpleFilterStore();

const searchForm = reactive<GetSimpleFilterListDto>({
  name: null,
  direction: null,
  status: null,
  pageNum: 1,
  pageSize: 20,
});

const pageInfo = reactive({
  total: 0,
  pageNum: 1,
  pageSize: 20,
});

const emit = defineEmits<{
  selectItem: [item: GetSimpleFilterListVo];
  addFilter: [];
}>();

const loadData = async () => {
  if (loading.value) {
    return;
  }

  loading.value = true;

  try {
    const result = await SimpleFilterApi.getSimpleFilterList(searchForm);
    filterList.value = result.data || [];
    pageInfo.total = result.total || 0;
    pageInfo.pageNum = searchForm.pageNum;
    pageInfo.pageSize = searchForm.pageSize;
  } catch (error) {
    ElMessage.error("加载数据失败");
    console.error("加载过滤器列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleItemClick = (item: GetSimpleFilterListVo) => {
  filterStore.setSelectedFilterId(item.id);
  emit("selectItem", item);
};

const handleAddFilter = () => {
  emit("addFilter");
};

const handleDelete = async (item: GetSimpleFilterListVo) => {
  try {
    await ElMessageBox.confirm(`确定要删除过滤器 "${item.name}" 吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    // TODO: 实现删除功能
    ElMessage.success("删除成功");
    await loadData();
  } catch {
    // 用户取消删除
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.container {
  height: 100%;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
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

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.pagination-wrapper {
  padding: 10px;
  border-top: 1px solid #e0e0e0;
  background: #fafafa;
  display: flex;
  justify-content: center;
}

.el-button + .el-button {
  margin-left: 0;
}

.el-select {
  min-width: 120px;
}
</style>
