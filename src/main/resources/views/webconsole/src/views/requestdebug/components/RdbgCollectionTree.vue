<template>
  <div class="collection-tree-container">
    <div class="flex justify-between items-center pt-1.5 pr-3 pb-1.5 pl-3">
      <el-input placeholder="输入任意字符查询" size="small" clearable />
      <el-button type="primary" @click="$emit('on-create-collection')" size="small" class="ml-2">新建</el-button>
      <!-- <div v-if="" class="root-drop-hint">拖拽到此处以移动到根级别</div> -->
    </div>

    <div>
      <div v-if="listLoading" class="loading-mask">加载中...</div>
      <div v-if="!listLoading && listData.length === 0" class="empty-tip">暂无数据</div>
      <div v-if="!listLoading && listData.length > 0" class="tree-list">
        <rdbg-collection-tree-item
          v-for="node in listData"
          :key="node.id"
          :node="node"
          :active-nodes="activeNodes"
          :active-collection-id="activeCollectionId"
          :drag-hover-zone="dragHoverZone"
          :drag-hover-target="dragHoverTarget"
          @on-drag-start="onDragStart"
          @on-drag-over="onDragOver"
          @on-drag-leave="onDragLeave"
          @on-drag-drop="onDrop"
        />
      </div>
    </div>
    <rdbg-collection-tree-right-menu ref="rightMenuRef" />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import RdbgCollectonTreeService from "@/views/requestdebug/service/RdbgCollectonTreeService";
import RdbgCollectionTreeItem from "@/views/requestdebug/components/RdbgCollectionTreeItem.vue";
import type { GetCollectionTreeVo } from "@/views/requestdebug/api/CollectionApi";
import RdbgCollectionTreeRightMenu from "@/views/requestdebug/components/RdbgCollectionTreeRightMenu.vue";
import RdbgCollectionTreeItemService from "../service/RdbgCollectionTreeItemService";
const rightMenuRef = ref<InstanceType<typeof RdbgCollectionTreeRightMenu>>();

defineEmits<{
  (e: "on-create-collection"): void;
}>();

//列表功能打包
const { listData, listTotal, listFilter, listLoading, loadList } = RdbgCollectonTreeService.useCollectionTree();

//拖拽功能打包
const { draggedCollection, dragHoverZone, dragHoverTarget, onDragStart, onDragOver, onDragLeave, onDrop } =
  RdbgCollectonTreeService.useCollectionTreeItemDrag();

//激活的节点ID列表(展开状态)
const activeNodes = ref<string[]>([]);
//激活的集合ID
const activeCollectionId = ref<string | null>(null);

//切换节点展开/收起
const handleToggleNode = (nodeId: string) => {
  const index = activeNodes.value.indexOf(nodeId);
  if (index > -1) {
    activeNodes.value.splice(index, 1);
    return;
  }
  activeNodes.value.push(nodeId);
};

//选择集合
const handleSelectCollection = (collectionId: string) => {
  activeCollectionId.value = collectionId;
};

//刷新树
const handleRefreshTree = () => {
  loadList();
};

//应用树结构
const handleApplyTree = (tree: GetCollectionTreeVo[]) => {
  listData.value = tree;
};
</script>

<style scoped>
.collection-tree-container {
  width: 100%;
  height: 100%;
  overflow-y: auto;
}

.loading-mask {
  padding: 20px;
  text-align: center;
  color: #909399;
}

.empty-tip {
  padding: 20px;
  text-align: center;
  color: #909399;
}

.tree-list {
  padding: 4px;
}
</style>
