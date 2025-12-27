<template>
  <div class="list-container">
    <!-- 控制面板 -->
    <DriveContrlPanel
      :entry-count="entryTotal"
      :upload-count="inQueueUploadCount"
      @on-search="updateQueryKeyword"
      @open-upload-queue="openFileUploadModal"
    />

    <!-- 文件选择器 -->
    <DriveFileSelector ref="fileSelectorRef" @on-file-selected="onFileSelected" :max-select="1000">
      <!-- 条目列表 -->
      <DriveEntryGrid
        :keyword="entryKeyword"
        @on-directory-change="onGridDirectoryChange"
        @on-entries-loaded="onGridLoad"
        @on-entry-click=""
        @on-entry-dblclick=""
        @on-entry-drag="dragMove"
        @on-entry-contextmenu="onEntryContextmenu"
        ref="entryGridRef"
      />
    </DriveFileSelector>

    <!-- 右键菜单 -->
    <DriveEntryRightMenu
      ref="rightMenuRef"
      @on-create-folder="menuCreateDir"
      @on-upload-file="menuUploadFile"
      @on-preview="menuPreview"
      @on-download="menuDownload"
      @on-cut="menuCut"
      @on-copy="menuCopy"
      @on-paste="menuPaste"
      @on-delete="menuRemove"
      @on-rename="menuRename"
      @on-properties="menuProperties"
      @on-refresh="menuRefresh"
    />

    <!-- 创建文件夹模态框 -->
    <DriveModalCreateDir ref="createEntryModalRef" :current-dir="currentDir" @success="loadEntries" />

    <!-- 文件上传队列组件 -->
    <DriveModalFileUpload ref="fileUploadRef" kind="drive" @on-upload-success="loadEntries" @on-queue-update="onQueueUpdate" />

    <!-- 删除确认对话框 -->
    <DriveModalRemove ref="removeConfirmRef" @success="loadEntries" />

    <!-- 重命名模态框 -->
    <DriveModalRename ref="renameModalRef" @success="loadEntries" />

    <!-- 移动冲突确认模态框 -->
    <DriveModalMoveConfirm ref="moveConfirmModalRef" />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import DriveApi from "@/views/drive/api/DriveApi.ts";
import DriveModalCreateDir from "@/views/drive/components/DriveModalCreateDir.vue";
import DriveEntryGrid from "@/views/drive/components/DriveEntryGrid.vue";
import DriveEntryRightMenu from "@/views/drive/components/DriveEntryRightMenu.vue";
import DriveModalFileUpload, { type UploadQueueItem } from "@/views/drive/components/DriveModalFileUpload.vue";
import { Result } from "@/commons/entity/Result";
import DriveContrlPanel from "@/views/drive/components/DriveContrlPanel.vue";
import DriveFileSelector from "@/views/drive/components/DriveFileSelector.vue";
import DriveModalRemove from "@/views/drive/components/DriveModalRemove.vue";
import DriveModalRename from "@/views/drive/components/DriveModalRename.vue";
import DriveModalMoveConfirm from "@/views/drive/components/DriveModalMoveConfirm.vue";
import { DriveHolder } from "@/store/DriveHolder.ts";
import type { CurrentDirPo, EntryPo } from "@/views/drive/api/DriveTypes.ts";
import DriveService from "./service/DriveService";

const inQueueUploadCount = ref(0); //正在上传的文件数量
const fileInput = ref<HTMLInputElement | null>(null);
const fileUploadRef = ref<InstanceType<typeof DriveModalFileUpload> | null>(null);
const createEntryModalRef = ref<InstanceType<typeof DriveModalCreateDir> | null>(null);
const rightMenuRef = ref<InstanceType<typeof DriveEntryRightMenu> | null>(null);
const removeConfirmRef = ref<InstanceType<typeof DriveModalRemove> | null>(null);
const renameModalRef = ref<InstanceType<typeof DriveModalRename> | null>(null);
const fileSelectorRef = ref<InstanceType<typeof DriveFileSelector> | null>(null);
const moveConfirmModalRef = ref<InstanceType<typeof DriveModalMoveConfirm> | null>(null);
const currentSelectedEntry = ref<EntryPo | null>(null);
const driveHolder = DriveHolder();
const entryGridRef = ref<InstanceType<typeof DriveEntryGrid>>();

//条目列表打包
const { currentDir, entryTotal, entryKeyword, updateQueryKeyword, onGridLoad, onGridDirectoryChange, loadEntries } =
  DriveService.useEntryList(entryGridRef);

/**
 * 右键菜单功能打包
 */
const {
  refresh: menuRefresh,
  paste: menuPaste,
  createDir: menuCreateDir,
  uploadFile: menuUploadFile,
  preview: menuPreview,
  download: menuDownload,
  cut: menuCut,
  copy: menuCopy,
  remove: menuRemove,
  rename: menuRename,
  properties: menuProperties,
} = DriveService.useEntryRightMenuFunction(
  createEntryModalRef,
  fileSelectorRef,
  removeConfirmRef,
  renameModalRef,
  entryGridRef
);

//拖拽功能打包
const { dragMove } = DriveService.useEntryDragFunction(entryGridRef, moveConfirmModalRef);

/**
 * 右键菜单打开
 * @param event 鼠标事件
 * @param entries 当前选中的条目列表
 */
const onEntryContextmenu = (entries: EntryPo[], event: MouseEvent) => {
  rightMenuRef.value?.openRightMenu(event, entries);
};

/**
 * 文件选择器->文件选择
 */
const onFileSelected = (files: File[]) => {
  ElMessage.info(`正在处理 ${files.length} 个文件`);
  //添加到上传队列
  fileUploadRef.value?.toUploadQueue(files, currentDir.value.id);
};

/**
 * 打开文件上传弹窗
 */
const openFileUploadModal = () => {
  fileUploadRef.value?.openModal();
};

/**
 * 上传队列更新
 * @param queue 上传队列
 */
const onQueueUpdate = (queue: UploadQueueItem[]) => {
  let count = 0;
  queue.forEach((item) => {
    if (item.status === "uploading" || item.status === "pending") {
      count++;
    }
  });
  inQueueUploadCount.value = count;
};
</script>

<style scoped>
.list-container {
  padding: 10px;
  max-width: 100%;
  overflow-x: auto;
  width: 100%;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  width: 100%;
}

:deep(.modal-centered) {
  margin: 0 auto;
  top: 50%;
  transform: translateY(-50%);
}
</style>
