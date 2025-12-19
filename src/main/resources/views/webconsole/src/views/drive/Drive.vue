<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="listForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="关键字">
              <el-input v-model="listForm.keyword" placeholder="条目名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="父级ID">
              <el-input v-model="listForm.parentId" placeholder="输入父级ID查询" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <!-- 占位，保持布局一致性 -->
          </el-col>
          <el-col :span="3" :offset="3">
            <el-form-item>
              <el-button type="primary" @click="loadList" :disabled="listLoading">查询</el-button>
              <el-button @click="resetList" :disabled="listLoading">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <!-- 条目列表 -->
    <div class="list-table">
      <el-table :data="listData" stripe v-loading="listLoading" border>
        <el-table-column
          prop="name"
          label="条目名称"
          min-width="210"
          show-overflow-tooltip
          :show-overflow-tooltip-props="{
            effect: 'dark',
            placement: 'top',
            enterable: false,
          }"
        />
        <el-table-column prop="kind" label="类型" min-width="100">
          <template #default="scope">
            {{ scope.row.kind === 0 ? "文件" : "文件夹" }}
          </template>
        </el-table-column>
        <el-table-column prop="attachSize" label="文件大小" min-width="120">
          <template #default="scope">
            {{ formatFileSize(scope.row.attachSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="attachSuffix" label="文件类型" min-width="120" />
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="listForm.pageNum"
          v-model:page-size="listForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="listTotal"
          @size-change="
            (val: number) => {
              listForm.pageSize = val;
              loadList();
            }
          "
          @current-change="
            (val: number) => {
              listForm.pageNum = val;
              loadList();
            }
          "
          background
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import DriveApi, { type GetEntryListDto, type GetEntryListVo } from "@/api/drive/DriveApi.ts";

const listForm = reactive<GetEntryListDto>({
  parentId: null,
  keyword: null,
  pageNum: 1,
  pageSize: 10,
});

const listData = ref<GetEntryListVo[]>([]);
const listTotal = ref(0);
const listLoading = ref(false);

const formatFileSize = (bytes: number): string => {
  if (!bytes || bytes === 0) {
    return "-";
  }
  if (bytes < 1024) {
    return bytes + " B";
  }
  if (bytes < 1024 * 1024) {
    return (bytes / 1024).toFixed(2) + " KB";
  }
  if (bytes < 1024 * 1024 * 1024) {
    return (bytes / (1024 * 1024)).toFixed(2) + " MB";
  }
  return (bytes / (1024 * 1024 * 1024)).toFixed(2) + " GB";
};

const loadList = async () => {
  listLoading.value = true;
  try {
    const res = await DriveApi.getEntryList(listForm);
    if (res.code === 0) {
      listData.value = res.data;
      listTotal.value = res.total;
    }
    if (res.code !== 0) {
      ElMessage.error(res.message || "加载列表失败");
    }
  } catch (error: any) {
    ElMessage.error(error.message || "加载列表失败");
  } finally {
    listLoading.value = false;
  }
};

const resetList = () => {
  listForm.pageNum = 1;
  listForm.pageSize = 10;
  listForm.keyword = null;
  listForm.parentId = null;
  loadList();
};

loadList();
</script>

<style scoped>
.list-container {
  padding: 20px;
  max-width: 100%;
  overflow-x: auto;
  width: 100%;
}

.list-table {
  margin-bottom: 20px;
  width: 100%;
  overflow-x: auto;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  width: 100%;
}

.action-buttons {
  margin-bottom: 15px;
  border-top: 2px dashed var(--el-border-color);
  padding-top: 15px;
}

:deep(.modal-centered) {
  margin: 0 auto;
  top: 50%;
  transform: translateY(-50%);
}
</style>
