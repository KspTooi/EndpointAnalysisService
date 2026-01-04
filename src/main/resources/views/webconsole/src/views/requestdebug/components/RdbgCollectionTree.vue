<template>
  <div class="h-full w-full flex flex-col" @dragover.prevent @drop.prevent="onDrop(null, $event)">
    <div class="flex justify-between items-center pt-1.5 pr-3 pb-1.5 pl-3">
      <el-input placeholder="输入任意字符查询" size="small" clearable />
      <el-button type="primary" @click="modalCollectionCreateRef?.openModal" size="small" class="ml-2">新建</el-button>
      <!-- <div v-if="" class="root-drop-hint">拖拽到此处以移动到根级别</div> -->
    </div>

    <div
      class="h-full overflow-y-auto"
      @contextmenu.prevent="onContextmenu(null, $event)"
      v-loading="listLoading"
      element-loading-text="正在处理..."
    >
      <el-scrollbar>
        <el-empty v-if="!listLoading && listData.length === 0" description="暂无数据" />
        <div class="tree-list">
          <rdbg-collection-tree-item
            v-for="node in listData"
            :key="node.id"
            :node="node"
            :expanded-ids="expandedIds"
            :selected-ids="selectedIds"
            :drag-hover-zone="dragHoverZone"
            :drag-hover-target="dragHoverTarget"
            @on-click="toggleNode"
            @on-contextmenu="onContextmenu"
            @on-drag-start="onDragStart"
            @on-drag-over="onDragOver"
            @on-drag-leave="onDragLeave"
            @on-drag-drop="onDrop"
          />
        </div>
      </el-scrollbar>
    </div>

    <rdbg-collection-tree-right-menu
      ref="rightMenuRef"
      @on-create="onCreateCollection"
      @on-copy="onCopyCollection"
      @on-remove="onRemoveCollection"
    />

    <RdbgModalCollectionCreate ref="modalCollectionCreateRef" @on-success="loadList" />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import RdbgCollectonTreeService from "@/views/requestdebug/service/RdbgCollectonTreeService";
import RdbgCollectionTreeItem from "@/views/requestdebug/components/RdbgCollectionTreeItem.vue";
import type { GetCollectionTreeVo } from "@/views/requestdebug/api/CollectionApi";
import RdbgCollectionTreeRightMenu from "@/views/requestdebug/components/RdbgCollectionTreeRightMenu.vue";
import CollectionApi from "@/views/requestdebug/api/CollectionApi";
import { ElMessage, ElMessageBox } from "element-plus";
import RdbgModalCollectionCreate from "@/views/requestdebug/components/RdbgModalCollectionCreate.vue";
const rightMenuRef = ref<InstanceType<typeof RdbgCollectionTreeRightMenu>>();
const modalCollectionCreateRef = ref<InstanceType<typeof RdbgModalCollectionCreate>>();

defineEmits<{}>();

//列表功能打包
const { listData, listTotal, listFilter, listLoading, loadList } = RdbgCollectonTreeService.useCollectionTree();

//集合选择与展开功能打包
const { expandedIds, selectedIds, toggleNode, selectNode } = RdbgCollectonTreeService.useCollectionSelection();

/**
 * 拖拽到目标
 * @param target 目标
 * @param entries 拖拽的集合
 * @param zone 拖拽区域
 */
const onDrag = async (target: GetCollectionTreeVo, entries: GetCollectionTreeVo[], zone: "center" | "top" | "bottom") => {
  //执行拖拽移动
  let kind = 0;
  let targetId = null;

  if (zone === "center") {
    kind = 2;
  }

  if (zone === "top") {
    kind = 0;
  }

  if (zone === "bottom") {
    kind = 1;
  }

  if (target != null) {
    targetId = target.id;
  }

  await CollectionApi.moveCollection({
    nodeId: entries[0].id,
    targetId: targetId,
    kind: kind,
  });

  await loadList();
};

//拖拽功能打包
const { draggedCollection, dragHoverZone, dragHoverTarget, onDragStart, onDragOver, onDragLeave, onDrop } =
  RdbgCollectonTreeService.useCollectionTreeItemDrag(onDrag);

/**
 * 右键菜单打开
 * @param node 节点
 * @param event 事件
 */
const onContextmenu = (node: GetCollectionTreeVo, event: MouseEvent) => {
  rightMenuRef.value?.openRightMenu(event, node);
};

/**
 * 创建集合
 * @param node 节点
 */
const onCreateCollection = (node: GetCollectionTreeVo) => {
  let parentId = null;

  if (node) {
    //请求
    if (node.kind === 0) {
      parentId = node.parentId;
    }

    //分组
    if (node.kind === 1) {
      parentId = node.id;
    }
  }

  modalCollectionCreateRef.value?.openModal(parentId);
};

/**
 * 复制集合
 * @param node 节点
 */
const onCopyCollection = async (node: GetCollectionTreeVo) => {
  await CollectionApi.copyCollection({
    id: node.id,
  });

  await loadList();
};

/**
 * 删除集合
 * @param collections 集合
 */
const onRemoveCollection = async (collections: GetCollectionTreeVo[]) => {
  const hasBatch = collections.length > 1;

  let title = "删除请求";
  let text = "确定要删除吗?";

  if (hasBatch) {
    text = "确定要删除选中的" + collections.length + "个项目吗?";
    title = "删除多个项目";
  }

  if (!hasBatch) {
    const firstCollection = collections[0];
    const kind = firstCollection.kind;
    if (kind === 0) {
      title = "删除请求";
      text = "确定要删除请求 " + firstCollection.name + " 吗?";
    }
    if (kind === 1) {
      title = "删除分组";
      text = "确定要删除分组 " + firstCollection.name + " 吗?";
    }
  }

  try {
    await ElMessageBox.confirm(text, title, {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
  } catch (error) {
    return;
  }

  try {
    await CollectionApi.removeCollection({ ids: collections.map((collection) => collection.id) });
    await loadList();
  } catch (error) {
    ElMessage.error(error.message);
  }
};
</script>

<style scoped>
.empty-tip {
  padding: 20px;
  text-align: center;
  color: #909399;
}
</style>
