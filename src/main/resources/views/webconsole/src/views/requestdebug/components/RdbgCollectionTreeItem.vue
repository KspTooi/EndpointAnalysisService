<template>
  <div class="tree-node">
    <!-- 节点本身 -->
    <div
      class="tag-tree-item"
      :class="{
        'node-active': isGroup(node) ? activeCollectionId === node.id : activeCollectionId === node.id,
        'tag-item-active': isGroup(node) ? isExpanded(node.id) : false,
        'request-item': !isGroup(node),
        'drag-over-center': isDragHoverCenter,
        'drag-over-top': isDragHoverTop,
        'drag-over-bottom': isDragHoverBottom,
      }"
      draggable="true"
      @click="onClick(node)"
      @dblclick="onDoubleClick(node)"
      @contextmenu.prevent="onContextmenu"
      @dragstart="onDragStart"
      @dragover.prevent="onDragOver"
      @dragleave.prevent="onDragLeave"
      @drop.prevent="onDrop"
    >
      <!-- 请求组的显示 -->
      <div v-if="isGroup(node)" class="tag-tree-item-tag">
        <el-icon v-if="hasChildren(node)" class="expand-icon" @click.stop="onClick">
          <ArrowRight v-if="!isExpanded(node.id)" />
          <ArrowDown v-if="isExpanded(node.id)" />
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
        <div class="operation-name">
          {{ node.name }}
        </div>
      </div>
    </div>

    <!-- 子节点 -->
    <div v-if="isGroup(node) && hasChildren(node) && isExpanded(node.id)" class="operation-list">
      <CollectionTreeItem
        v-for="(child, childIndex) in node.children"
        :key="child.id"
        :node="child"
        :active-nodes="activeNodes"
        :active-collection-id="activeCollectionId"
        :parent-node="node"
        :child-index="childIndex"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { GetCollectionTreeVo } from "@/views/requestdebug/api/CollectionApi";
import { Folder, ArrowDown, ArrowRight } from "@element-plus/icons-vue";
import RdbgCollectionTreeItemService, {
  type CollectionDragEmitter,
} from "@/views/requestdebug/service/RdbgCollectionTreeItemService";

const props = defineProps<{
  node: GetCollectionTreeVo;
  activeNodes: string[];
  activeCollectionId: string | null;
  dragHoverZone: "center" | "top" | "bottom";
  dragHoverTarget: GetCollectionTreeVo;
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
const isExpanded = (nodeId: string) => RdbgCollectionTreeItemService.isExpanded(nodeId, props);
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
  background: linear-gradient(90deg, #409eff 0%, #66b3ff 100%);
  color: white;
  padding: 2px 2px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.3px;
  border: none;
  margin-left: 4px;
  min-width: 20px;
  text-align: center;
  line-height: 14px;
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.2),
    0 1px 2px rgba(64, 158, 255, 0.3);
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
  padding: 6px 12px;
  font-size: 14px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  margin: 0 0px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  user-select: none;
  min-height: 20px;
  border: 1px solid rgba(255, 255, 255, 0);
}

.drag-over-center {
  outline: 2px dashed #409eff;
  background: #f0f8ff;
}

.drag-over-top {
  box-shadow: inset 0 3px 0 0 #409eff;
}

.drag-over-bottom {
  box-shadow: inset 0 -3px 0 0 #409eff;
}

.request-item {
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border: 1px solid rgba(222, 226, 230, 0);
}

.request-item:hover {
  background: linear-gradient(135deg, #e8f4fd 0%, #f0f9ff 100%);
  border-color: #409eff;
}

.tag-item-active {
  background: linear-gradient(135deg, #f0f8ff 0%, #e6f3ff 100%) !important;
  border-color: #40b9ff !important;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15) !important;
}

.tag-tree-item:hover {
  background: linear-gradient(135deg, #f0f8ff 0%, #e6f3ff 100%);
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.tag-tree-item:active {
  background: linear-gradient(135deg, #e0f2ff 0%, #d0ebff 100%);
  transform: translateY(0);
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.2);
}

.expand-icon {
  cursor: pointer;
  transition: transform 0.3s ease;
  color: #909399;
  margin-right: 4px;
}

.expand-icon:hover {
  color: #409eff;
}

.operation-list {
  margin-left: 15px;
  margin-top: 2px;
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.operation-item-content {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.operation-method {
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  min-width: 45px;
  text-align: center;
  color: white;
}

.operation-name {
  flex: 1;
  color: #606266;
  font-weight: 500;
}

.method-get {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.method-post {
  background: linear-gradient(135deg, #409eff 0%, #66b3ff 100%);
}

.method-put {
  background: linear-gradient(135deg, #e6a23c 0%, #f0c78a 100%);
}

.method-delete {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
}

.method-patch {
  background: linear-gradient(135deg, #909399 0%, #b1b3b8 100%);
}

.method-head {
  background: linear-gradient(135deg, #909399 0%, #b1b3b8 100%);
}

.method-options {
  background: linear-gradient(135deg, #909399 0%, #b1b3b8 100%);
}

.method-unknown {
  background: linear-gradient(135deg, #909399 0%, #b1b3b8 100%);
}

.node-active {
  border: 1px solid #5dc7cd;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  background: linear-gradient(135deg, #f0ffff 0%, rgb(211, 250, 255) 100%) !important;
}
</style>
