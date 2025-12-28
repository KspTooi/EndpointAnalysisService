<template>
  <div
    class="drive-entry-item"
    :draggable="type === 0"
    @click="onClick"
    @dblclick="onDoubleClick"
    @contextmenu.stop.prevent="onContextmenu"
    @dragstart="onDragStart"
    @dragover.prevent="onDragOver"
    @drop.prevent="onDrop"
  >
    <div class="entry-icon-wrapper">
      <!-- 上级目录 -->
      <el-icon v-if="type === 1" :size="64" class="!text-[#00a8be]">
        <Folder />
      </el-icon>

      <!-- 普通条目 -->
      <el-icon
        v-if="type === 0"
        :size="64"
        class="entry-icon"
        :class="{ 'folder-icon': entry.kind === 1, 'file-icon': entry.kind === 0 }"
      >
        <!-- 文件夹 -->
        <template v-if="entry.kind === 1">
          <Folder />
        </template>

        <!-- 文件 -->
        <template v-if="entry.kind === 0">
          <IBytesizePhoto v-if="fileCategory === EntryCategory.PHOTO" />
          <IBiFileEarmarkPlay v-if="fileCategory === EntryCategory.VIDEO" />
          <IBiFileEarmarkMusic v-if="fileCategory === EntryCategory.AUDIO" />
          <IBiFiletypePdf v-if="fileCategory === EntryCategory.PDF" />
          <IBiFiletypeDoc v-if="fileCategory === EntryCategory.WORD" />
          <IBiFiletypeXls v-if="fileCategory === EntryCategory.EXCEL" />
          <IBiFiletypePpt v-if="fileCategory === EntryCategory.PPT" />
          <IFormkitZip v-if="fileCategory === EntryCategory.ARCHIVE" />
          <IBiCodeSlash v-if="fileCategory === EntryCategory.CODE" />
          <IBiFiletypeTxt v-if="fileCategory === EntryCategory.TEXT" />
          <IBiFiletypeExe v-if="fileCategory === EntryCategory.EXECUTABLE" />
          <Document v-if="fileCategory === EntryCategory.OTHER" />
        </template>
      </el-icon>
    </div>

    <!-- 上级目录 -->
    <div v-if="type === 1" class="entry-name">{{ "上级目录" }}</div>

    <!-- 普通条目 -->
    <div v-if="type === 0" class="entry-name" :title="entry.name">{{ entry.name }}</div>
    <div v-if="type === 0 && entry.kind === 0 && entry.attachSize" class="entry-size">{{ vFileSize }}</div>
  </div>
</template>

<script setup lang="ts">
import { Folder, Document, Picture, VideoCamera, Headset } from "@element-plus/icons-vue";
import { computed } from "vue";
import FileUtils from "@/utils/FileUtils";
import { ElMessage } from "element-plus";
import type { CurrentDirPo, EntryPo } from "@/views/drive/api/DriveTypes.ts";
import FileCategoryService, { EntryCategory } from "@/service/FileCategoryService";

/**
 * 定义props
 */
const props = withDefaults(
  defineProps<{
    //条目类型 0:普通条目 1:上级目录 可选 默认值为0
    type?: 0 | 1;

    //条目对象
    entry?: EntryPo;

    //当前目录
    currentDir: CurrentDirPo;
  }>(),
  {
    type: 0,
  }
);

/**
 * 定义emits
 */
const emit = defineEmits<{
  (e: "on-click", entry: EntryPo): void;
  (e: "on-dblclick", entry: EntryPo, currentDir: CurrentDirPo): void;
  (e: "on-contextmenu", entry: EntryPo, event: MouseEvent): void;
  (e: "on-drag-start", entry: EntryPo, event: DragEvent): void;
  (e: "on-drag-over", entry: EntryPo, event: DragEvent): void;
  (e: "on-drag-drop", entry: EntryPo, currentDir: CurrentDirPo, event: DragEvent): void;
}>();

const vFileSize = computed(() => {
  return FileUtils.formatFileSize(props.entry.attachSize);
});

const { fileCategory } = FileCategoryService.useFileCategory(props.entry?.attachSuffix || "");

/**
 * 条目被单击
 * @param entry 条目对象
 */
const onClick = () => {
  emit("on-click", props.entry);
};

/**
 * 条目被双击
 * @param entry 条目对象
 */
const onDoubleClick = () => {
  //如果当前是上级目录 则构造一个虚拟的PO
  if (props.type === 1) {
    const parentDirPo: EntryPo = {
      id: props.currentDir.parentId,
      parentId: props.currentDir.parentId,
      name: props.currentDir.name + "_上级目录",
      kind: 1,
      attachId: null,
      attachSize: null,
      attachSuffix: null,
      createTime: null,
      updateTime: null,
    };
    emit("on-dblclick", parentDirPo, props.currentDir);
    return;
  }

  emit("on-dblclick", props.entry, props.currentDir);
};

/**
 * 右键菜单打开
 * @param entry 条目对象
 * @param event 鼠标事件
 */
const onContextmenu = (event: MouseEvent) => {
  emit("on-contextmenu", props.entry, event);
};

/**
 * 拖拽开始
 * @param entry 条目对象
 * @param event 鼠标事件
 */
const onDragStart = (event: DragEvent) => {
  emit("on-drag-start", props.entry, event);
};

/**
 * 拖拽经过
 * @param entry 条目对象
 * @param event 鼠标事件
 */
const onDragOver = (event: DragEvent) => {
  emit("on-drag-over", props.entry, event);
};

/**
 * 拖拽放置
 * @param entry 条目对象
 * @param event 鼠标事件
 */
const onDrop = (event: DragEvent) => {
  //如果当前是上级目录 则构造一个虚拟的PO
  if (props.type === 1) {
    emit("on-drag-drop", null, props.currentDir, event);
    return;
  }

  emit("on-drag-drop", props.entry, props.currentDir, event);
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
