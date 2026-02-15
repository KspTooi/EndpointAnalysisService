<template>
  <StdListLayout>
    <template #query>
      <el-form :model="listForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="用户组名称" label-for="query-keyword">
              <el-input v-model="listForm.keyword" placeholder="输入用户组名称查询" clearable id="query-keyword" />
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

    <template #actions>
      <el-button type="success" @click="openModal('add', null)">创建用户组</el-button>
      <el-button
        type="danger"
        @click="removeListBatch(listSelected)"
        :disabled="listSelected.length === 0"
        :loading="listLoading"
      >
        删除选中项
      </el-button>
    </template>

    <template #table>
      <el-table
        :data="listData"
        stripe
        v-loading="listLoading"
        border
        height="100%"
        @selection-change="(val: GetGroupListVo[]) => (listSelected = val)"
      >
        <el-table-column type="selection" width="40" />
        <el-table-column prop="name" label="用户组名称" min-width="120" />
        <el-table-column prop="code" label="用户组标识" min-width="120" />
        <el-table-column prop="memberCount" label="成员数量" min-width="100" />
        <el-table-column prop="permissionCount" label="权限数量" min-width="100" />
        <!-- <el-table-column label="系统用户组" min-width="80">
          <template #default="scope">
            <el-tag :type="scope.row.isSystem ? 'info' : 'success'">
              {{ scope.row.isSystem ? "是" : "否" }}
            </el-tag>
          </template>
        </el-table-column> -->
        <el-table-column label="状态" min-width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? "启用" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column label="操作" fixed="right" min-width="180">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="EditIcon">
              编辑
            </el-button>
            <el-button link type="primary" size="small" @click="openPermissionEditModal(scope.row)" :icon="EditIcon">
              管理权限
            </el-button>
            <el-button
              link
              type="danger"
              size="small"
              @click="removeList(scope.row.id)"
              :icon="DeleteIcon"
              :disabled="scope.row.isSystem"
            >
              删除
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
  </StdListLayout>

  <UserGpModal
    :visible="modalPermissionEditVisible"
    :row="modalPermissionEditRow"
    @close="modalPermissionEditVisible = false"
  />

  <CoreOrgDeptSelectModal
    v-model="deptSelectModalVisible"
    multiple
    :default-selected="modalForm.deptIds"
    @confirm="onDeptSelectConfirm"
  />

  <!-- 用户组编辑/新增模态框 -->
  <el-dialog
    v-model="modalVisible"
    :title="modalMode === 'edit' ? '编辑用户组' : '添加用户组'"
    width="900px"
    :close-on-click-modal="false"
    @close="
      (async () => {
        await resetModal();
        loadList();
      })()
    "
  >
    <el-form
      v-if="modalVisible"
      ref="modalFormRef"
      :model="modalForm"
      :rules="modalRules"
      label-width="100px"
      :validate-on-rule-change="false"
    >
      <el-row :gutter="20">
        <!-- 左侧基础信息 -->
        <el-col :span="12">
          <div class="p-2.5">
            <div class="section-title text-sm font-bold mb-4 pl-2.5">基础信息</div>
            <el-form-item label="用户组标识" prop="code" label-for="group-code">
              <el-input
                v-model="modalForm.code"
                :disabled="modalMode === 'edit' && isSystemGroup"
                :placeholder="modalMode === 'edit' && isSystemGroup ? '系统用户组不可修改标识' : '请输入组标识'"
                id="group-code"
              />
            </el-form-item>
            <el-form-item label="用户组名称" prop="name" label-for="group-name">
              <el-input
                v-model="modalForm.name"
                :disabled="modalMode === 'edit' && isSystemGroup"
                :placeholder="modalMode === 'edit' && isSystemGroup ? '系统用户组不可修改名称' : '请输入组名称'"
                id="group-name"
              />
            </el-form-item>
            <el-form-item label="排序号" prop="seq">
              <el-input-number v-model="modalForm.seq" :min="0" class="w-full" />
            </el-form-item>
            <el-form-item label="用户组状态" prop="status" label-for="group-status">
              <el-radio-group v-model="modalForm.status" id="group-status">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="用户组描述" prop="remark" label-for="group-remark">
              <el-input v-model="modalForm.remark" type="textarea" :rows="2" id="group-remark" placeholder="请输入描述" />
            </el-form-item>

            <div class="section-title text-sm font-bold mb-4 pl-2.5 mt-5">数据权限</div>
            <el-form-item label="权限范围" prop="rowScope" label-for="group-rowScope">
              <el-select v-model="modalForm.rowScope" placeholder="请选择数据权限" id="group-rowScope" class="w-full">
                <el-option :value="0" label="全部" />
                <el-option :value="1" label="本公司/租户及以下" />
                <el-option :value="2" label="本部门及以下" />
                <el-option :value="3" label="本部门" />
                <el-option :value="4" label="仅本人" />
                <el-option :value="5" label="指定部门" />
              </el-select>
            </el-form-item>
            <el-form-item v-if="modalForm.rowScope === 5" label="指定部门" prop="deptIds">
              <div class="flex items-center">
                <el-button type="primary" @click="openDeptSelect" size="small">选择部门</el-button>
                <span class="ml-2 text-gray-500">已选择 {{ modalForm.deptIds?.length || 0 }} 个部门</span>
              </div>
            </el-form-item>
          </div>
        </el-col>

        <!-- 右侧权限分配 -->
        <el-col :span="12">
          <div class="p-2.5">
            <div class="section-title text-sm font-bold mb-4 pl-2.5">功能权限分配</div>
            <div class="permission-wrapper rounded overflow-hidden">
              <div class="p-2.5 permission-header-bg">
                <el-input v-model="permissionSearch" placeholder="搜索权限码/名称" clearable size="small">
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
                <div class="mt-2 flex justify-between items-center">
                  <span class="text-xs text-gray-500">已选 {{ selectedPermissionIds.length }} 项</span>
                  <el-button-group>
                    <el-button type="primary" size="small" link @click="selectAllPermissions">全选</el-button>
                    <el-button type="primary" size="small" link @click="deselectAllPermissions">清空</el-button>
                  </el-button-group>
                </div>
              </div>
              <div class="h-[380px] overflow-y-auto p-2.5">
                <el-checkbox-group v-model="selectedPermissionIds">
                  <div v-for="permission in filteredPermissions" :key="permission.id" class="permission-row py-1.5">
                    <el-checkbox :value="permission.id">
                      <div class="flex flex-col leading-snug ml-2">
                        <div class="text-[13px] perm-name-color">{{ permission.name }}</div>
                        <div class="text-[11px] font-mono perm-code-color">{{ permission.code }}</div>
                      </div>
                    </el-checkbox>
                  </div>
                </el-checkbox-group>
                <el-empty v-if="filteredPermissions.length === 0" :image-size="60" description="无匹配权限" />
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="modalVisible = false">取消</el-button>
        <el-button type="primary" @click="submitModal" :loading="modalLoading">
          {{ modalMode === "add" ? "创建" : "保存" }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, markRaw } from "vue";
import { Edit, Delete } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import type { GetGroupListVo } from "@/views/auth/api/GroupApi.ts";
import UserGroupService from "@/views/auth/service/UserGroupService.ts";
import UserGpModal from "@/views/auth/components/UserGpModal.vue";
import CoreOrgDeptSelectModal from "@/views/core/components/public/CoreOrgDeptSelectModal.vue";
import StdListLayout from "@/soa/std-series/StdListLayout.vue";
import OrgManagerService from "../core/service/OrgManagerService";

const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);
const modalFormRef = ref<FormInstance>();

/**
 * 用户组列表打包
 */
const { listForm, listData, listTotal, listLoading, loadList, resetList, removeList, removeListBatch } =
  UserGroupService.useUserGroupList();

/**
 * 用户组模态框打包
 */
const {
  modalVisible,
  modalMode,
  modalLoading,
  isSystemGroup,
  modalForm,
  modalRules,
  permissionSearch,
  selectedPermissionIds,
  filteredPermissions,
  openModal,
  resetModal,
  submitModal,
  selectAllPermissions,
  deselectAllPermissions,
  deptSelectModalVisible,
  openDeptSelect,
  onDeptSelectConfirm,
} = UserGroupService.useUserGroupModal(modalFormRef, loadList);

/**
 * 用户组权限模态框打包
 */
const {
  modalPermissionEditVisible,
  modalPermissionEditRow,
  openPermissionEditModal: openPermModal,
} = UserGroupService.useUserGroupPermissionModal();

const openPermissionEditModal = openPermModal;

/**
 * 选中的列表项
 */
const listSelected = ref<GetGroupListVo[]>([]);
</script>

<style scoped>
/* 使用 CSS 变量的样式保留在这里 */
.section-title {
  color: var(--el-text-color-primary);
  border-left: 4px solid var(--el-color-primary);
}

.permission-wrapper {
  border: 1px solid var(--el-border-color-lighter);
}

.permission-header-bg {
  background-color: var(--el-fill-color-light);
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.permission-row {
  border-bottom: 1px dashed var(--el-border-color-extra-light);
}

.permission-row:last-child {
  border-bottom: none;
}

.perm-name-color {
  color: var(--el-text-color-primary);
}

.perm-code-color {
  color: var(--el-text-color-secondary);
}

:deep(.el-checkbox) {
  height: auto;
  display: flex;
  align-items: center;
}

:deep(.el-checkbox__label) {
  padding-left: 0;
}
</style>
