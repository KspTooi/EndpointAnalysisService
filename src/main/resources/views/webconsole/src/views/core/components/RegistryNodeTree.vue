<template>
  <div class="flex flex-col h-full bg-[var(--el-bg-color)] box-border">
    <!-- 搜索栏 -->
    <div class="px-2 mb-2">
      <el-input
        v-model="filterText"
        placeholder="搜索注册表节点"
        clearable
        :prefix-icon="SearchIcon"
        @input="onFilterInput"
        size="small"
        class="custom-input"
      />
    </div>

    <!-- 树容器 -->
    <div class="flex-1 overflow-y-auto">
      <!-- 全部节点行 -->
      <div
        v-if="showHeader"
        class="all-registry-node group flex items-center justify-between h-[28px] pl-[27.5px] pr-2 cursor-pointer mb-[1px] transition-colors duration-200 text-[13px]"
        :class="{ 'is-active': isAllSelected }"
        @click="handleSelectAll"
      >
        <div class="flex items-center">
          <el-icon class="mr-1.5 text-[14px]"><ListIcon /></el-icon>
          <span class="node-label text-[var(--el-text-color-regular)]">全部注册表节点</span>
        </div>
        <div class="hidden group-hover:flex items-center text-[20px] font-bold" @click.stop="openModal(null)">
          <el-icon class="p-0.5 rounded hover:bg-[var(--el-color-primary-light-7)] cursor-pointer" title="新建根节点">
            <PlusIcon />
          </el-icon>
        </div>
      </div>

      <!-- 核心树组件 -->
      <el-tree
        ref="treeRef"
        :data="treeData"
        :props="defaultProps"
        :filter-node-method="filterNode"
        :expand-on-click-node="false"
        node-key="id"
        highlight-current
        default-expand-all
        @node-click="handleNodeClick"
        class="custom-tree"
      >
        <template #default="{ node, data }">
          <span class="custom-tree-node group flex-1 flex items-center justify-between pr-2 text-[13px]">
            <div class="flex items-center">
              <el-icon class="mr-1.5 text-[14px]">
                <FolderIcon />
              </el-icon>
              <span class="text-[var(--el-text-color-regular)]">{{ data.nkey }}</span>
            </div>
            <div class="hidden group-hover:flex items-center text-[20px] font-bold">
              <el-icon
                class="p-0.5 rounded hover:bg-[var(--el-color-primary-light-7)] cursor-pointer"
                title="新建子节点"
                @click.stop="openModal(data)"
              >
                <PlusIcon />
              </el-icon>
            </div>
          </span>
        </template>
      </el-tree>
    </div>

    <!-- 创建节点模态框 -->
    <el-dialog v-model="modalVisible" title="新建节点" width="500px" :close-on-click-modal="false" @close="resetModal">
      <el-form v-if="modalVisible" ref="modalFormRef" :model="modalForm" :rules="modalRules" label-width="100px">
        <el-form-item label="节点Key" prop="nkey">
          <el-input v-model="modalForm.nkey" placeholder="请输入节点Key（字母、数字、下划线或中划线）" />
        </el-form-item>
        <el-form-item label="节点标签" prop="label">
          <el-input v-model="modalForm.label" placeholder="请输入节点标签" />
        </el-form-item>
        <el-form-item label="排序" prop="seq">
          <el-input-number v-model="modalForm.seq" :min="0" />
        </el-form-item>
        <el-form-item label="说明" prop="remark">
          <el-input v-model="modalForm.remark" type="textarea" placeholder="请输入说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="modalVisible = false">取消</el-button>
          <el-button type="primary" @click="submitModal" :loading="modalLoading">创建</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, markRaw, onMounted } from "vue";
import type { ElTree } from "element-plus";
import { Search, List, Folder, Plus } from "@element-plus/icons-vue";
import type { GetRegistryNodeTreeVo } from "@/views/core/api/RegistryApi";
import RegistryNodeTreeService from "@/views/core/service/RegistryNodeTreeService";
import type { FormInstance } from "element-plus";

// 注册图标
const SearchIcon = markRaw(Search);
const ListIcon = markRaw(List);
const FolderIcon = markRaw(Folder);
const PlusIcon = markRaw(Plus);

// 事件定义
const emit = defineEmits<{
  (e: "on-select", node: GetRegistryNodeTreeVo | null): void; // 节点选中回调
}>();

// 组件属性
const props = withDefaults(
  defineProps<{
    showHeader?: boolean; // 是否显示“全部节点”顶栏
  }>(),
  {
    showHeader: true,
  }
);

// DOM & 组件引用
const treeRef = ref<InstanceType<typeof ElTree>>();
const modalFormRef = ref<FormInstance>();

// 组件内部状态
const isAllSelected = ref(true); // 是否处于“全部节点”状态
const currentSelectedKey = ref<string | number | null>(null); // 当前树内选中的 Key

// 核心业务 Hook：节点树逻辑
const { treeData, loading, filterText, loadTreeData, onSelectNode } = RegistryNodeTreeService.useRegistryNodeTree();

// 辅助方法：用于刷新后重新加载
const _loadTreeData = () => loadTreeData();

// 核心业务 Hook：节点新增模态框逻辑
const { modalVisible, modalLoading, modalForm, modalRules, openModal, submitModal, resetModal } =
  RegistryNodeTreeService.useNodeModal(modalFormRef, _loadTreeData);

// ElTree 基础配置
const defaultProps = {
  children: "children",
  label: "label",
};

/**
 * 处理“全部注册表节点”点击事件
 * 清空树选中状态，通知父组件加载全部数据
 */
const handleSelectAll = () => {
  isAllSelected.value = true;
  currentSelectedKey.value = null;
  treeRef.value?.setCurrentKey(null);
  emit("on-select", null);
  onSelectNode(null);
};

/**
 * 实时过滤搜索输入
 */
const onFilterInput = (val: string) => {
  treeRef.value?.filter(val);
};

/**
 * Element Plus 树搜索逻辑
 * 支持按 Key 或 Label 模糊搜索
 */
const filterNode = (value: string, data: GetRegistryNodeTreeVo) => {
  if (!value) return true;
  return (data.nkey || "").includes(value) || (data.label || "").includes(value);
};

/**
 * 处理常规树节点点击事件
 */
const handleNodeClick = (data: GetRegistryNodeTreeVo) => {
  isAllSelected.value = false;
  currentSelectedKey.value = data.id;
  emit("on-select", data);
  onSelectNode(data);
};

// 生命周期：挂载后加载树
onMounted(() => {
  loadTreeData();
});

// 暴露 API 给父组件使用
defineExpose({ loadTreeData });
</script>

<style scoped>
/* 无法使用 Tailwind 覆盖的 Element Plus 深度样式或变量交互 */
.custom-tree {
  background: transparent;
}

/* 统一图标颜色，确保非选中状态下也有颜色 */
.all-registry-node .el-icon,
.custom-tree-node .el-icon {
  color: var(--el-color-primary);
}

.all-registry-node:hover {
  background-color: var(--el-color-primary-light-9);
}

.all-registry-node.is-active {
  background-color: var(--el-color-primary-light-8);
  color: var(--el-color-primary);
  font-weight: bold;
}

.all-registry-node.is-active .node-label {
  color: var(--el-color-primary);
}

:deep(.custom-input .el-input__wrapper) {
  border-radius: 0;
}

:deep(.el-tree-node__content) {
  height: 28px;
  border-radius: 0;
  margin-bottom: 1px;
}

:deep(.el-tree-node__content:hover) {
  background-color: var(--el-color-primary-light-9);
}

:deep(.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content) {
  background-color: var(--el-color-primary-light-8);
  font-weight: bold;
}

/* 确保选中节点的图标和文字都变为 Primary 颜色 */
:deep(.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content) .el-icon,
:deep(.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content) span {
  color: var(--el-color-primary) !important;
}

:deep(.el-tree-node__expand-icon) {
  font-size: 16px;
}
</style>
