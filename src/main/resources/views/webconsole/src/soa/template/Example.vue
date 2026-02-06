<template>
  <div class="list-container">
    <div class="right-content">
      <!-- 查询条件区域 -->
      <div class="query-form">
        <el-form :model="listForm" inline class="flex justify-between">
          <div>
            <el-form-item label="名称">
              <el-input v-model="listForm.name" placeholder="输入名称" clearable />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="listForm.status" placeholder="选择状态" clearable style="width: 180px">
                <el-option label="启用" :value="0" />
                <el-option label="禁用" :value="1" />
              </el-select>
            </el-form-item>
          </div>
          <el-form-item>
            <el-button type="primary" @click="loadList" :disabled="listLoading">查询</el-button>
            <el-button @click="resetList" :disabled="listLoading">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 操作按钮区域 -->
      <div class="action-buttons">
        <el-button type="success" @click="openModal('add', null)">新增</el-button>
      </div>

      <!-- 列表表格区域 -->
      <div class="list-table">
        <el-table :data="listData" stripe v-loading="listLoading" border>
          <el-table-column prop="name" label="名称" min-width="150" />
          <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
          <el-table-column label="状态" min-width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'" size="small">
                {{ scope.row.status === 0 ? "启用" : "禁用" }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" min-width="180" />
          <el-table-column prop="updateTime" label="更新时间" min-width="180" />
          <el-table-column label="操作" fixed="right" min-width="180">
            <template #default="scope">
              <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="EditIcon">
                编辑
              </el-button>
              <el-button link type="danger" size="small" @click="removeList(scope.row)" :icon="DeleteIcon"> 删除 </el-button>
            </template>
          </el-table-column>
        </el-table>

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

    <!-- 新增/编辑模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑示例' : '新增示例'"
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
        label-width="100px"
        :validate-on-rule-change="false"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="modalForm.name" placeholder="请输入名称" clearable />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="modalForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="modalForm.status">
            <el-radio :label="0">启用</el-radio>
            <el-radio :label="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
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
import { ref, markRaw } from "vue";
import { Edit, Delete } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import ExampleService from "@/soa/template/service/ExampleService.ts";

// 使用markRaw包装图标组件，防止被Vue响应式系统处理
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);

// 列表管理打包
const { listForm, listData, listTotal, listLoading, loadList, resetList, removeList } = ExampleService.useExampleList();

// 模态框表单引用
const modalFormRef = ref<FormInstance>();

// 模态框打包
const { modalVisible, modalLoading, modalMode, modalForm, modalRules, openModal, resetModal, submitModal } =
  ExampleService.useExampleModal(modalFormRef, loadList);
</script>

<style scoped>
.list-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: var(--el-bg-color);
}

.right-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 15px;
  box-sizing: border-box;
}

.action-buttons {
  margin-bottom: 15px;
  border-top: 2px dashed var(--el-border-color);
  padding-top: 15px;
}

.list-table {
  flex: 1;
  width: 100%;
  box-sizing: border-box;
  overflow-y: auto;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 15px;
  padding-bottom: 15px;
}
</style>
