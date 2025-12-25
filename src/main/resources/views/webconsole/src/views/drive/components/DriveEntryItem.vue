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
      <el-icon :size="64" class="entry-icon" :class="{ 'folder-icon': kind === 1, 'file-icon': kind === 0 }">
        <Folder v-if="kind === 1" />
        <Document v-else />
      </el-icon>
    </div>
    <div class="entry-name" :title="name">{{ name }}</div>
    <div v-if="kind === 0 && attachSize" class="entry-size">{{ formatFileSize(attachSize) }}</div>
  </div>
</template>

<script setup lang="ts">
import { Folder, Document } from "@element-plus/icons-vue";

const props = defineProps<{
  id: string; //条目ID
  name: string; //条目名称
  kind: number; //类型 0:文件 1:文件夹
  attachId: string | null; //文件附件ID
  attachSize: string; //文件附件大小
  attachSuffix: string | null; //文件附件类型
}>();

const emit = defineEmits<{
  (e: "click", id: string): void;
  (e: "dblclick", id: string, kind: number): void;
  (e: "contextmenu", event: MouseEvent): void;
  (e: "dragstart", id: string, event: DragEvent): void;
  (e: "dragover", id: string, event: DragEvent): void;
  (e: "drop", id: string, event: DragEvent): void;
}>();

const formatFileSize = (bytes: string): string => {
  //转换为数字
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
  emit("click", props.id);
};

const handleDoubleClick = () => {
  emit("dblclick", props.id, props.kind);
};

const handleRightClick = (event: MouseEvent) => {
  emit("contextmenu", event);
};

const handleDragStart = (event: DragEvent) => {
  emit("dragstart", props.id, event);
};

const handleDragOver = (event: DragEvent) => {
  emit("dragover", props.id, event);
};

const handleDrop = (event: DragEvent) => {
  emit("drop", props.id, event);
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
