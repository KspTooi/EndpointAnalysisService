<template>
  <div class="list-container">
    <!-- 说明文档 -->
    <el-alert type="info" :closable="false" style="margin-bottom: 20px; margin-top: 20px">
      <template #title>
        <div style="display: flex; align-items: center; gap: 8px">
          <el-icon><InfoFilled /></el-icon>
          <span style="font-weight: bold">权限节点缺失指示器</span>
        </div>
      </template>
      <div style="font-size: 13px; line-height: 1.6">
        <div>
          <span style="color: #67c23a; font-weight: bold">● 绿色</span>：权限完整，所有权限节点已在系统中定义
          <span style="margin-left: 16px; color: #e6a23c; font-weight: bold">● 橙色</span>：部分缺失，部分权限节点未在系统中定义
          <span style="margin-left: 16px; color: #f56c6c; font-weight: bold">● 红色</span
          >：完全缺失，所有权限节点均未在系统中定义
        </div>
      </div>
    </el-alert>

    <!-- 查询表单 -->
    <div class="query-form">
      <QueryPersistTip />
      <el-form :model="listForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="菜单名称">
              <el-input v-model="listForm.name" placeholder="请输入菜单名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="所需权限">
              <el-input v-model="listForm.permission" placeholder="请输入所需权限" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="菜单类型">
              <el-select v-model="listForm.menuKind" placeholder="请选择菜单类型" clearable>
                <el-option label="目录" value="0" />
                <el-option label="菜单" value="1" />
                <el-option label="按钮" value="2" />
              </el-select>
            </el-form-item>
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

    <div class="action-buttons">
      <el-button type="success" @click="openModal('add', null)">创建菜单</el-button>
      <el-button type="primary" @click="listExpandToggle()"> {{ listExpand ? "收起全部" : "展开全部" }} </el-button>
    </div>

    <!-- 列表 -->
    <div class="list-table">
      <el-table :data="listData" v-loading="listLoading" border row-key="id" default-expand-all ref="listTableRef">
        <el-table-column label="菜单名称" prop="name" show-overflow-tooltip width="360">
          <template #default="scope">
            <div style="display: flex; align-items: center; gap: 8px; display: inline">
              <Icon
                v-if="scope.row.menuIcon"
                :icon="scope.row.menuIcon"
                :width="16"
                :height="16"
                style="vertical-align: middle; display: inline"
              />
              {{ scope.row.name }}
              <span v-if="scope.row.menuKind === 2" style="color: #999; font-size: 14px"> ({{ scope.row.menuBtnId }}) </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="类型" prop="menuKind" width="70">
          <template #default="scope">
            <el-tag v-if="scope.row.menuKind === 0">目录</el-tag>
            <el-tag v-if="scope.row.menuKind === 1" type="success">菜单</el-tag>
            <el-tag v-if="scope.row.menuKind === 2" type="info">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="菜单路径" prop="menuPath" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.menuKind === 0 || scope.row.menuKind === 2" style="color: #999; font-size: 12px">不适用</span>
            <span v-else>{{ scope.row.menuPath }}</span>
          </template>
        </el-table-column>
        <el-table-column label="所需权限" prop="permission" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.menuKind === 0" style="color: #999; font-size: 12px">不适用</span>
            <span
              v-else
              :style="{
                color:
                  scope.row.missingPermission === 1
                    ? '#f56c6c'
                    : scope.row.missingPermission === 2
                      ? '#e6a23c'
                      : scope.row.missingPermission === 0 && scope.row.permission !== '*'
                        ? '#67c23a'
                        : '',
              }"
            >
              {{ scope.row.permission }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="排序" prop="seq" width="65" >
          <template #default="scope">
            <el-button
                link
                type="success"
                size="small"
                @click="openModal('add-item', scope.row)"
                :icon="EditIcon"
              >
                {{ scope.row.seq }}
              </el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="230">
          <template #default="scope">
            <div style="display: inline-flex; justify-content: flex-end; align-items: center; gap: 8px; width: 100%">
              <!-- 按钮下无法新增子项 -->
              <el-button
                v-if="scope.row.parentId === null || scope.row.menuKind == 1"
                link
                type="success"
                size="small"
                @click="openModal('add-item', scope.row)"
                :icon="PlusIcon"
              >
                新增子项
              </el-button>

              <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="EditIcon">
                编辑
              </el-button>
              <el-button
                link
                type="danger"
                size="small"
                @click="removeList(scope.row.id)"
                :icon="DeleteIcon"
                :disabled="scope.row.children && scope.row.children.length > 0"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 选择路由模态框 -->
    <GenricRouteChooseModal v-model="modalForm.menuPath" v-model:searchKeyword="grcmQuery" ref="grcmRef" />

    <!-- 菜单编辑模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑' + modalFormLabel : '添加' + modalFormLabel"
      width="550px"
      :close-on-click-modal="false"
      @close="
        resetModal(true);
        loadList();
      "
    >
      <el-form
        v-if="modalVisible"
        ref="modalFormRef"
        :model="modalForm"
        :rules="modalRules"
        label-width="80px"
        :validate-on-rule-change="false"
      >
        <el-form-item label="父级菜单" prop="parentId">
          <el-tree-select
            v-model="modalForm.parentId"
            :data="menuTreeForSelect"
            node-key="id"
            :props="{ value: 'id', label: 'name', children: 'children' }"
            check-strictly
            placeholder="请选择父级菜单"
            clearable
            default-expand-all
          />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuKind">
          <el-select v-model="modalForm.menuKind" placeholder="请选择菜单类型" clearable :disabled="modalMode === 'edit'">
            <el-option label="目录" :value="0" :disabled="modalMode === 'add-item'" />
            <el-option label="菜单" :value="1" :disabled="modalMode === 'add-item' && modalCurrentRow?.menuKind == 1" />
            <el-option label="按钮" :value="2" :disabled="modalMode === 'add-item' && modalCurrentRow?.menuKind == 0" />
          </el-select>
        </el-form-item>
        <el-form-item :label="modalFormLabel + '名称'" prop="name">
          <el-input v-model="modalForm.name" placeholder="请输入菜单名称" clearable />
        </el-form-item>
        <el-form-item label="按钮ID" prop="menuBtnId" v-if="modalForm.menuKind == 2">
          <el-input v-model="modalForm.menuBtnId" placeholder="请输入按钮ID" clearable />
        </el-form-item>
        <el-form-item :label="modalFormLabel + '路径'" prop="menuPath" v-if="modalForm.menuKind == 1">
          <el-input v-model="modalForm.menuPath" placeholder="请输入菜单路径" clearable>
            <template #append>
              <el-button @click="openGRCM">选择路由</el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="所需权限" prop="permission" v-if="modalForm.menuKind == 1 || modalForm.menuKind == 2">
          <el-input v-model="modalForm.permission" placeholder="请输入所需权限" clearable />
        </el-form-item>
        <el-form-item :label="modalFormLabel + '描述'" prop="description">
          <el-input v-model="modalForm.description" placeholder="请输入菜单描述" clearable />
        </el-form-item>
        <el-form-item
          :label="modalFormLabel + '图标'"
          prop="menuIcon"
          v-if="modalForm.menuKind == 0 || modalForm.menuKind == 1"
        >
          <IconPicker v-model="modalForm.menuIcon" />
        </el-form-item>
        <el-form-item label="查询参数" prop="menuQueryParam" v-if="modalForm.menuKind == 1">
          <el-input v-model="modalForm.menuQueryParam" placeholder="请输入菜单查询参数" clearable />
        </el-form-item>
        <el-form-item label="是否隐藏" prop="menuHidden">
          <el-radio-group v-model="modalForm.menuHidden">
            <el-radio :value="0">不隐藏</el-radio>
            <el-radio :value="1">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="seq">
          <el-input-number v-model.number="modalForm.seq" :min="0" :max="655350" placeholder="请输入排序" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="modalVisible = false">关闭</el-button>
          <el-button type="primary" @click="submitModal" :loading="modalLoading">
            {{ modalMode === "add" ? "创建" : "保存" }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import type { FormInstance, TableInstance } from "element-plus";
import { ref } from "vue";
import { Delete as DeleteIcon, View as ViewIcon, Plus as PlusIcon, InfoFilled, Edit as EditIcon } from "@element-plus/icons-vue";
import IconPicker from "@/components/common/IconPicker.vue";
import { Icon } from "@iconify/vue";
import QueryPersistTip from "@/components/common/QueryPersistTip.vue";
import MenuManagerService from "@/views/core/service/MenuManagerService.ts";
import GenricRouteChooseModal from "@/soa/genric-route/GenricRouteChooseModal.vue";

const grcmRef = ref<InstanceType<typeof GenricRouteChooseModal>>();

//路由选择模态框查询参数
const grcmQuery = ref<string>("");

const openGRCM = () => {
  grcmRef.value?.openModal();
};

const listTableRef = ref<TableInstance>();
const modalFormRef = ref<FormInstance>();

/**
 * 菜单列表打包
 */
const { listForm, listExpand, listData, listLoading, fullMenuTree, loadList, resetList, removeList, listExpandToggle } =
  MenuManagerService.useMenuList(listTableRef);

/**
 * 菜单模态框打包
 */
const {
  modalVisible,
  modalLoading,
  modalMode,
  modalCurrentRow,
  modalForm,
  modalFormLabel,
  modalRules,
  menuTreeForSelect,
  openModal,
  resetModal,
  submitModal,
} = MenuManagerService.useMenuModal(modalFormRef, loadList, fullMenuTree);
</script>

<style scoped>
.list-container {
  padding: 0 20px;
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
</style>
