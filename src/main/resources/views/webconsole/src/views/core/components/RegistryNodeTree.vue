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
        class="custom-tree"
      >
        <template #default="{ node }">
          <span class="custom-tree-node">
            <el-icon class="node-icon">
              <FolderIcon />
            </el-icon>
            <span class="node-label">{{ node.label }}</span>
          </span>
        </template>
      </el-tree>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, markRaw, onMounted } from "vue";
import type { ElTree } from "element-plus";
import { Search, List, Folder } from "@element-plus/icons-vue";
import type { GetRegistryNodeTreeVo } from "@/views/core/api/RegistryApi";
import RegistryNodeTreeService from "@/views/core/service/RegistryNodeTreeService";

const SearchIcon = markRaw(Search);
const ListIcon = markRaw(List);
const FolderIcon = markRaw(Folder);

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
const isAllSelected = ref(true);
const currentSelectedKey = ref<string | number | null>(null);
const { treeData, loading, filterText, loadTreeData, onSelectNode } = RegistryNodeTreeService.useRegistryNodeTree();

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
