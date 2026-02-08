<template>
  <div class="registry-tree-container" v-loading="loading">
    <div class="filter-wrapper pr-2 pl-2">
      <el-input
        v-model="filterText"
        placeholder="搜索注册表节点"
        clearable
        :prefix-icon="SearchIcon"
        @input="onFilterInput"
        size="small"
      />
    </div>

    <div class="action-wrapper pr-2 pl-2 mb-2">
      <el-button type="primary" size="small" :icon="PlusIcon" @click="handleAddRootNode">
        新建根节点
      </el-button>
    </div>

    <div class="tree-wrapper">
      <div v-if="showHeader" class="all-registry-node" :class="{ 'is-active': isAllSelected }" @click="handleSelectAll">
        <el-icon class="node-icon"><ListIcon /></el-icon>
        <span class="node-label">全部注册表</span>
      </div>
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
        @node-contextmenu="handleNodeContextMenu"
        class="custom-tree"
      >
        <template #default="{ node, data }">
          <span class="custom-tree-node">
            <el-icon class="node-icon">
              <FolderIcon />
            </el-icon>
            <span class="node-label">{{ node.label }}</span>
          </span>
        </template>
      </el-tree>
    </div>

    <!-- 右键菜单 -->
    <el-dropdown
      ref="contextMenuRef"
      trigger="contextmenu"
      :virtual-triggering="true"
      :virtual-ref="triggerRef"
      @command="handleContextMenuCommand"
    >
      <span></span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="addChild" :icon="PlusIcon">新建子节点</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <!-- 创建节点模态框 -->
    <el-dialog
      v-model="modalVisible"
      title="新建节点"
      width="500px"
      :close-on-click-modal="false"
      @close="resetModal"
    >
      <el-form
        v-if="modalVisible"
        ref="modalFormRef"
        :model="modalForm"
        :rules="modalRules"
        label-width="100px"
      >
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
import { ref, markRaw, onMounted, computed } from "vue";
import type { ElTree, ElDropdown } from "element-plus";
import { Search, List, Folder, Plus } from "@element-plus/icons-vue";
import type { GetRegistryNodeTreeVo } from "@/views/core/api/RegistryApi";
import RegistryNodeTreeService from "@/views/core/service/RegistryNodeTreeService";
import type { FormInstance } from "element-plus";

const SearchIcon = markRaw(Search);
const ListIcon = markRaw(List);
const FolderIcon = markRaw(Folder);
const PlusIcon = markRaw(Plus);

const emit = defineEmits<{
  (e: "on-select", node: GetRegistryNodeTreeVo | null): void;
}>();

const props = withDefaults(
  defineProps<{
    showHeader?: boolean;
  }>(),
  {
    showHeader: true,
  }
);

const treeRef = ref<InstanceType<typeof ElTree>>();
const contextMenuRef = ref<InstanceType<typeof ElDropdown>>();
const modalFormRef = ref<FormInstance>();
const isAllSelected = ref(true);
const currentSelectedKey = ref<string | number | null>(null);
const contextMenuNode = ref<GetRegistryNodeTreeVo | null>(null);
const triggerRef = computed(() => contextMenuRef.value?.$el);

const { treeData, loading, filterText, loadTreeData, onSelectNode } = RegistryNodeTreeService.useRegistryNodeTree();

const _loadTreeData = () => loadTreeData();

const { modalVisible, modalLoading, modalForm, modalRules, openModal, submitModal, resetModal } =
  RegistryNodeTreeService.useNodeModal(modalFormRef, _loadTreeData);

const defaultProps = {
  children: "children",
  label: "label",
};

const handleSelectAll = () => {
  isAllSelected.value = true;
  currentSelectedKey.value = null;
  treeRef.value?.setCurrentKey(null);
  emit("on-select", null);
  onSelectNode(null);
};

const onFilterInput = (val: string) => {
  treeRef.value?.filter(val);
};

const filterNode = (value: string, data: GetRegistryNodeTreeVo) => {
  if (!value) return true;
  return data.label.includes(value);
};

const handleNodeClick = (data: GetRegistryNodeTreeVo) => {
  isAllSelected.value = false;
  currentSelectedKey.value = data.id;
  emit("on-select", data);
  onSelectNode(data);
};

const handleNodeContextMenu = (event: MouseEvent, data: any, node: any) => {
  event.preventDefault();
  contextMenuNode.value = data;
  if (contextMenuRef.value) {
    contextMenuRef.value.handleOpen();
  }
};

const handleContextMenuCommand = (command: string) => {
  if (command === "addChild") {
    openModal(contextMenuNode.value);
  }
};

const handleAddRootNode = () => {
  openModal(null);
};

const reset = () => {
  handleSelectAll();
};

onMounted(() => {
  loadTreeData();
});

defineExpose({
  reset,
  loadTreeData,
});
</script>

<style scoped>
.registry-tree-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: var(--el-bg-color);
  box-sizing: border-box;
}

.filter-wrapper {
  margin-bottom: 8px;
}

.tree-wrapper {
  flex: 1;
  overflow-y: auto;
}

.custom-tree {
  background: transparent;
}

.all-registry-node {
  display: flex;
  align-items: center;
  height: 28px;
  padding-left: 27.5px;
  cursor: pointer;
  margin-bottom: 1px;
  transition: background-color 0.2s;
  font-size: 13px;
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

.custom-tree-node {
  display: flex;
  align-items: center;
  font-size: 13px;
}

.node-icon {
  margin-right: 6px;
  font-size: 14px;
  color: var(--el-color-primary);
}

.node-label {
  color: var(--el-text-color-regular);
}

:deep(.el-input__wrapper) {
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
  color: var(--el-color-primary);
  font-weight: bold;
}

:deep(.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content .node-label) {
  color: var(--el-color-primary);
}

:deep(.el-tree-node__expand-icon) {
  font-size: 16px;
}
</style>
