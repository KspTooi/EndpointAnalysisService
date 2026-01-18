<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="listForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="用户名">
              <el-input v-model="listForm.userName" placeholder="输入用户名查询" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <!-- 占位，保持布局一致性 -->
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

    <div class="action-buttons"></div>

    <!-- 会话列表 -->
    <div class="list-table">
      <el-table :data="listData" stripe v-loading="listLoading" border>
        <el-table-column prop="id" label="会话ID" min-width="100" />
        <el-table-column prop="username" label="用户名" min-width="150" />
        <el-table-column prop="createTime" label="登入时间" min-width="180" />
        <el-table-column prop="expiresAt" label="过期时间" min-width="180" />
        <el-table-column label="操作" fixed="right" min-width="180">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openModal(scope.row)" :icon="ViewIcon"> 详情 </el-button>
            <el-button link type="danger" size="small" @click="handleCloseSession(scope.row)" :icon="CloseIcon"> 关闭会话 </el-button>
          </template>
        </el-table-column>
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
            () => {
              loadList();
            }
          "
          @current-change="
            () => {
              loadList();
            }
          "
          background
        />
      </div>
    </div>

    <!-- 会话详情模态框 -->
    <el-dialog v-model="modalVisible" title="会话详情" width="800px" :close-on-click-modal="false">
      <el-descriptions :column="1" border v-if="currentSessionDetails">
        <el-descriptions-item label="会话ID">{{ currentSessionDetails.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentSessionDetails.username }}</el-descriptions-item>
        <el-descriptions-item label="登入时间">{{ currentSessionDetails.createTime }}</el-descriptions-item>
        <el-descriptions-item label="过期时间">{{ currentSessionDetails.expiresAt }}</el-descriptions-item>
        <el-descriptions-item label="权限节点">
          <div v-if="currentSessionDetails.permissions && currentSessionDetails.permissions.length > 0">
            <el-input v-model="permissionSearchKeyword" placeholder="搜索权限节点" clearable style="margin-bottom: 10px; width: 100%" />
            <el-table :data="filteredPermissions" stripe max-height="300px" style="width: 100%">
              <el-table-column label="权限代码" min-width="500">
                <template #default="scope">
                  {{ scope.row }}
                </template>
              </el-table-column>
            </el-table>
            <div style="margin-top: 10px; text-align: right">总权限数：{{ currentSessionDetails.permissions.length }}</div>
          </div>
          <el-tag v-else type="info">无权限</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="modalVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, markRaw, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { View, CloseBold } from "@element-plus/icons-vue";
import AdminSessionApi, { type GetSessionDetailsVo, type GetSessionListDto, type GetSessionListVo } from "@/views/core/api/SessionApi.ts";
import { Result } from "@/commons/entity/Result.ts";

const ViewIcon = markRaw(View);
const CloseIcon = markRaw(CloseBold);

const listForm = reactive<GetSessionListDto>({
  userName: null,
  pageNum: 1,
  pageSize: 10,
});

const listData = ref<GetSessionListVo[]>([]);
const listTotal = ref(0);
const listLoading = ref(false);

const modalVisible = ref(false);
const currentSessionDetails = ref<GetSessionDetailsVo | null>(null);
const permissionSearchKeyword = ref("");
const filteredPermissions = computed(() => {
  if (!currentSessionDetails.value?.permissions) return [];

  return currentSessionDetails.value.permissions.filter((permission) => permission.toLowerCase().includes(permissionSearchKeyword.value.toLowerCase()));
});

const loadList = async () => {
  listLoading.value = true;
  const result = await AdminSessionApi.getSessionList(listForm);

  if (Result.isSuccess(result)) {
    listData.value = result.data;
    listTotal.value = result.total;
  }

  if (Result.isError(result)) {
    ElMessage.error(result.message);
  }

  listLoading.value = false;
};

const resetList = () => {
  listForm.pageNum = 1;
  listForm.pageSize = 10;
  listForm.userName = null;
  loadList();
};

const openModal = async (row: GetSessionListVo) => {
  listLoading.value = true;
  try {
    const res = await AdminSessionApi.getSessionDetails({ id: row.id });
    currentSessionDetails.value = res;
    permissionSearchKeyword.value = ""; // 重置搜索关键词
    modalVisible.value = true;
  } catch (error: any) {
    ElMessage.error(error.message || "获取会话详情失败");
  } finally {
    listLoading.value = false;
  }
};

const handleCloseSession = async (row: GetSessionListVo) => {
  try {
    await ElMessageBox.confirm(`确定要关闭用户 ${row.username} 的会话吗？`, "警告", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
    await AdminSessionApi.closeSession({ id: row.id });
    ElMessage.success("会话关闭成功");
    await loadList(); // Refresh the list
  } catch (error) {
    if (error !== "cancel") {
      const errorMsg = error instanceof Error ? error.message : "关闭会话失败";
      ElMessage.error(errorMsg);
    }
  }
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

.action-buttons {
  margin-bottom: 15px;
  border-top: 2px dashed var(--el-border-color);
  padding-top: 15px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  width: 100%;
}

.el-descriptions {
  margin-top: 0; /* Reset margin if any default from el-dialog content */
}

.el-tag {
  margin-right: 5px;
  margin-bottom: 5px;
}

/* Ensure tooltip content is readable if it becomes too long */
:deep(.el-tooltip__popper) {
  max-width: 400px; /* Adjust as needed */
  word-break: break-all;
}
</style>
