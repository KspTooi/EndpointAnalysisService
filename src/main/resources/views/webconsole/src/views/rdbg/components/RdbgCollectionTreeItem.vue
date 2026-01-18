<template>
  <div class="tree-node">
    <!-- 节点本身 -->
    <div
      class="tag-tree-item"
      :class="{
        'node-active': rdbgStore.isSelected(node),
        'request-item': !isGroup(node),
        'drag-over-center': isDragHoverCenter,
        'drag-over-top': isDragHoverTop,
        'drag-over-bottom': isDragHoverBottom,
      }"
      draggable="true"
      @click="onClick(node)"
      @dblclick.prevent.stop="onDoubleClick(node)"
      @contextmenu.prevent.stop="onContextmenu"
      @dragstart="onDragStart"
      @dragover.prevent.stop="onDragOver"
      @dragleave.prevent.stop="onDragLeave"
      @drop.prevent.stop="onDrop"
    >
      <!-- 请求组的显示 -->
      <div v-if="isGroup(node)" class="tag-tree-item-tag">
        <el-icon v-if="hasChildren(node)" class="expand-icon" @click.stop="onClick(node)">
          <ArrowRight v-if="!isExpanded(node)" />
          <ArrowDown v-if="isExpanded(node)" />
        </el-icon>
        <el-icon class="folder-icon">
          <Folder v-show="node.children?.length === 0" />
          <IStreamlineCyberNetwork v-show="node.children?.length !== 0" />
        </el-icon>
        <span class="node-name">{{ node.name }}</span>
      </div>

      <div class="item-indicator-count" v-if="isGroup(node) && hasChildren(node)">
        {{ node.children?.length }}
      </div>

      <!-- 请求项的显示 -->
      <div v-if="!isGroup(node)" class="operation-item-content" style="overflow: hidden">
        <div v-if="node.reqMethod !== undefined" class="operation-method" :class="methodClass">
          {{ methodName }}
        </div>
        <div class="operation-name flex items-center gap-1">
          <span v-if="rdbgStore.getUncommittedCollections.find((c) => c.id === node.id)" class="text-[#07998b]"
            ><ILineMdUploadingLoop />
          </span>
          {{ node.name }}
        </div>
      </div>
    </div>

    <!-- 子节点 -->
    <div v-if="isGroup(node) && hasChildren(node) && isExpanded(node)" class="operation-list">
      <RdbgCollectionTreeItem
        v-for="(child, childIndex) in node.children"
        :key="child.id"
        :node="child"
        :drag-hover-zone="dragHoverZone"
        :drag-hover-target="dragHoverTarget"
        :parent-node="node"
        :child-index="childIndex"
        @on-click="(collection) => emit('on-click', collection)"
        @on-dblclick="(collection) => emit('on-dblclick', collection)"
        @on-contextmenu="(collection, event) => emit('on-contextmenu', collection, event)"
        @on-drag-start="(collection, event) => emit('on-drag-start', collection, event)"
        @on-drag-over="(collection, event) => emit('on-drag-over', collection, event)"
        @on-drag-leave="(collection, event) => emit('on-drag-leave', collection, event)"
        @on-drag-drop="(collection, event) => emit('on-drag-drop', collection, event)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { GetCollectionTreeVo } from "@/views/rdbg/api/CollectionApi";
import { Folder, ArrowDown, ArrowRight } from "@element-plus/icons-vue";
import RdbgCollectionTreeItemService, {
  type CollectionDragEmitter,
} from "@/views/rdbg/service/RdbgCollectionTreeItemService";
import { useRdbgStore } from "@/views/rdbg/service/RdbgStore";
const rdbgStore = useRdbgStore();

const props = defineProps<{
  //当前节点
  node: GetCollectionTreeVo;

  //拖拽悬停状态
  dragHoverZone: "center" | "top" | "bottom" | null;
  dragHoverTarget: GetCollectionTreeVo | null;

  //嵌套组件状态
  parentNode?: GetCollectionTreeVo | undefined;
  childIndex?: number | undefined;
}>();
const emit = defineEmits<CollectionDragEmitter>();

//集合树项事件发射器打包
const { onClick, onDoubleClick, onContextmenu, onDragStart, onDragOver, onDragLeave, onDrop } =
  RdbgCollectionTreeItemService.useCollectionTreeItemEmitter(props.node, emit);

//集合树项拖拽悬停状态计算打包
const { isDragHoverTop, isDragHoverCenter, isDragHoverBottom } =
  RdbgCollectionTreeItemService.useCollectionItemDragHover(props);

//集合树项计算属性打包
const { methodName, methodClass } = RdbgCollectionTreeItemService.useCollectionItemComputed(props);

//是否是组
const isGroup = (node: GetCollectionTreeVo) => RdbgCollectionTreeItemService.isGroup(node);
//是否有子节点
const hasChildren = (node: GetCollectionTreeVo) => RdbgCollectionTreeItemService.hasChildren(node);
//是否展开
const isExpanded = (node: GetCollectionTreeVo) => rdbgStore.isExpanded(node);
</script>

<style scoped>
.tag-tree-item-tag {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 6px;
  font-weight: 500;
  color: #2c3e50;
  flex: 1;
}

.tag-tree-item-tag .el-icon {
  color: #409eff;
  font-size: 16px;
}

.folder-icon {
  color: #30aabf !important;
}

.item-indicator-count {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #409eff;
  color: white;
  padding: 0 6px;
  font-size: 11px;
  font-weight: 700;
  margin-left: 4px;
  min-width: 20px;
  height: 18px;
  line-height: 18px;
}

.node-name {
  flex: 1;
  color: #2c3e50;
}

.tag-tree-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  font-size: 14px;
  background: #ffffff;
  margin: 1px 0;
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
  min-height: 32px;
  border: 1px solid transparent;
  border-right: none !important;
  border-left: none !important;
}

.drag-over-center {
  background: #f0f7ff !important;
  border: 1px dashed #409eff !important;
}

.drag-over-top {
  border-top: 2px solid #409eff !important;
}

.drag-over-bottom {
  border-bottom: 2px solid #409eff !important;
}

.request-item {
  background: #ffffff;
}

.request-item:hover {
  background: #f5f7fa;
}

.tag-item-active {
  background: #f0f7ff !important;
}

.tag-tree-item:hover {
  background: #f5f7fa;
}

.tag-tree-item:active {
  background: #ecf5ff;
}

.expand-icon {
  cursor: pointer;
  transition: transform 0.2s ease;
  color: #909399;
  margin-right: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.expand-icon:hover {
  color: #409eff;
}

.operation-list {
  margin-left: 18px;
  border-left: 1px solid #e4e7ed;
  padding-left: 4px;
}

.operation-item-content {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.operation-method {
  padding: 0 4px;
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  min-width: 42px;
  max-width: 42px;
  overflow: hidden;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  text-align: left;
}

.operation-name {
  flex: 1;
  color: #606266;
  font-size: 13px;
}

.method-get {
  background: #67c23a;
}

.method-post {
  background: #e6a23c;
}

.method-put {
  background: #409eff;
}

.method-delete {
  background: #f56c6c;
}

.method-patch {
  background: #6b0280;
}

.method-head {
  background: #049735;
}

.method-options {
  background: #970472;
}

.method-unknown {
  background: #909399;
}

.node-active {
  background: #ecf5ff !important;
  border-color: #b3d8ff !important;
  color: #409eff !important;
}

.node-active .node-name,
.node-active .operation-name {
  color: #409eff !important;
}
</style>
