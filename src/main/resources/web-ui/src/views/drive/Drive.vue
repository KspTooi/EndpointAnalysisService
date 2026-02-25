<template>
  <div class="no-active-company" v-if="noSpaceAvailable" style="width: 100%">
    <el-empty>
      <template #description>
        <div class="empty-description">
          <h3>暂无可用的云盘空间</h3>
          <p>您尚未加入任何云盘空间，请联系管理员为您分配</p>
        </div>
      </template>
      <el-button type="primary" @click="goToCompanySetup">前往空间管理</el-button>
    </el-empty>
  </div>

  <div class="list-container no-outline" ref="containerRef" v-if="!noSpaceAvailable">
    <!-- 控制面板 -->
    <DriveControlPanel
      :entry-count="entryTotal"
      :upload-count="inQueueUploadCount"
      @on-search="updateQueryKeyword"
      @open-upload-queue="openFileUploadModal"
      @on-path-change="onPathChange"
      @no-space-available="noSpaceAvailable = true"
    />
    <!-- {{ isFocused }} -->

    <!-- 文件选择器 -->
    <DriveFileSelector ref="fileSelectorRef" @on-file-selected="onFileSelected" :max-select="1000">
      <!-- 条目列表 -->
      <DriveEntryGrid
        :keyword="entryKeyword"
        @on-directory-change="onGridDirectoryChange"
        @on-entries-loaded="onGridLoad"
        @on-entry-dblclick="enterOrOpenFile"
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
      @on-download-url="menuDownloadUrl"
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

    <!-- 下载URL模态框 -->
    <DriveModalDownloadUrl ref="downloadUrlModalRef" />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { ElMessage } from "element-plus";
import DriveModalCreateDir from "@/views/drive/components/DriveModalCreateDir.vue";
import DriveEntryGrid from "@/views/drive/components/DriveEntryGrid.vue";
import DriveEntryRightMenu from "@/views/drive/components/DriveEntryRightMenu.vue";
import DriveModalFileUpload, { type UploadQueueItem } from "@/views/drive/components/DriveModalFileUpload.vue";
import DriveControlPanel from "@/views/drive/components/DriveControlPanel.vue";
import DriveFileSelector from "@/views/drive/components/DriveFileSelector.vue";
import DriveModalRemove from "@/views/drive/components/DriveModalRemove.vue";
import DriveModalRename from "@/views/drive/components/DriveModalRename.vue";
import DriveModalMoveConfirm from "@/views/drive/components/DriveModalMoveConfirm.vue";
import DriveModalDownloadUrl from "@/views/drive/components/DriveModalDownloadUrl.vue";
import type { EntryPo, GetDriveInfoVo, GetEntryListPathVo } from "@/views/drive/api/DriveTypes.ts";
import DriveService from "@/views/drive/service/DriveService";
import ElementFocusService from "@/service/ElmentFocusService";
import GenricHotkeyService from "@/service/GenricHotkeyService";
import type Result from "@/commons/entity/Result";
import { useRouter } from "vue-router";
const router = useRouter();

const inQueueUploadCount = ref(0); //正在上传的文件数量
const fileUploadRef = ref<InstanceType<typeof DriveModalFileUpload> | null>(null);
const createEntryModalRef = ref<InstanceType<typeof DriveModalCreateDir> | null>(null);
const rightMenuRef = ref<InstanceType<typeof DriveEntryRightMenu> | null>(null);
const removeConfirmRef = ref<InstanceType<typeof DriveModalRemove> | null>(null);
const renameModalRef = ref<InstanceType<typeof DriveModalRename> | null>(null);
const downloadUrlModalRef = ref<InstanceType<typeof DriveModalDownloadUrl> | null>(null);
const fileSelectorRef = ref<InstanceType<typeof DriveFileSelector> | null>(null);
const moveConfirmModalRef = ref<InstanceType<typeof DriveModalMoveConfirm> | null>(null);
const entryGridRef = ref<InstanceType<typeof DriveEntryGrid>>();
const containerRef = ref<HTMLElement>();

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
  downloadUrl: menuDownloadUrl,
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
  entryGridRef,
  downloadUrlModalRef
);

//拖拽功能打包
const { dragMove } = DriveService.useEntryDragFunction(entryGridRef, moveConfirmModalRef);

//焦点服务打包
const { isFocused, clearFocus } = ElementFocusService.useElementFocus(containerRef);

//快捷键功能打包
GenricHotkeyService.useHotkeyFunction(
  {
    enter: () => {
      enterOrOpenFile(null);
    },

    //复制
    ctrl_c: () => {
      menuCopy(entryGridRef.value?.getSelectedEntries() || []);
    },

    //粘贴
    ctrl_v: () => {
      menuPaste();
    },

    //剪切
    ctrl_x: () => {
      menuCut(entryGridRef.value?.getSelectedEntries() || []);
    },

    //删除
    delete: () => {
      menuRemove(entryGridRef.value?.getSelectedEntries() || []);
      clearFocus();
    },

    //重命名
    f2: () => {
      menuRename(entryGridRef.value?.getSelectedEntries()?.[0] || null);
      clearFocus();
    },

    //刷新
    f5: () => {
      menuRefresh();
    },

    //返回
    backspace: () => {
      entryGridRef.value?.backspace();
    },

    //选择所有
    ctrl_a: () => {
      entryGridRef.value?.selectAll();
    },
  },
  isFocused
);

/**
 * 进入或打开文件
 * @param entry 条目对象
 */
const enterOrOpenFile = (entry: EntryPo) => {
  //获取当前选中的条目列表
  const selectedEntries = [];

  //如果传入条目 则添加到选中列表
  if (entry) {
    selectedEntries.push(entry);
  }

  //如果未传入条目 则获取当前选中的条目列表
  if (!entry && entryGridRef.value) {
    selectedEntries.push(...entryGridRef.value?.getSelectedEntries());
  }

  if (selectedEntries.length != 1) {
    return;
  }

  //如果是文件夹 则导航进入文件夹
  if (selectedEntries[0].kind == 1) {
    entryGridRef.value?.enterDirectory(selectedEntries[0], currentDir.value);
    return;
  }

  //如果是文件 尝试预览、下载
  menuPreview(selectedEntries[0]);
};

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

const noSpaceAvailable = ref(false);

/**
 * 前往空间管理
 */
const goToCompanySetup = () => {
  router.push({
    name: "company-manager",
  });
};

/**
 * 当前目录路径变更
 * @param paths 当前目录路径
 */
const onPathChange = (path: GetEntryListPathVo) => {
  entryGridRef.value?.redirectDirectory(path.id);
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

.no-outline:focus {
  outline: none;
}

.no-active-company {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  padding: 20px;
}
</style>
