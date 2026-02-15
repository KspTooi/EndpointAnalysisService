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

  <!-- 用户组编辑/新增模态框 -->
  <el-dialog
    v-model="modalVisible"
    :title="modalMode === 'edit' ? '编辑用户组' : '添加用户组'"
    width="800px"
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
      <div class="form-two-columns">
        <div class="form-left-column">
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
          <el-form-item label="用户组描述" prop="remark" label-for="group-remark">
            <el-input v-model="modalForm.remark" type="textarea" :rows="3" id="group-remark" />
          </el-form-item>
          <el-form-item label="用户组状态" prop="status" label-for="group-status">
            <el-radio-group v-model="modalForm.status" id="group-status">
              <el-radio :value="1">启用</el-radio>
              <el-radio :value="0">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </div>

        <div class="form-right-column">
          <el-form-item label="权限码" prop="permissionIds" label-for="permission-search" class="permission-form-item">
            <div class="permission-container">
              <div class="permission-search">
                <el-input v-model="permissionSearch" placeholder="搜索权限码" clearable id="permission-search">
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
                <div class="permission-select-buttons">
                  <el-button-group>
                    <el-button type="primary" size="small" @click="selectAllPermissions"> 全选 </el-button>
                    <el-button type="primary" size="small" @click="deselectAllPermissions"> 取消全选 </el-button>
                  </el-button-group>
                </div>
              </div>
              <div class="permission-list">
                <el-checkbox-group v-model="selectedPermissionIds" id="permission-group" style="width: 240px">
                  <div v-for="permission in filteredPermissions" :key="permission.id" class="permission-item">
                    <el-checkbox :value="permission.id">
                      <div class="permission-info">
                        <span class="permission-name">{{ permission.name }}</span>
                        <span class="permission-code">{{ permission.code }}</span>
                      </div>
                    </el-checkbox>
                  </div>
                </el-checkbox-group>
                <div v-if="filteredPermissions.length === 0" class="no-permission">
                  <el-empty description="未找到匹配的权限码" />
                </div>
              </div>
            </div>
          </el-form-item>
        </div>
      </div>
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
} = UserGroupService.useUserGroupModal(modalFormRef, loadList);

/**
 * 用户组权限模态框打包
 */
const { modalPermissionEditVisible, modalPermissionEditRow, openPermissionEditModal } =
  UserGroupService.useUserGroupPermissionModal();

/**
 * 选中的列表项
 */
const listSelected = ref<GetGroupListVo[]>([]);

//部门列表打包
const {
  queryForm,
  listData: deptListData,
  listLoading: deptListLoading,
  filteredData: deptFilteredData,
  treeSelectData: deptTreeSelectData,
  filterData: deptFilterData,
  resetQuery: deptResetQuery,
  loadList: deptLoadList,
} = OrgManagerService.useOrgTree(null);
</script>

<style scoped>
.form-two-columns {
  display: flex;
  flex-direction: row;
  gap: 20px;
  width: 100%;
}

.form-left-column {
  flex: 1;
  min-width: 200px;
}

.form-right-column {
  flex: 1;
  min-width: 200px;
}

.permission-form-item {
  height: 100%;
}

.permission-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
  height: 100%;
}

.permission-list {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  padding: 10px;
}

.permission-item {
  margin: 8px 0;
  display: flex;
  align-items: center;
}

.permission-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.permission-name {
  font-size: 14px;
  color: var(--el-text-color-primary);
}

.permission-code {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  font-family: monospace;
}

.no-permission {
  padding: 20px 0;
}

:deep(.el-checkbox__label) {
  display: flex;
  align-items: flex-start;
}

.permission-select-buttons {
  display: flex;
  justify-content: flex-start;
  gap: 10px;
  margin-top: 10px;
}
</style>
