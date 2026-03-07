<template>
  <div class="w-full">
    <el-input v-model="tableName" placeholder="请输入或浏览选择数据源表名" clearable>
      <template #append>
        <el-button :disabled="!dataSourceId" @click="openBrowser">浏览</el-button>
      </template>
    </el-input>
  </div>

  <el-dialog v-model="visible" :title="title" width="600px" :close-on-click-modal="false" append-to-body>
    <div class="flex gap-2 mb-3 w-full">
      <el-input v-model="keyword" placeholder="搜索表名 / 注释" clearable />
    </div>
    <el-table
      :data="filteredList"
      v-loading="loading"
      border
      stripe
      highlight-current-row
      height="400px"
      @row-click="onRowClick"
      style="cursor: pointer"
    >
      <el-table-column prop="tableName" label="表名" min-width="160" show-overflow-tooltip />
      <el-table-column prop="tableComment" label="注释" min-width="200" show-overflow-tooltip />
    </el-table>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import type { GetDataSourceTableListVo } from "../api/DataSourceApi";
import DataSourceApi from "@/views/gen/api/DataSourceApi";
import { ElMessage, ElMessageBox } from "element-plus";

const tableName = defineModel<string>({ required: true, default: "" });

const props = defineProps<{
  dataSourceId: string;
}>();

const visible = ref(false);
const loading = ref(false);
const keyword = ref("");
const tableList = ref<GetDataSourceTableListVo[]>([]);

const title = computed(() => {
  return `数据源浏览器 - ${props.dataSourceId}`;
});

const filteredList = computed(() => {
  const kw = keyword.value.trim().toLowerCase();
  if (!kw) return tableList.value;
  return tableList.value.filter((t) => t.tableName.toLowerCase().includes(kw) || t.tableComment.toLowerCase().includes(kw));
});

const loadTables = async () => {
  loading.value = true;
  tableList.value = [];
  try {
    const result = await DataSourceApi.getDataSourceTableList({ id: props.dataSourceId });
    tableList.value = result.data;
  } catch (error: any) {
    ElMessageBox.alert(error.message, "数据源错误", { type: "error", confirmButtonText: "确定" });
    visible.value = false;
    return;
  } finally {
    loading.value = false;
  }
};

const openBrowser = async () => {
  keyword.value = "";
  visible.value = true;
  await loadTables();
};

// 数据源切换时清空已缓存的表列表
watch(
  () => props.dataSourceId,
  () => {
    tableList.value = [];
  }
);

const onRowClick = (row: GetDataSourceTableListVo) => {
  tableName.value = row.tableName;
  visible.value = false;
};
</script>

<style scoped></style>
