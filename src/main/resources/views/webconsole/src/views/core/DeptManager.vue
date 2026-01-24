<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="queryForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="部门名称">
              <el-input v-model="queryForm.name" placeholder="输入部门名称查询" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="部门状态">
              <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
                <el-option label="正常" :value="0" />
                <el-option label="禁用" :value="1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <!-- 占位，保持布局一致性 -->
          </el-col>
          <el-col :span="3" :offset="3">
            <el-form-item>
              <el-button type="primary" @click="filterData" :disabled="listLoading">查询</el-button>
              <el-button @click="resetQuery" :disabled="listLoading">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="action-buttons">
      <el-button type="success" @click="openModal('add', null)">创建部门</el-button>
    </div>

    <!-- 部门列表 -->
    <div class="list-table">
      <el-table ref="listTableRef" :data="filteredData" stripe v-loading="listLoading" border row-key="id" default-expand-all>
        <el-table-column prop="name" label="部门名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="principalName" label="负责人" min-width="120">
          <template #default="scope">
            <span v-if="scope.row.principalName">{{ scope.row.principalName }}</span>
            <span v-else class="text-gray-400">未设置</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
              {{ scope.row.status === 0 ? "正常" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="seq" label="排序" min-width="100" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" fixed="right" min-width="200">
          <template #default="scope">
            <el-button link type="success" size="small" @click="openModal('add-item', scope.row)" :icon="PlusIcon">
              新增子级
            </el-button>
            <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="EditIcon">
              编辑
            </el-button>
            <el-button link type="danger" size="small" @click="removeList(scope.row.id)" :icon="DeleteIcon"> 删除 </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 部门编辑/新增模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑部门' : modalMode === 'add-item' ? '新增子级部门' : '添加部门'"
      width="500px"
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
        label-width="100px"
        :validate-on-rule-change="false"
      >
        <el-form-item label="父级部门" prop="parentId">
          <el-tree-select
            v-model="modalForm.parentId"
            :data="treeSelectData"
            placeholder="请选择父级部门（不选则为顶级）"
            clearable
            check-strictly
            :render-after-expand="false"
            :disabled="modalMode === 'add-item'"
            node-key="value"
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="modalForm.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="负责人ID" prop="principalId">
          <el-input v-model="modalForm.principalId" placeholder="请输入负责人ID" clearable />
        </el-form-item>
        <el-form-item label="部门状态" prop="status">
          <el-radio-group v-model="modalForm.status">
            <el-radio :value="0">正常</el-radio>
            <el-radio :value="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="seq">
          <el-input-number v-model="modalForm.seq" :min="0" :max="655350" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="modalVisible = false">取消</el-button>
          <el-button type="primary" @click="submitModal" :loading="modalLoading">
            {{ modalMode === "add" ? "创建" : modalMode === "add-item" ? "创建" : "保存" }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { Edit, Delete, Plus } from "@element-plus/icons-vue";
import { markRaw } from "vue";
import type { FormInstance, TableInstance } from "element-plus";
import DeptManagerService from "@/views/core/service/DeptManagerService.ts";

// 使用markRaw包装图标组件
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);
const PlusIcon = markRaw(Plus);

const listTableRef = ref<TableInstance>();
const modalFormRef = ref<FormInstance>();

// 使用部门树服务
const { queryForm, listLoading, filteredData, treeSelectData, filterData, resetQuery, removeList, loadList } =
  DeptManagerService.useDeptTree(listTableRef);

// 使用部门模态框服务
const { modalVisible, modalLoading, modalMode, modalForm, modalRules, openModal, resetModal, submitModal } =
  DeptManagerService.useDeptModal(modalFormRef, loadList, treeSelectData);
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
</style>
