<template>
  <div
    class="drive-entry-item"
    @click="handleClick"
    @dblclick="handleDoubleClick"
    @contextmenu.stop.prevent="handleRightClick"
    @dragover.prevent="handleDragOver"
    @drop.prevent="handleDrop"
  >
    <div class="entry-icon-wrapper">
      <el-icon :size="64" class="entry-icon">
        <Folder />
      </el-icon>
    </div>
    <div class="entry-name" :title="name">{{ name }}</div>
  </div>
</template>

<script setup lang="ts">
import { Folder } from "@element-plus/icons-vue";

const props = defineProps<{
  targetId: string | null; //目标ID
  name: string; //条目名称
}>();

const emit = defineEmits<{
  (e: "click", targetId: string | null): void;
  (e: "dblclick", targetId: string | null): void;
  (e: "contextmenu", event: MouseEvent): void;
  (e: "dragover", targetId: string | null, event: DragEvent): void;
  (e: "drop", targetId: string | null, event: DragEvent): void;
}>();

const handleClick = () => {
  emit("click", props.targetId);
};

const handleDoubleClick = () => {
  emit("dblclick", props.targetId);
};

const handleRightClick = (event: MouseEvent) => {
  emit("contextmenu", event);
};

const handleDragOver = (event: DragEvent) => {
  emit("dragover", props.targetId, event);
};

const handleDrop = (event: DragEvent) => {
  emit("drop", props.targetId, event);
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

.entry-icon {
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
