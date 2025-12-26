<template>
  <div
    class="drive-entry-item"
    draggable="true"
    @click="handleClick"
    @dblclick="handleDoubleClick"
    @contextmenu.stop.prevent="handleRightClick"
    @dragstart="handleDragStart"
    @dragover.prevent="handleDragOver"
    @drop.prevent="handleDrop"
  >
    <div class="entry-icon-wrapper">
      <el-icon :size="64" class="entry-icon" :class="{ 'folder-icon': entry.kind === 1, 'file-icon': entry.kind === 0 }">
        <Folder v-if="entry.kind === 1" />
        <Document v-else />
      </el-icon>
    </div>
    <div class="entry-name" :title="entry.name">{{ entry.name }}</div>
    <div v-if="entry.kind === 0 && entry.attachSize" class="entry-size">{{ formatFileSize(entry.attachSize) }}</div>
  </div>
</template>

<script setup lang="ts">
import { Folder, Document } from "@element-plus/icons-vue";
import type { GetEntryListVo } from "@/api/drive/DriveApi.ts";

const props = defineProps<{
  entry: GetEntryListVo;
}>();

const emit = defineEmits<{
  (e: "on-entry-click", entry: GetEntryListVo): void;
  (e: "on-entry-dblclick", entry: GetEntryListVo): void;
  (e: "on-entry-contextmenu", entry: GetEntryListVo, event: MouseEvent): void;
  (e: "on-entry-dragstart", entry: GetEntryListVo, event: DragEvent): void;
  (e: "on-entry-dragover", entry: GetEntryListVo, event: DragEvent): void;
  (e: "on-entry-drop", entry: GetEntryListVo, event: DragEvent): void;
}>();

const formatFileSize = (bytes: string): string => {
  const bytesNum = parseInt(bytes);

  if (!bytesNum || bytesNum === 0) {
    return "-";
  }
  if (bytesNum < 1024) {
    return bytesNum + " B";
  }
  if (bytesNum < 1024 * 1024) {
    return (bytesNum / 1024).toFixed(2) + " KB";
  }
  if (bytesNum < 1024 * 1024 * 1024) {
    return (bytesNum / (1024 * 1024)).toFixed(2) + " MB";
  }
  return (bytesNum / (1024 * 1024 * 1024)).toFixed(2) + " GB";
};

const handleClick = () => {
  emit("on-entry-click", props.entry);
};

const handleDoubleClick = () => {
  emit("on-entry-dblclick", props.entry);
};

const handleRightClick = (event: MouseEvent) => {
  emit("on-entry-contextmenu", props.entry, event);
};

const handleDragStart = (event: DragEvent) => {
  emit("on-entry-dragstart", props.entry, event);
};

const handleDragOver = (event: DragEvent) => {
  emit("on-entry-dragover", props.entry, event);
};

const handleDrop = (event: DragEvent) => {
  emit("on-entry-drop", props.entry, event);
};
</script>

<style scoped>
.drive-entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  width: 120px;
  padding: 12px 8px;
  margin: 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
  user-select: none;
}

.drive-entry-item:hover {
  background-color: var(--el-fill-color-light);
}

.drive-entry-item:active {
  background-color: var(--el-fill-color);
}

.entry-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  margin-bottom: 8px;
}

.entry-icon {
  color: var(--el-text-color-primary);
}

.folder-icon {
  color: #409eff;
}

.file-icon {
  color: var(--el-text-color-regular);
}

.entry-name {
  width: 100%;
  text-align: center;
  font-size: 12px;
  color: var(--el-text-color-primary);
  word-break: break-all;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  min-height: 2.8em;
}

.entry-size {
  width: 100%;
  text-align: center;
  font-size: 11px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}
</style>
