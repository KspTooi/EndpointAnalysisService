<template>
  <div class="group-manager-container">
    <div class="query-form">
      <el-form :model="queryForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="组名称" label-for="query-keyword">
              <el-input v-model="queryForm.keyword" placeholder="输入组名称查询" clearable id="query-keyword" />
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
              <el-button type="primary" @click="loadGroupList" :disabled="loading">查询</el-button>
              <el-button @click="resetQuery" :disabled="loading">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="action-buttons">
      <el-button type="success" @click="openModal('add', null)">创建访问组</el-button>
    </div>

    <div class="group-table">
      <el-table :data="groupList" stripe v-loading="loading" border>
        <el-table-column prop="code" label="组标识" min-width="120" />
        <el-table-column prop="name" label="组名称" min-width="120" />
        <el-table-column prop="memberCount" label="成员数量" min-width="100" />
        <el-table-column prop="permissionCount" label="权限数量" min-width="100" />
        <el-table-column label="系统组" min-width="80">
          <template #default="scope">
            <el-tag :type="scope.row.isSystem ? 'info' : 'success'">
              {{ scope.row.isSystem ? "是" : "否" }}
            </el-tag>
          </template>
        </el-table-column>
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
            <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="EditIcon"> 编辑 </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)" :icon="DeleteIcon" :disabled="scope.row.isSystem"> 删除 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-if="!isMobile"
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
        <el-pagination
          v-else
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          layout="prev, pager, next"
          :total="total"
          @current-change="handleCurrentChange"
          :pager-count="5"
          size="small"
          background
        />
      </div>
    </div>

    <!-- 用户组编辑/新增模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑访问组' : '添加访问组'"
      width="800px"
      class="modal-centered"
      :close-on-click-modal="false"
      @close="
        resetModal();
        loadGroupList();
      "
    >
      <el-form v-if="modalVisible" ref="modalFormRef" :model="modalForm" :rules="groupFormRules" label-width="100px" :validate-on-rule-change="false">
        <div class="form-two-columns">
          <div class="form-left-column">
            <el-form-item label="组标识" prop="code" label-for="group-code">
              <el-input
                v-model="modalForm.code"
                :disabled="modalMode === 'edit' && isSystemGroup"
                :placeholder="modalMode === 'edit' && isSystemGroup ? '系统组不可修改标识' : '请输入组标识'"
                id="group-code"
              />
            </el-form-item>
            <el-form-item label="组名称" prop="name" label-for="group-name">
              <el-input
                v-model="modalForm.name"
                :disabled="modalMode === 'edit' && isSystemGroup"
                :placeholder="modalMode === 'edit' && isSystemGroup ? '系统组不可修改名称' : '请输入组名称'"
                id="group-name"
              />
            </el-form-item>
            <el-form-item label="描述" prop="description" label-for="group-description">
              <el-input v-model="modalForm.description" type="textarea" :rows="3" id="group-description" />
            </el-form-item>
            <el-form-item label="状态" prop="status" label-for="group-status">
              <el-radio-group v-model="modalForm.status" id="group-status">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>

          <div class="form-right-column">
            <el-form-item label="权限节点" prop="permissionIds" label-for="permission-search" class="permission-form-item">
              <div class="permission-container">
                <div class="permission-search">
                  <el-input v-model="permissionSearch" placeholder="搜索权限节点" clearable id="permission-search">
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
                      <el-checkbox :value="Number(permission.id)">
                        <div class="permission-info">
                          <span class="permission-name">{{ permission.name }}</span>
                          <span class="permission-code">{{ permission.code }}</span>
                        </div>
                      </el-checkbox>
                    </div>
                  </el-checkbox-group>
                  <div v-if="filteredPermissions.length === 0" class="no-permission">
                    <el-empty description="未找到匹配的权限节点" />
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, markRaw, computed } from "vue";
import { Edit, Delete, Search } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import type { FormInstance } from "element-plus";
import AdminGroupApi, {
  type GetGroupListDto,
  type GetGroupListVo,
  type GroupPermissionDefinitionVo,
  type GetGroupDetailsVo,
  type AddGroupDto,
  type EditGroupDto,
} from "@/api/core/GroupApi.ts";
import AdminPermissionApi from "@/api/core/PermissionApi.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import { Result } from "@/commons/entity/Result.ts";

const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);

// 检测是否为移动设备
const isMobile = ref(false);
const checkMobile = () => {
  isMobile.value = window.innerWidth < 768;
};

// 查询表单
const queryForm = reactive<GetGroupListDto>({
  pageNum: 1,
  pageSize: 10,
  keyword: "",
  status: undefined,
});

// 用户组列表
const groupList = ref<GetGroupListVo[]>([]);
const total = ref(0);
const loading = ref(false);

// 模态框相关
const modalVisible = ref(false);
const modalMode = ref<"add" | "edit">("add");
const modalLoading = ref(false);
const modalFormRef = ref<FormInstance>();

// 表单数据
const modalForm = reactive<GetGroupDetailsVo>({
  id: "",
  code: "",
  name: "",
  description: "",
  isSystem: false,
  status: 1,
  sortOrder: 0,
  permissions: [],
});

// 表单校验规则
const groupFormRules = {
  code: [
    { required: true, message: "请输入组标识", trigger: "blur" },
    { min: 2, max: 50, message: "组标识长度必须在2-50个字符之间", trigger: "blur" },
    { pattern: /^[a-zA-Z][a-zA-Z_]*$/, message: "组标识只能包含英文字符和下划线，且必须以字母开头", trigger: "blur" },
  ],
  name: [
    { required: true, message: "请输入组名称", trigger: "blur" },
    { min: 2, max: 50, message: "组名称长度必须在2-50个字符之间", trigger: "blur" },
  ],
  description: [{ max: 200, message: "描述不能超过200个字符", trigger: "blur" }],
  sortOrder: [
    { required: true, message: "请输入排序号", trigger: "blur" },
    { min: 0, message: "排序号必须大于等于0", trigger: "blur" },
  ],
};

// 在 script 部分添加
const isSystemGroup = ref(false);

// 权限列表
const permissionList = ref<GroupPermissionDefinitionVo[]>([]);

// 权限搜索
const permissionSearch = ref("");

// 过滤后的权限列表
const filteredPermissions = computed(() => {
  const search = permissionSearch.value.toLowerCase().trim();
  if (!search) {
    return permissionList.value;
  }
  return permissionList.value.filter((permission) => permission.name.toLowerCase().includes(search) || permission.code.toLowerCase().includes(search));
});

// 加载用户组列表数据
const loadGroupList = async () => {
  if (loading.value) {
    return;
  }

  try {
    loading.value = true;
    const vos = await AdminGroupApi.getGroupList(queryForm);
    groupList.value = vos.data;
    total.value = Number(vos.total);
  } catch (error) {
    ElMessage.error("加载访问组列表失败");
    console.error("加载访问组列表失败", error);
  } finally {
    loading.value = false;
  }
};

// 重置查询条件
const resetQuery = () => {
  queryForm.keyword = "";
  queryForm.status = undefined;
  queryForm.pageNum = 1;
  loadGroupList();
};

// 处理每页大小变化
const handleSizeChange = (val: number) => {
  queryForm.pageSize = val;
  loadGroupList();
};

// 处理页码变化
const handleCurrentChange = (val: number) => {
  queryForm.pageNum = val;
  loadGroupList();
};

// 重置表单
const resetModal = () => {
  modalForm.id = "";
  modalForm.code = "";
  modalForm.name = "";
  modalForm.description = "";
  modalForm.status = 1;
  modalForm.sortOrder = 0;
  modalForm.permissions = [];

  // 重置权限搜索
  permissionSearch.value = "";

  // 重置权限列表
  permissionList.value = [];

  // 重置选中的权限ID
  selectedPermissionIds.value = [];

  // 重置表单验证状态
  if (modalFormRef.value) {
    modalFormRef.value.resetFields();
  }
};

// 打开模态框
const openModal = async (mode: "add" | "edit", row: GetGroupListVo | null) => {
  modalMode.value = mode;
  resetModal();

  //如果是编辑模式则需要加载详情数据
  if (mode === "edit" && row) {
    isSystemGroup.value = row.isSystem;
    try {
      const ret = await AdminGroupApi.getGroupDetails({ id: row.id });

      modalForm.id = ret.id;
      modalForm.code = ret.code;
      modalForm.name = ret.name;
      modalForm.description = ret.description;
      modalForm.status = ret.status;
      modalForm.sortOrder = ret.sortOrder;
      modalForm.permissions = ret.permissions || [];

      permissionList.value = ret.permissions || [];
      selectedPermissionIds.value = ret.permissions ? ret.permissions.filter((p) => p.has === 0).map((p) => Number(p.id)) : [];
    } catch (error: any) {
      ElMessage.error(error.message || "获取访问组详情失败");
      return;
    }
  } else {
    isSystemGroup.value = false;
    try {
      // 获取所有权限节点
      const permissions = await AdminPermissionApi.getPermissionDefinition();

      permissionList.value = permissions.map((p) => ({
        id: p.id,
        code: p.code,
        name: p.name,
        has: 0,
      }));
    } catch (error) {
      console.log(error);
      ElMessage.error("获取权限节点列表失败");
    }
  }

  modalVisible.value = true;
};

// 提交表单
const submitModal = async () => {
  //先校验表单
  try {
    await modalFormRef?.value?.validate();
  } catch (error) {
    return;
  }

  modalLoading.value = true;

  //提交表单
  try {
    if (modalMode.value === "add") {
      const addDto: AddGroupDto = {
        code: modalForm.code,
        name: modalForm.name,
        description: modalForm.description,
        status: modalForm.status,
        sortOrder: modalForm.sortOrder,
        permissionIds: selectedPermissionIds.value,
      };
      const result = await AdminGroupApi.addGroup(addDto);
      if (Result.isSuccess(result)) {
        ElMessage.success("操作成功");
        resetModal();
      }
      if (Result.isError(result)) {
        ElMessage.error(result.message);
        return;
      }
    }

    if (modalMode.value === "edit") {
      const editDto: EditGroupDto = {
        id: modalForm.id,
        code: modalForm.code,
        name: modalForm.name,
        description: modalForm.description,
        status: modalForm.status,
        sortOrder: modalForm.sortOrder,
        permissionIds: selectedPermissionIds.value,
      };
      const result = await AdminGroupApi.editGroup(editDto);
      if (Result.isSuccess(result)) {
        ElMessage.success("操作成功");
      }
      if (Result.isError(result)) {
        ElMessage.error(result.message);
        return;
      }
    }
  } catch (error: any) {
    ElMessage.error(error.message);
    return;
  } finally {
    modalLoading.value = false;
  }

  loadGroupList();
};

// 处理删除用户组
const handleDelete = (row: GetGroupListVo) => {
  ElMessageBox.confirm(`确定要删除访问组 ${row.name} 吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        const params: CommonIdDto = { id: row.id };
        await AdminGroupApi.removeGroup(params);
        ElMessage.success("删除访问组成功");
        loadGroupList();
      } catch (error) {
        const errorMsg = error instanceof Error ? error.message : "删除失败";
        ElMessage.error(errorMsg);
      }
    })
    .catch(() => {
      // 用户取消删除操作
    });
};

const selectedPermissionIds = ref<number[]>([]);

// 全选权限节点
const selectAllPermissions = () => {
  selectedPermissionIds.value = filteredPermissions.value.map((p) => Number(p.id));
};

// 取消全选权限节点
const deselectAllPermissions = () => {
  selectedPermissionIds.value = [];
};

// 页面加载和窗口大小变化时检测设备类型
onMounted(() => {
  checkMobile();
  window.addEventListener("resize", checkMobile);
  loadGroupList();
});
</script>

<style scoped>
.group-manager-container {
  padding: 20px;
  max-width: 100%;
  overflow-x: auto;
  width: 100%;
}

.action-buttons {
  margin-bottom: 15px;
  border-top: 2px dashed var(--el-border-color);
  padding-top: 15px;
}

.group-table {
  margin-bottom: 20px;
  width: 100%;
  overflow-x: auto;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  width: 100%;
}

@media (min-width: 768px) {
  .pagination-container {
    justify-content: flex-end;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  width: 100%;
}

/* 两列布局样式 */
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

/* 响应式处理 */
@media (max-width: 768px) {
  .form-two-columns {
    flex-direction: column;
  }

  .form-left-column,
  .form-right-column {
    width: 100%;
  }

  .permission-list {
    max-height: 200px;
  }
}

.permission-select-buttons {
  display: flex;
  justify-content: flex-start;
  gap: 10px;
  margin-top: 10px;
}
</style>
