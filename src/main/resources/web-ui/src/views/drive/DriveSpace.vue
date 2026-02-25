<template>
  <StdListContainer>
    <!-- 查询条件区域 -->
    <StdListAreaQuery>
      <el-form :model="listForm" inline class="flex justify-between">
        <div>
          <el-form-item label="空间名称">
            <el-input v-model="listForm.name" placeholder="输入空间名称" clearable />
          </el-form-item>
          <el-form-item label="空间描述">
            <el-input v-model="listForm.remark" placeholder="输入空间描述" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="listForm.status" placeholder="全部" clearable style="width: 120px">
              <el-option label="正常" :value="0" />
              <el-option label="归档" :value="1" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item>
          <el-button type="primary" @click="loadList" :disabled="listLoading">查询</el-button>
          <el-button @click="resetList" :disabled="listLoading">重置</el-button>
        </el-form-item>
      </el-form>
    </StdListAreaQuery>

    <!-- 操作按钮区域 -->
    <StdListAreaAction class="flex gap-2">
      <el-button type="success" @click="openModal('add', null)">新增云盘空间</el-button>
    </StdListAreaAction>

    <!-- 列表表格区域 -->
    <StdListAreaTable>
      <el-table :data="listData" stripe v-loading="listLoading" border height="100%">
        <el-table-column type="index" label="序号" width="60" align="center">
          <template #default="scope">
            {{ (listForm.pageNum - 1) * listForm.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="空间名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="remark" label="空间描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="maName" label="主管理员" min-width="100" show-overflow-tooltip />
        <el-table-column prop="memberCount" label="成员数" min-width="80" align="center" />
        <el-table-column label="我的角色" min-width="100" align="center">
          <template #default="scope">
            <span :class="roleClass(scope.row.myRole)" style="font-weight: 500">
              {{ roleLabel(scope.row.myRole) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="配额使用情况" min-width="200">
          <template #default="scope">
            <div class="quota-progress-wrapper">
              <div class="quota-info">
                <span>{{ mbDisplay(scope.row.quotaUsed) }} / {{ mbDisplay(scope.row.quotaLimit) }}</span>
                <span class="quota-percentage">{{ getQuotaPercentage(scope.row.quotaUsed, scope.row.quotaLimit) }}%</span>
              </div>
              <el-progress
                :percentage="getQuotaPercentage(scope.row.quotaUsed, scope.row.quotaLimit)"
                :stroke-width="8"
                :show-text="false"
                :color="getQuotaColor(scope.row.quotaUsed, scope.row.quotaLimit)"
                class="square-progress"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="80" show-overflow-tooltip>
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'info'">
              {{ scope.row.status === 0 ? "正常" : "归档" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" min-width="180">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="EditIcon">
              管理空间
            </el-button>
            <el-button link type="danger" size="small" @click="removeList(scope.row)" :icon="DeleteIcon"> 删除 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <template #pagination>
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
      </template>
    </StdListAreaTable>

    <!-- 新增/编辑模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '管理云盘空间' : '新增云盘空间'"
      width="800px"
      :close-on-click-modal="false"
      class="drive-space-modal"
      @close="
        resetModal();
        loadList();
      "
    >
      <div class="modal-content-wrapper" v-if="modalVisible">
        <!-- 左侧：基本信息表单 -->
        <div class="base-info-section">
          <div class="section-title">基本信息</div>
          <el-form
            ref="modalFormRef"
            :model="modalForm"
            :rules="modalRules"
            label-position="top"
            :validate-on-rule-change="false"
          >
            <el-form-item label="空间名称" prop="name">
              <el-input v-model="modalForm.name" placeholder="请输入空间名称" clearable maxlength="80" show-word-limit />
            </el-form-item>
            <el-form-item label="空间描述" prop="remark">
              <el-input
                v-model="modalForm.remark"
                type="textarea"
                placeholder="请输入空间描述"
                :rows="4"
                maxlength="65535"
                show-word-limit
              />
            </el-form-item>
            <div class="flex gap-4">
              <el-form-item label="配额限制(MB)" prop="quotaLimit" class="flex-1">
                <el-input v-model="modalForm.quotaLimit" placeholder="请输入配额限制" clearable />
              </el-form-item>
              <el-form-item label="状态" prop="status" class="flex-1">
                <el-select v-model="modalForm.status" style="width: 100%">
                  <el-option label="正常" :value="0" />
                  <el-option label="归档" :value="1" />
                </el-select>
              </el-form-item>
            </div>
          </el-form>
        </div>

        <!-- 右侧：成员管理 -->
        <div class="member-section">
          <div class="section-header">
            <div class="section-title">成员管理</div>
            <div class="action-buttons">
              <el-button size="small" type="primary" plain @click="openUserSelect">添加用户</el-button>
              <el-button size="small" type="primary" plain @click="openDeptSelect">添加部门</el-button>
            </div>
          </div>

          <div class="member-table-wrapper">
            <el-table :data="modalMembers" border stripe size="small" height="100%" v-loading="memberOpLoading">
              <el-table-column prop="memberName" label="名称" min-width="100" show-overflow-tooltip />
              <el-table-column label="类型" width="65" align="center">
                <template #default="scope">
                  <span :class="scope.row.memberKind === 0 ? 'text-primary' : 'text-warning'" style="font-weight: 500">
                    {{ scope.row.memberKind === 0 ? "用户" : "部门" }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="角色" width="120">
                <template #default="scope">
                  <el-select
                    v-model="scope.row.role"
                    size="small"
                    style="width: 100%"
                    :disabled="modalMode === 'edit' && scope.row.role === 0"
                    @change="modalMode === 'edit' ? editUpdateMemberRole(scope.row) : undefined"
                  >
                    <el-option label="主管理员" :value="0" disabled />
                    <el-option label="行政管理员" :value="1" />
                    <el-option label="编辑者" :value="2" />
                    <el-option label="查看者" :value="3" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="60" align="center" fixed="right">
                <template #default="scope">
                  <el-button v-if="modalMode === 'add'" link type="danger" size="small" @click="removeMember(scope.$index)">
                    移除
                  </el-button>
                  <el-button
                    v-if="modalMode === 'edit'"
                    link
                    type="danger"
                    size="small"
                    :disabled="scope.row.role === 0"
                    @click="editRemoveMember(scope.row)"
                  >
                    移除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="modalVisible = false">取消</el-button>
          <el-button type="primary" @click="submitModal" :loading="modalLoading">
            {{ modalMode === "add" ? "创建" : "保存" }}
          </el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 用户选择弹窗 -->
    <CoreUserSelectModal ref="userSelectModalRef" :multiple="true" />
    <!-- 部门选择弹窗 -->
    <CoreOrgDeptSelectModal ref="deptSelectModalRef" :multiple="true" />
  </StdListContainer>
</template>

<script setup lang="ts">
import { ref, markRaw } from "vue";
import { Edit, Delete } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import DriveSpaceService from "@/views/drive/service/DriveSpaceService.ts";
import StdListContainer from "@/soa/std-series/StdListContainer.vue";
import StdListAreaQuery from "@/soa/std-series/StdListAreaQuery.vue";
import StdListAreaAction from "@/soa/std-series/StdListAreaAction.vue";
import StdListAreaTable from "@/soa/std-series/StdListAreaTable.vue";
import CoreUserSelectModal from "@/views/core/components/public/CoreUserSelectModal.vue";
import CoreOrgDeptSelectModal from "@/views/core/components/public/CoreOrgDeptSelectModal.vue";
import type { GetUserListVo } from "@/views/core/api/UserApi.ts";
import type { GetOrgTreeVo } from "@/views/core/api/OrgApi.ts";

// bytes 转 MB 显示
const mbDisplay = (bytes: string) => {
  if (!bytes) return "-";
  const mb = Number(bytes) / 1048576;
  return mb >= 1024 ? `${(mb / 1024).toFixed(1)} GB` : `${Math.round(mb)} MB`;
};

// 计算配额百分比
const getQuotaPercentage = (used: string, limit: string) => {
  if (!limit || limit === "0") return 0;
  const percentage = Math.round((Number(used) / Number(limit)) * 100);
  return percentage > 100 ? 100 : percentage;
};

// 根据使用百分比获取进度条颜色
const getQuotaColor = (used: string, limit: string) => {
  const p = getQuotaPercentage(used, limit);
  if (p >= 90) return "#f56c6c"; // 危险
  if (p >= 70) return "#e6a23c"; // 警告
  return "#409eff"; // 正常
};

// 角色文字映射
const roleLabel = (role: number) => {
  if (role === 0) return "主管理员";
  if (role === 1) return "行政管理员";
  if (role === 2) return "编辑者";
  if (role === 3) return "查看者";
  return "未知";
};

// 角色颜色类映射
const roleClass = (role: number) => {
  if (role === 0) return "text-danger";
  if (role === 1) return "text-primary";
  if (role === 2) return "text-success";
  return "text-info";
};

// 使用markRaw包装图标组件，防止被Vue响应式系统处理
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);

// 列表管理打包
const { listForm, listData, listTotal, listLoading, loadList, resetList, removeList } = DriveSpaceService.useDriveSpaceList();

// 模态框表单引用
const modalFormRef = ref<FormInstance>();

// 模态框打包
const {
  modalVisible,
  modalLoading,
  modalMode,
  modalForm,
  modalRules,
  modalMembers,
  memberOpLoading,
  openModal,
  resetModal,
  submitModal,
  addUserMembers,
  addDeptMembers,
  removeMember,
  editUpdateMemberRole,
  editRemoveMember,
} = DriveSpaceService.useDriveSpaceModal(modalFormRef, loadList);

// 用户选择 Modal ref
const userSelectModalRef = ref<InstanceType<typeof CoreUserSelectModal>>();
// 部门选择 Modal ref
const deptSelectModalRef = ref<InstanceType<typeof CoreOrgDeptSelectModal>>();

// 打开用户选择弹窗
const openUserSelect = async () => {
  if (!userSelectModalRef.value) return;
  try {
    const result = await userSelectModalRef.value.select();
    const users = Array.isArray(result) ? (result as GetUserListVo[]) : [result as GetUserListVo];
    await addUserMembers(users);
  } catch {
    // 用户取消
  }
};

// 打开部门选择弹窗
const openDeptSelect = async () => {
  if (!deptSelectModalRef.value) return;
  try {
    const result = await deptSelectModalRef.value.select();
    const depts = Array.isArray(result) ? (result as GetOrgTreeVo[]) : [result as GetOrgTreeVo];
    await addDeptMembers(depts);
  } catch {
    // 用户取消
  }
};
</script>

<style scoped>
.drive-space-modal :deep(.el-dialog__body) {
  padding: 20px;
}

.modal-content-wrapper {
  display: flex;
  gap: 24px;
  height: 480px;
}

.base-info-section {
  flex: 3;
  display: flex;
  flex-direction: column;
}

.member-section {
  flex: 4;
  display: flex;
  flex-direction: column;
  border-left: 1px solid var(--el-border-color-lighter);
  padding-left: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-bottom: 12px;
}

.section-header .section-title {
  margin-bottom: 0;
}

.member-table-wrapper {
  flex: 1;
  overflow: hidden;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.text-primary {
  color: var(--el-color-primary);
}

.text-warning {
  color: var(--el-color-warning);
}

.text-danger {
  color: var(--el-color-danger);
}

.text-success {
  color: var(--el-color-success);
}

.text-info {
  color: var(--el-color-info);
}

:deep(.el-form-item__label) {
  font-weight: 500;
  padding-bottom: 4px !important;
}

:deep(.el-table) {
  --el-table-header-bg-color: var(--el-fill-color-light);
}

.quota-progress-wrapper {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.quota-info {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.quota-percentage {
  font-weight: 500;
}

/* 直角风格进度条 */
.square-progress :deep(.el-progress-bar__outer) {
  border-radius: 0 !important;
}

.square-progress :deep(.el-progress-bar__inner) {
  border-radius: 0 !important;
}
</style>
