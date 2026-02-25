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
        <el-table-column prop="id" label="空间ID" min-width="120" show-overflow-tooltip />
        <el-table-column prop="name" label="空间名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="remark" label="空间描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="quotaLimit" label="配额限制" min-width="120" show-overflow-tooltip>
          <template #default="scope">{{ mbDisplay(scope.row.quotaLimit) }}</template>
        </el-table-column>
        <el-table-column prop="quotaUsed" label="已用配额" min-width="120" show-overflow-tooltip>
          <template #default="scope">{{ mbDisplay(scope.row.quotaUsed) }}</template>
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
      width="600px"
      :close-on-click-modal="false"
      @close="
        resetModal();
        loadList();
      "
    >
      <el-form
        v-if="modalVisible"
        ref="modalFormRef"
        :model="modalForm"
        :rules="modalRules"
        label-width="120px"
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
            :rows="3"
            maxlength="65535"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="配额限制(MB)" prop="quotaLimit">
          <el-input v-model="modalForm.quotaLimit" placeholder="请输入配额限制(MB)" clearable />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="modalForm.status" style="width: 100%">
            <el-option label="正常" :value="0" />
            <el-option label="归档" :value="1" />
          </el-select>
        </el-form-item>
      </el-form>

      <!-- 成员列表区域 -->
      <div style="margin-top: 16px">
        <div class="flex justify-between items-center" style="margin-bottom: 8px">
          <span style="font-size: 14px; font-weight: 600">成员列表</span>
          <div class="flex gap-2">
            <el-button size="small" @click="openUserSelect">添加用户</el-button>
            <el-button size="small" @click="openDeptSelect">添加部门</el-button>
          </div>
        </div>
        <el-table :data="modalMembers" border stripe size="small" max-height="240" v-loading="memberOpLoading">
          <el-table-column label="类型" width="70">
            <template #default="scope">
              <el-tag :type="scope.row.memberKind === 0 ? 'primary' : 'warning'" size="small">
                {{ scope.row.memberKind === 0 ? "用户" : "部门" }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="memberName" label="名称" min-width="120" show-overflow-tooltip />
          <el-table-column label="角色" width="150">
            <template #default="scope">
              <el-select
                v-model="scope.row.role"
                size="small"
                style="width: 100%"
                :disabled="modalMode === 'edit' && scope.row.role === 0"
                @change="modalMode === 'edit' ? editUpdateMemberRole(scope.row) : undefined"
              >
                <el-option label="主管理员" :value="0" />
                <el-option label="行政管理员" :value="1" />
                <el-option label="编辑者" :value="2" />
                <el-option label="查看者" :value="3" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="70" fixed="right">
            <template #default="scope">
              <el-button
                v-if="modalMode === 'add'"
                link
                type="danger"
                size="small"
                @click="removeMember(scope.$index)"
              >移除</el-button>
              <el-button
                v-if="modalMode === 'edit'"
                link
                type="danger"
                size="small"
                :disabled="scope.row.role === 0"
                @click="editRemoveMember(scope.row)"
              >移除</el-button>
            </template>
          </el-table-column>
        </el-table>
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

<style scoped></style>
