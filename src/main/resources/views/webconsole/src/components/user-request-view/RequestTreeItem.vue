<template>
  <div class="tree-node">
    <!-- 节点本身 -->
    <div
      class="tag-tree-item"
      :class="{
        /* 'tag-item-active': isGroup(node) ? isExpanded(node.id) : isActive(node.id), */
        'node-active': isGroup(node) ? RequestTreeHolder().getActiveNodeId === node.id : RequestTreeHolder().getActiveNodeId === node.id,
        'tag-item-active': isGroup(node) ? isExpanded(node.id) : false,
        'request-item': !isGroup(node),
        'drag-over-center': dragHoverZone === 'center',
        'drag-over-top': dragHoverZone === 'top',
        'drag-over-bottom': dragHoverZone === 'bottom',
      }"
      @click="handleNodeClick(node)"
      @contextmenu.prevent="handleRightClick"
      draggable="true"
      @dragstart="handleDragStart"
      @dragover.prevent="handleDragOver"
      @dragleave="handleDragLeave"
      @drop.prevent="handleDrop"
    >
      <!-- 请求组的显示 -->
      <div v-if="isGroup(node)" class="tag-tree-item-tag">
        <el-icon v-if="hasChildren(node)" class="expand-icon" @click.stop="handleToggleNode(node.id)">
          <ArrowRight v-if="!isExpanded(node.id)" />
          <ArrowDown v-if="isExpanded(node.id)" />
        </el-icon>
        <el-icon class="folder-icon">
          <Folder v-show="node.children?.length === 0" />
          <!--          <IDeviconPlainHyperv />-->
          <!--          <IFlatColorIconsFolder  />-->
          <IStreamlineCyberNetwork v-show="node.children?.length !== 0" />
        </el-icon>
        <span class="node-name">{{ node.name }}</span>
      </div>

      <div class="item-indicator-filter" v-if="node.simpleFilterCount && node.simpleFilterCount > 0" :title="`启用了${node.simpleFilterCount}个基本过滤器`">
        <el-icon class="filter-icon">
          <!--          <Filter />-->
          <IDashiconsImageFilter />
        </el-icon>
        <span>{{ node.simpleFilterCount }}</span>
      </div>

      <div class="item-indicator-count" v-if="isGroup(node) && hasChildren(node)">
        {{ node.children?.length }}
      </div>

      <!-- 请求项的显示 -->
      <div v-if="!isGroup(node)" class="operation-item-content" style="overflow: hidden">
        <div v-if="node.method" class="operation-method" :class="getMethodClass(node.method)">
          {{ node.method }}
        </div>
        <div class="operation-name">
          {{ node.name }}
        </div>
      </div>
      <div class="item-indicator-light" v-if="node.linkForOriginalRequest === 1" :title="`已绑定原始请求`">
        <el-icon class="filter-icon">
          <IVaadinLink />
        </el-icon>
      </div>
    </div>

    <!-- 子节点 -->
    <div v-if="isGroup(node) && hasChildren(node) && isExpanded(node.id)" class="operation-list">
      <RequestTreeItem
        v-for="(child, childIndex) in node.children"
        :key="child.id"
        :node="child"
        :active-nodes="activeNodes"
        :active-request-id="activeRequestId"
        :parent-node="node"
        :child-index="childIndex"
        @toggle-node="$emit('toggle-node', $event)"
        @select-request="$emit('select-request', $event)"
        @right-click="$emit('right-click', $event)"
        @refresh-tree="$emit('refresh-tree')"
        @apply-tree="$emit('apply-tree', $event)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import UserRequestTreeApi from "@/api/requestdebug/UserRequestTreeApi.ts";
import type { GetUserRequestTreeVo, EditUserRequestTreeDto } from "@/api/requestdebug/UserRequestTreeApi.ts";
import { Folder, ArrowDown, ArrowRight, Document, Filter } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { RequestTreeHolder } from "@/store/RequestTreeHolder";

interface Props {
  node: GetUserRequestTreeVo;
  activeNodes: string[];
  activeRequestId: string | null;
  parentNode?: GetUserRequestTreeVo | undefined;
  childIndex?: number | undefined;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  "toggle-node": [nodeId: string];
  "select-request": [requestId: string];
  "right-click": [event: { node: GetUserRequestTreeVo; x: number; y: number }];
  "refresh-tree": []; //刷新树结构
  "apply-tree": [tree: GetUserRequestTreeVo[]]; //应用树结构
}>();

// 工具函数
const isGroup = (node: GetUserRequestTreeVo) => node.type === 0;
const hasChildren = (node: GetUserRequestTreeVo) => node.children && node.children.length > 0;
const isExpanded = (nodeId: string) => props.activeNodes.includes(nodeId);
const isActive = (nodeId: string) => props.activeRequestId === nodeId;

// 获取HTTP方法的CSS类名
const getMethodClass = (method: string | undefined) => {
  if (!method) return "";
  const knownMethods = ["get", "post", "put", "delete", "patch", "head", "options"];
  const lowerMethod = method.toLowerCase();
  return knownMethods.includes(lowerMethod) ? `method-${lowerMethod}` : "method-unknown";
};

const handleToggleNode = (nodeId: string) => {
  emit("toggle-node", nodeId);
};

const handleSelectRequest = (requestId: string) => {
  emit("select-request", requestId);
};

//处理Tree节点 点击
const handleNodeClick = (node: GetUserRequestTreeVo) => {
  //处理Group
  if (isGroup(node)) {
    if (hasChildren(node)) {
      handleToggleNode(node.id);
    }
    //更新RequstTree状态
    RequestTreeHolder().setActiveNodeId(node.id);
    RequestTreeHolder().setActiveNodeType("group");
    RequestTreeHolder().setActiveGroupId(node.groupId);
    RequestTreeHolder().setActiveRequestId(null);
  }

  //处理Request
  if (!isGroup(node)) {
    handleSelectRequest(node.requestId);
    RequestTreeHolder().setActiveNodeId(node.id);
    RequestTreeHolder().setActiveNodeType("request");
    RequestTreeHolder().setActiveRequestId(node.requestId);
    RequestTreeHolder().setActiveGroupId(null);
  }
};

const handleRightClick = (event: MouseEvent) => {
  emit("right-click", {
    node: props.node,
    x: event.clientX,
    y: event.clientY,
  });
};

// 拖拽交互状态
import { ref } from "vue";
const dragHoverZone = ref<"center" | "top" | "bottom" | null>(null);

// 序列化拖拽数据
const serializeDragData = (n: GetUserRequestTreeVo) =>
  JSON.stringify({
    id: n.id,
    parentId: n.parentId,
    type: n.type,
    name: n.name,
  });

// 事件: 开始拖拽
const handleDragStart = (event: DragEvent) => {
  if (!event.dataTransfer) return;
  event.dataTransfer.setData("application/json", serializeDragData(props.node));
  event.dataTransfer.effectAllowed = "move";
};

// 事件: 悬停
const handleDragOver = (event: DragEvent) => {
  const target = event.currentTarget as HTMLElement;
  const rect = target.getBoundingClientRect();
  const y = event.clientY - rect.top;
  const zoneHeight = rect.height;
  const threshold = Math.max(10, zoneHeight * 0.25);

  if (y < threshold) {
    dragHoverZone.value = "top";
  } else if (y > zoneHeight - threshold) {
    dragHoverZone.value = "bottom";
  } else {
    dragHoverZone.value = "center";
  }
};

// 事件: 离开
const handleDragLeave = () => {
  dragHoverZone.value = null;
};

// 事件: 放置
const handleDrop = async (event: DragEvent) => {
  const zone = dragHoverZone.value;
  dragHoverZone.value = null;
  try {
    if (!event.dataTransfer) return;
    const json = event.dataTransfer.getData("application/json");
    if (!json) return;
    const drag = JSON.parse(json) as { id: string; parentId: string | null; type: number; name: string };
    // 自身或同一元素不处理
    if (drag.id === props.node.id) return;

    const dragNodeId = drag.id;
    const targetNodeId = props.node.id;

    const moveParam = {
      keyword: null,
      nodeId: dragNodeId,
      targetId: targetNodeId,
      kind: 2, //0:顶部 1:底部 2:内部
    };

    //标记事件已被处理过
    event.dataTransfer.dropEffect = "none";

    // 拖拽到目标节点中心
    if (zone === "center") {
      moveParam.kind = 2;
      const ret = await UserRequestTreeApi.moveUserRequestTree(moveParam);
      ElMessage.success("已完成移动操作");
      emit("apply-tree", ret);
      return false;
    }

    //拖拽到目标上边缘
    if (zone === "top") {
      moveParam.kind = 0;
      const ret = await UserRequestTreeApi.moveUserRequestTree(moveParam);
      ElMessage.success("已完成移动操作");
      emit("apply-tree", ret);
      return false;
    }

    //拖拽到目标下边缘
    if (zone === "bottom") {
      moveParam.kind = 1;
      const ret = await UserRequestTreeApi.moveUserRequestTree(moveParam);
      ElMessage.success("已完成移动操作");
      emit("apply-tree", ret);
      return false;
    }
  } catch (e: any) {
    ElMessage.error(e?.message || "拖拽操作失败");
    return false;
  }
};
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

.item-indicator-filter {
  display: flex;
  align-items: center;
  background: linear-gradient(90deg, #ff8888 0%, #ee5a6f 100%);
  color: white;
  padding: 2px 6px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  border: none;
  margin-left: auto;
  gap: 3px;
}

.item-indicator-light {
  color: rgb(206, 206, 206) !important;
  font-size: 12px;
  margin-right: 7.5px;
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
  /*border: 1px solid #e9ecef;*/
  /*border-radius: 4px;*/
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
  /*border: 1px solid #dee2e6;*/
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
