<template>
  <div v-show="visible" ref="rightMenuRef" class="context-menu" :style="{ left: menuX + 'px', top: menuY + 'px' }" @click.stop @contextmenu.prevent>
    <!-- 当没有选中条目时，只显示新建文件夹和上传文件 -->
    <template v-if="!currentEntry">
      <div class="menu-item" @click="onRefresh">
        <el-icon><Refresh /></el-icon>
        <span>刷新</span>
      </div>
      <div class="menu-item" :class="{ disabled: !hasClipboard }" @click="onPaste">
        <el-icon><DocumentCopy /></el-icon>
        <span>粘贴</span>
      </div>
      <div class="menu-item" @click="onCreateFolder">
        <el-icon><FolderAdd /></el-icon>
        <span>新建文件夹</span>
      </div>
      <div class="menu-item" @click="onUploadFile">
        <el-icon><Upload /></el-icon>
        <span>上传文件</span>
      </div>
    </template>

    <!-- 当单选条目时，显示完整菜单 -->
    <template v-if="currentEntry && !isMultiSelect">
      <div v-if="isMultiSelect && currentEntry.kind === 0" class="menu-item" @click="onPreview">
        <el-icon><View /></el-icon>
        <span>预览</span>
      </div>
      <div v-if="currentEntry.kind === 0" class="menu-item" @click="onDownload">
        <el-icon><Download /></el-icon>
        <span>下载</span>
      </div>
      <div v-if="currentEntry.kind === 0" class="menu-divider"></div>
      <div class="menu-item" @click="onCut">
        <el-icon><Scissor /></el-icon>
        <span>剪切</span>
      </div>
      <div class="menu-item" @click="onCopy">
        <el-icon><DocumentCopy /></el-icon>
        <span>复制</span>
      </div>
      <div class="menu-divider"></div>
      <div class="menu-item" @click="onDelete">
        <el-icon><Delete /></el-icon>
        <span>删除</span>
      </div>
      <div class="menu-item" @click="onRename">
        <el-icon><Edit /></el-icon>
        <span>重命名</span>
      </div>
      <div class="menu-divider"></div>
      <div class="menu-item" @click="onProperties">
        <el-icon><InfoFilled /></el-icon>
        <span>属性</span>
      </div>
    </template>

    <!-- 当多选条目时，显示完整菜单 -->
    <template v-if="isMultiSelect">
      <div v-if="currentEntries.every((item) => item.kind === 0)" class="menu-item" @click="onDownload">
        <el-icon><Download /></el-icon>
        <span>下载({{ currentEntries.length }})</span>
      </div>
      <div class="menu-item" @click="onCopy">
        <el-icon><DocumentCopy /></el-icon>
        <span>复制</span>
      </div>
      <div class="menu-item" @click="onDelete">
        <el-icon><Delete /></el-icon>
        <span>删除</span>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { FolderAdd, View, Download, Scissor, DocumentCopy, Delete, Edit, InfoFilled, Upload, Refresh } from "@element-plus/icons-vue";
import DriveEntryRightMenuService from "../service/DriveEntryRightMenuService";
import type { EntryPo } from "@/views/drive/api/DriveTypes.ts"

const rightMenuRef = ref<HTMLElement>();

const emit = defineEmits<{
  (e: "on-refresh"): void; //刷新
  (e: "on-paste"): void; //粘贴
  (e: "on-create-folder"): void; //创建文件夹
  (e: "on-upload-file"): void; //上传文件
  (e: "on-preview", entry: EntryPo): void; //预览
  (e: "on-download", entries: EntryPo[]): void; //下载
  (e: "on-cut", entries: EntryPo[]): void; //剪切
  (e: "on-copy", entries: EntryPo[]): void; //复制
  (e: "on-delete", entries: EntryPo[]): void; //删除
  (e: "on-rename", entry: EntryPo): void; //重命名
  (e: "on-properties", entry: EntryPo): void; //属性
}>();

//右键菜单控制打包
const {
  openRightMenu,
  closeRightMenu,
  isMultiSelect,
  currentEntries,
  visible,
  currentEntry,
  hasClipboard,
  x: menuX,
  y: menuY,
} = DriveEntryRightMenuService.useRightMenuControl(rightMenuRef);

/**
 * 刷新
 */
const onRefresh = () => {
  emit("on-refresh");
  closeRightMenu();
};

/**
 * 粘贴
 */
const onPaste = () => {
  if (!hasClipboard.value) {
    return;
  }
  emit("on-paste");
  closeRightMenu();
};

/**
 * 创建文件夹
 */
const onCreateFolder = () => {
  emit("on-create-folder");
  closeRightMenu();
};

/**
 * 上传文件
 */
const onUploadFile = () => {
  emit("on-upload-file");
  closeRightMenu();
};

/**
 * 预览
 */
const onPreview = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("on-preview", currentEntry.value);
  closeRightMenu();
};

//下载
const onDownload = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("on-download", currentEntries.value);
  closeRightMenu();
};

/**
 * 剪切
 */
const onCut = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("on-cut", currentEntries.value);
  closeRightMenu();
};

/**
 * 复制
 */
const onCopy = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("on-copy", currentEntries.value);
  closeRightMenu();
};

/**
 * 删除
 */
const onDelete = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("on-delete", currentEntries.value);
  closeRightMenu();
};

/**
 * 重命名
 */
const onRename = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("on-rename", currentEntry.value);
  closeRightMenu();
};

/**
 * 属性
 */
const onProperties = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("on-properties", currentEntry.value);
  closeRightMenu();
};

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
