<template>
  <StdListLayout>
    <template #query>
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
    </template>

    <template #table>
      <el-table :data="listData" stripe v-loading="listLoading" border height="100%">
        <el-table-column prop="username" label="用户名" min-width="150" />
        <el-table-column prop="rsMax" label="数据权限(RS)等级" min-width="150">
          <template #default="scope">
            <el-tag v-if="scope.row.rsMax === 0" type="success">全部</el-tag>
            <el-tag v-if="scope.row.rsMax === 1">本公司/租户及以下</el-tag>
            <el-tag v-if="scope.row.rsMax === 2">本部门及以下</el-tag>
            <el-tag v-if="scope.row.rsMax === 3">本部门</el-tag>
            <el-tag v-if="scope.row.rsMax === 4">仅本人</el-tag>
            <el-tag v-if="scope.row.rsMax === 5" type="warning">指定部门</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="登入时间" min-width="180" />
        <el-table-column prop="expiresAt" label="过期时间" min-width="180" />
        <el-table-column prop="isExpired" label="是否过期" min-width="180">
          <template #default="scope">
            <el-tag :type="scope.row.isExpired ? 'danger' : 'success'">
              {{ scope.row.isExpired ? "是" : "否" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" min-width="180">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openModal(scope.row)" :icon="ViewIcon"> 详情 </el-button>
            <el-button link type="danger" size="small" @click="onCloseSession(scope.row)" :icon="CloseIcon">
              关闭会话
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <template #pagination>
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
    </template>
  </StdListLayout>

  <!-- 会话详情模态框 -->
  <el-dialog v-model="modalVisible" title="会话详情" width="900px" :close-on-click-modal="false" destroy-on-close>
    <div v-if="currentSessionDetails" class="session-details-container">
      <!-- 基本信息 -->
      <el-descriptions :column="2" border title="基本信息" class="mb-4">
        <el-descriptions-item label="会话ID" :span="2">{{ currentSessionDetails.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentSessionDetails.username }}</el-descriptions-item>
        <el-descriptions-item label="数据权限(RS)等级">
          <el-tag v-if="currentSessionDetails.rsMax === 0" type="success">全部</el-tag>
          <el-tag v-if="currentSessionDetails.rsMax === 1">本公司/租户及以下</el-tag>
          <el-tag v-if="currentSessionDetails.rsMax === 2">本部门及以下</el-tag>
          <el-tag v-if="currentSessionDetails.rsMax === 3">本部门</el-tag>
          <el-tag v-if="currentSessionDetails.rsMax === 4">仅本人</el-tag>
          <el-tag v-if="currentSessionDetails.rsMax === 5" type="warning">指定部门</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="登入时间">{{ currentSessionDetails.createTime }}</el-descriptions-item>
        <el-descriptions-item label="过期时间">{{ currentSessionDetails.expiresAt }}</el-descriptions-item>
      </el-descriptions>

      <el-row :gutter="20">
        <!-- 数据权限部门列表 -->
        <el-col :span="10" v-if="currentSessionDetails.rsDeptNames && currentSessionDetails.rsDeptNames.length > 0">
          <div class="section-title">允许访问部门(RSAD) ({{ currentSessionDetails.rsDeptNames.length }})</div>
          <el-table :data="currentSessionDetails.rsDeptNames" stripe border max-height="400px" size="small">
            <el-table-column label="部门名称">
              <template #default="scope">
                <el-tag size="small" type="info">{{ scope.row }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-col>

        <!-- 权限节点列表 -->
        <el-col :span="currentSessionDetails.rsDeptNames && currentSessionDetails.rsDeptNames.length > 0 ? 14 : 24">
          <div class="section-title">
            权限节点 ({{ currentSessionDetails.permissions?.length || 0 }})
            <el-input
              v-model="permissionSearchKeyword"
              placeholder="搜索权限代码"
              clearable
              size="small"
              style="width: 200px; float: right"
            />
          </div>
          <el-table :data="filteredPermissions" stripe border max-height="400px" size="small">
            <el-table-column label="权限代码" show-overflow-tooltip>
              <template #default="scope">
                <code>{{ scope.row }}</code>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="modalVisible = false">关闭</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, markRaw, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { View, CloseBold } from "@element-plus/icons-vue";
import AdminSessionApi, {
  type GetSessionDetailsVo,
  type GetSessionListDto,
  type GetSessionListVo,
} from "@/views/auth/api/SessionApi.ts";
import { Result } from "@/commons/entity/Result.ts";
import StdListLayout from "@/soa/std-series/StdListLayout.vue";

const ViewIcon = markRaw(View);
const CloseIcon = markRaw(CloseBold);

const listForm = reactive<GetSessionListDto>({
  userName: null,
  pageNum: 1,
  pageSize: 20,
});

const listData = ref<GetSessionListVo[]>([]);
const listTotal = ref(0);
const listLoading = ref(false);

const modalVisible = ref(false);
const currentSessionDetails = ref<GetSessionDetailsVo | null>(null);
const permissionSearchKeyword = ref("");
const filteredPermissions = computed(() => {
  if (!currentSessionDetails.value?.permissions) return [];

  return currentSessionDetails.value.permissions.filter((permission) =>
    permission.toLowerCase().includes(permissionSearchKeyword.value.toLowerCase())
  );
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
  listForm.pageSize = 20;
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

const onCloseSession = async (row: GetSessionListVo) => {
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
.session-details-container {
  padding: 10px 0;
}

.mb-4 {
  margin-bottom: 20px;
}

.section-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 10px;
  padding-bottom: 5px;
  border-bottom: 1px solid #ebeef5;
  color: #606266;
  line-height: 32px;
}

code {
  font-family: monospace;
  background-color: #f5f7fa;
  padding: 2px 4px;
  border-radius: 4px;
  color: #409eff;
}

.el-tag {
  margin-right: 5px;
}

/* Ensure tooltip content is readable if it becomes too long */
:deep(.el-tooltip__popper) {
  max-width: 400px; /* Adjust as needed */
  word-break: break-all;
}
</style>
