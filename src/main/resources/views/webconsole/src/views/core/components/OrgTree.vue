<template>
  <div class="org-tree-container" v-loading="loading">
    <div class="filter-wrapper pr-2 pl-2">
      <el-input
        v-model="filterText"
        placeholder="搜索机构/部门"
        clearable
        :prefix-icon="SearchIcon"
        @input="onFilterInput"
        size="small"
      />
    </div>

    <div class="tree-wrapper">
      <div v-if="showHeader && !multiple" class="all-org-node" :class="{ 'is-active': isAllSelected }" @click="handleSelectAll">
        <el-icon class="node-icon"><OfficeBuildingIcon /></el-icon>
        <span class="node-label">全部公司</span>
      </div>
      <el-tree
        ref="treeRef"
        :data="treeData"
        :props="defaultProps"
        :filter-node-method="filterNode"
        :expand-on-click-node="false"
        :show-checkbox="multiple"
        node-key="id"
        highlight-current
        default-expand-all
        @node-click="handleNodeClick"
        @check="handleCheckChange"
        class="custom-tree"
      >
        <template #default="{ node, data }">
          <span class="custom-tree-node" :class="{ 'is-disabled': isNodeDisabled(data) }">
            <el-icon class="node-icon">
              <component :is="data.kind === 1 ? OfficeBuildingIcon : ManagementIcon" />
            </el-icon>
            <span class="node-label">{{ node.label }}</span>
          </span>
        </template>
      </el-tree>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, markRaw, onMounted } from "vue";
import type { ElTree } from "element-plus";
import { Search, OfficeBuilding, Management } from "@element-plus/icons-vue";
import type { GetOrgTreeVo } from "@/views/core/api/OrgApi";
import OrgTreeService from "@/views/core/service/OrgTreeService";

const SearchIcon = markRaw(Search);
const OfficeBuildingIcon = markRaw(OfficeBuilding);
const ManagementIcon = markRaw(Management);

const emit = defineEmits<{
  (e: "on-select", node: GetOrgTreeVo | null): void;
  (e: "on-check", nodes: GetOrgTreeVo[]): void;
}>();

const props = withDefaults(
  defineProps<{
    showHeader?: boolean;
    multiple?: boolean;
    defaultCheckedKeys?: string[];
    selectKind?: 'all' | 'root' | 'dept';
  }>(),
  {
    showHeader: true,
    multiple: false,
    defaultCheckedKeys: () => [],
    selectKind: 'all',
  }
);

const treeRef = ref<InstanceType<typeof ElTree>>();
const isAllSelected = ref(true);
const currentSelectedKey = ref<string | number | null>(null);
const { treeData, loading, filterText, loadTreeData, onSelectOrg } = OrgTreeService.useOrgTree();

const defaultProps = {
  children: "children",
  label: "name",
};

const handleSelectAll = () => {
  isAllSelected.value = true;
  currentSelectedKey.value = null;
  treeRef.value?.setCurrentKey(null);
  emit("on-select", null);
  onSelectOrg(null);
};

const onFilterInput = (val: string) => {
  treeRef.value?.filter(val);
};

const filterNode = (value: string, data: GetOrgTreeVo) => {
  if (!value) return true;
  return data.name.includes(value);
};

const isNodeDisabled = (data: GetOrgTreeVo) => {
  if (props.selectKind === 'all') return false;
  if (props.selectKind === 'root') return data.kind !== 1;
  if (props.selectKind === 'dept') return data.kind === 1;
  return false;
};

const handleNodeClick = (data: GetOrgTreeVo) => {
  if (props.multiple) return;
  if (isNodeDisabled(data)) {
    treeRef.value?.setCurrentKey(currentSelectedKey.value);
    return;
  }
  isAllSelected.value = false;
  currentSelectedKey.value = data.id;
  emit("on-select", data);
  onSelectOrg(data);
};

const handleCheckChange = () => {
  if (!treeRef.value) return;
  const checkedNodes = treeRef.value.getCheckedNodes() as GetOrgTreeVo[];
  emit("on-check", checkedNodes);
};

const reset = () => {
  handleSelectAll();
  if (props.multiple && treeRef.value) {
    treeRef.value.setCheckedKeys([]);
  }
};

const getCheckedNodes = () => {
  return treeRef.value?.getCheckedNodes() as GetOrgTreeVo[];
};

const setCheckedKeys = (keys: string[]) => {
  treeRef.value?.setCheckedKeys(keys);
};

onMounted(() => {
  loadTreeData();
});

defineExpose({
  reset,
  getCheckedNodes,
  setCheckedKeys,
});
</script>

<style scoped>
.org-tree-container {
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

.all-org-node {
  display: flex;
  align-items: center;
  height: 28px;
  padding-left: 27.5px; /* Align with tree nodes (expand icon width) */
  cursor: pointer;
  margin-bottom: 1px;
  transition: background-color 0.2s;
  font-size: 13px;
}

.all-org-node:hover {
  background-color: var(--el-color-primary-light-9);
}

.all-org-node.is-active {
  background-color: var(--el-color-primary-light-8);
  color: var(--el-color-primary);
  font-weight: bold;
}

.all-org-node.is-active .node-label {
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

.custom-tree-node.is-disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.custom-tree-node.is-disabled .node-icon,
.custom-tree-node.is-disabled .node-label {
  color: var(--el-text-color-disabled);
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

:deep(.el-tree-node__content:has(.is-disabled):hover) {
  background-color: transparent;
  cursor: not-allowed;
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
