<template>
  <div class="tree-node">
    <!-- 节点本身 -->
    <div
      class="tag-tree-item"
      :class="{
        'tag-item-active': isGroup(node) ? isExpanded(node.id) : isActive(node.id),
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
          <ArrowDown v-else />
        </el-icon>
        <el-icon class="folder-icon">
          <Folder />
        </el-icon>
        <span class="node-name">{{ node.name }}</span>
      </div>

      <!-- 请求项的显示 -->
      <div v-else class="operation-item-content" style="overflow: hidden">
        <div v-if="node.method" class="operation-method" :class="getMethodClass(node.method)">
          {{ node.method }}
        </div>
        <div class="operation-name">
          {{ node.name }}
        </div>
      </div>

      <div v-if="isGroup(node) && hasChildren(node)" class="tag-tree-item-count">
        {{ node.children?.length }}
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
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import UserRequestTreeApi from "@/api/UserRequestTreeApi";
import type { GetUserRequestTreeVo, EditUserRequestTreeDto } from "@/api/UserRequestTreeApi";
import { Folder, ArrowDown, ArrowRight, Document } from "@element-plus/icons-vue";
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
  "refresh-tree": [];
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
    RequestTreeHolder().setActiveGroupId(node.id);
    RequestTreeHolder().setActiveRequestId(null);
  }

  //处理Request
  if (!isGroup(node)) {
    handleSelectRequest(node.id);
    RequestTreeHolder().setActiveNodeId(node.id);
    RequestTreeHolder().setActiveNodeType("request");
    RequestTreeHolder().setActiveRequestId(node.id);
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

    // 拖拽到中心：若目标是分组，则加入子组
    if (zone === "center" && isGroup(props.node)) {
      if (drag.id === props.node.id) return;
      const dtoCenter: EditUserRequestTreeDto = {
        id: drag.id,
        parentId: props.node.id,
        type: drag.type,
        name: drag.name,
        seq: (props.node.children?.length || 0) + 1,
      };
      await UserRequestTreeApi.editUserRequestTree(dtoCenter);
      ElMessage.success("已移动到子组");
      emit("refresh-tree");
      return;
    }

    // 拖拽到上/下边缘：进行排序（目标同级之前/之后）
    const parentId = props.parentNode ? props.parentNode.id : null;
    // 若拖拽进来的是分组（type=0）且当前目标是请求（type=1），不允许跨类型排序为安全起见
    if (!isGroup(props.node) && drag.type === 0) {
      return;
    }
    let seq = (props.childIndex ?? 0) + 1;

    if (zone === "top") {
      seq = (props.childIndex ?? 0) - 1;
      console.log("top", seq);
    }
    if (zone === "bottom") {
      seq = (props.childIndex ?? 0) + 1;
      console.log("bottom", seq);
    }

    const dtoSort: EditUserRequestTreeDto = {
      id: drag.id,
      parentId,
      type: drag.type,
      name: drag.name,
      seq,
    };
    await UserRequestTreeApi.editUserRequestTree(dtoSort);
    ElMessage.success("排序已更新");
    emit("refresh-tree");
  } catch (e: any) {
    ElMessage.error(e?.message || "拖拽操作失败");
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
  color: #409eff !important;
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
  border: 1px solid #e9ecef;
  border-radius: 4px;
  margin: 4px 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  user-select: none;
  min-height: 20px;
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
  border: 1px solid #dee2e6;
}

.request-item:hover {
  background: linear-gradient(135deg, #e8f4fd 0%, #f0f9ff 100%);
  border-color: #409eff;
}

.tag-item-active {
  background: linear-gradient(135deg, #f0f8ff 0%, #e6f3ff 100%) !important;
  border-color: #40b9ff !important;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15) !important;
  transform: translateY(-1px) !important;
}

.tag-tree-item:hover {
  background: linear-gradient(135deg, #f0f8ff 0%, #e6f3ff 100%);
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  transform: translateY(-1px);
}

.tag-tree-item:active {
  background: linear-gradient(135deg, #e0f2ff 0%, #d0ebff 100%);
  transform: translateY(0);
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.2);
}

.tag-tree-item-count {
  background: linear-gradient(135deg, #409eff 0%, #66b3ff 100%);
  color: white;
  padding: 2px 4px;
  border-radius: 16px;
  font-size: 11px;
  font-weight: 600;
  min-width: 20px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.3);
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
  margin-left: 20px;
  margin-top: 4px;
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
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

.tree-node {
  margin-bottom: 2px;
}
</style>
