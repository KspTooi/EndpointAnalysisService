<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="queryForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="组织机构名称">
              <el-input v-model="queryForm.name" placeholder="输入组织机构名称查询" clearable />
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
              <el-button type="primary" @click="filterData" :disabled="listLoading">查询</el-button>
              <el-button @click="resetQuery" :disabled="listLoading">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="action-buttons">
      <el-button type="success" @click="openModal('add', null)">创建组织机构</el-button>
    </div>

    <!-- 组织机构列表 -->
    <div class="list-table">
      <el-table ref="listTableRef" :data="filteredData" stripe v-loading="listLoading" border row-key="id" default-expand-all>
        <el-table-column prop="name" label="组织机构名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="kind" label="类型" min-width="100">
          <template #default="scope">
            <el-tag :type="scope.row.kind === 1 ? 'primary' : 'info'">
              {{ scope.row.kind === 1 ? "企业" : "部门" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="seq" label="排序" min-width="100" />
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

    <!-- 组织机构编辑/新增模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="
        modalMode === 'edit'
          ? '编辑' + modalKindName
          : modalMode === 'add-item'
            ? '新增子级' + modalKindName
            : '添加' + modalKindName
      "
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
        label-width="120px"
        :validate-on-rule-change="false"
      >
        <el-form-item :label="modalKindName + '名称'" prop="name">
          <el-input v-model="modalForm.name" :placeholder="'请输入' + modalKindName + '名称'" />
        </el-form-item>

        <el-form-item :label="modalKindName + '类型'" prop="kind" v-if="modalMode !== 'edit'">
          <el-radio-group v-model="modalForm.kind" :disabled="modalMode === 'add-item'">
            <el-radio :value="1">企业</el-radio>
            <el-radio :value="0">部门</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="上级组织" prop="parentId" v-show="modalKind == 0">
          <el-tree-select
            v-model="modalForm.parentId"
            :data="treeSelectData"
            placeholder="请选择上级组织"
            clearable
            check-strictly
            :render-after-expand="false"
            :disabled="modalMode === 'add-item'"
            node-key="value"
          />
        </el-form-item>

        <!-- <el-form-item :label="modalKindName + '主管ID'" prop="principalId" v-if="modalForm.kind === 0">
          <el-input v-model="modalForm.principalId" placeholder="请输入主管ID" clearable />
        </el-form-item> -->
        <el-form-item :label="modalKindName + '排序'" prop="seq">
          <el-input-number v-model="modalForm.seq" :min="0" :max="655350" style="width: 100%" />
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
import OrgManagerService from "@/views/core/service/OrgManagerService.ts";

// 使用markRaw包装图标组件
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);
const PlusIcon = markRaw(Plus);

const listTableRef = ref<TableInstance>();
const modalFormRef = ref<FormInstance>();

// 使用组织机构树服务
const { queryForm, listLoading, filteredData, treeSelectData, filterData, resetQuery, removeList, loadList } =
  OrgManagerService.useOrgTree(listTableRef);

// 使用组织机构模态框服务
const {
  modalKindName,
  modalKind,
  modalVisible,
  modalLoading,
  modalMode,
  modalForm,
  modalRules,
  openModal,
  resetModal,
  submitModal,
} = OrgManagerService.useOrgModal(modalFormRef, loadList, treeSelectData);
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

.form-tip {
  font-size: 12px;
  color: var(--el-color-info);
  margin-top: 5px;
}
</style>
