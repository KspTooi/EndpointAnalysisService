<template>
  <div
    v-show="visible"
    ref="rightMenuRef"
    class="context-menu"
    :style="{ left: menuX + 'px', top: menuY + 'px' }"
    @click.stop
    @contextmenu.prevent
  >
    <div class="menu-item" @click="onCreateRequest">
      <el-icon><Document /></el-icon>
      <span>创建请求</span>
    </div>
    <div class="menu-item" @click="onCreateGroup">
      <el-icon><FolderAdd /></el-icon>
      <span>创建子组</span>
    </div>
    <div class="menu-divider"></div>
    <div class="menu-item" @click="onCopyGroup">
      <el-icon><DocumentCopy /></el-icon>
      <span>复制分组</span>
    </div>
    <div class="menu-item" @click="onEditGroup">
      <el-icon><Edit /></el-icon>
      <span>编辑分组</span>
    </div>
    <div class="menu-divider"></div>
    <div class="menu-item" @click="onDeleteGroup">
      <el-icon><Delete /></el-icon>
      <span>删除分组</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { Document, FolderAdd, DocumentCopy, Edit, Delete } from "@element-plus/icons-vue";
import RdbgCollectionTreeRightMenuService from "../service/RdbgCollectionTreeRightMenuService";
import type { GetCollectionTreeVo } from "../api/CollectionApi";

const rightMenuRef = ref<HTMLElement>();

export interface RightMenuEmitter {
  (e: "on-create-request", node: GetCollectionTreeVo | null): void;
  (e: "on-create-group", node: GetCollectionTreeVo | null): void;
  (e: "on-copy-group", node: GetCollectionTreeVo): void;
  (e: "on-edit-group", node: GetCollectionTreeVo): void;
  (e: "on-delete-group", node: GetCollectionTreeVo): void;
}

const emit = defineEmits<RightMenuEmitter>();

//右键菜单控制打包
const {
  openRightMenu,
  closeRightMenu,
  currentNode,
  visible,
  x: menuX,
  y: menuY,
} = RdbgCollectionTreeRightMenuService.useRightMenuControl(rightMenuRef);

//右键菜单事件提交打包
const { onCreateRequest, onCreateGroup, onCopyGroup, onEditGroup, onDeleteGroup } =
  RdbgCollectionTreeRightMenuService.useRightMenuEmitter(emit, closeRightMenu, currentNode);

defineExpose({
  openRightMenu,
  closeRightMenu,
});
</script>

<style scoped>
.context-menu {
  position: fixed;
  background: #ffffff;
  border: 1px solid #d4d4d4;
  border-radius: 2px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 2000;
  min-width: 160px;
  padding: 2px 0;
  font-size: 13px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 20px 6px 28px;
  cursor: pointer;
  color: #333333;
  transition: background-color 0.1s;
  user-select: none;
  white-space: nowrap;
}

.menu-item:hover {
  background: #e8f4ff;
  color: #000000;
}

.menu-item .el-icon {
  font-size: 14px;
  width: 16px;
  text-align: center;
}

.menu-divider {
  height: 1px;
  background: #e5e5e5;
  margin: 2px 0;
}

.menu-item.disabled {
  color: #a8a8a8;
  cursor: not-allowed;
  pointer-events: none;
}

.menu-item.disabled:hover {
  background: transparent;
  color: #a8a8a8;
}
</style>
