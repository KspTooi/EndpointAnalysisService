<template>
  <div class="path-container">
    <div v-for="(path, index) in paths" :key="path.id" class="path-item-wrapper">
      <div class="path-item" :class="{ 'is-last': index === paths.length - 1 }" @click="onPathClick(path, index)">
        <el-icon v-if="index === 0" class="root-icon"><FolderOpened /></el-icon>
        <span class="path-name">{{ path.name }}</span>
      </div>
      <span v-if="index < paths.length - 1" class="path-separator">
        <el-icon><ArrowRight /></el-icon>
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { FolderOpened, ArrowRight } from "@element-plus/icons-vue";
import type { GetEntryListPathVo } from "@/views/drive/api/DriveTypes.ts";

const props = defineProps<{
  paths: GetEntryListPathVo[];
}>();

const emit = defineEmits<{
  (e: "on-path-change", path: GetEntryListPathVo): void;
}>();

const onPathClick = (path: GetEntryListPathVo, index: number) => {
  if (index === props.paths.length - 1) return;
  emit("on-path-change", path);
};
</script>

<style scoped>
.path-container {
  display: flex;
  align-items: center;
  flex-wrap: nowrap;
  overflow-x: auto;
  overflow-y: hidden;
  padding: 2px 0;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.path-container::-webkit-scrollbar {
  display: none;
}

.path-item-wrapper {
  display: flex;
  align-items: center;
}

.path-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 8px;
  border-radius: 0;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  color: var(--el-text-color-regular);
  white-space: nowrap;
  user-select: none;
}

.path-item:hover {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.path-item.is-last {
  color: var(--el-text-color-primary);
  font-weight: 600;
  cursor: default;
}

.path-item.is-last:hover {
  background-color: transparent;
}

.root-icon {
  font-size: 16px;
  color: var(--el-color-primary);
}

.path-name {
  font-size: 13.5px;
  line-height: 1.2;
}

.path-separator {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 2px;
  color: var(--el-text-color-placeholder);
  font-size: 12px;
  user-select: none;
}

.path-separator .el-icon {
  font-size: 12px;
}
</style>
