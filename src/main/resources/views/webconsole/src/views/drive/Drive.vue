<template>
  <div class="list-container">
    <!-- 控制面板 -->
    <DriveContrlPanel :entry-count="entryTotal" :upload-count="inQueueUploadCount" @on-search="updateQueryKeyword" @open-upload-queue="openFileUploadModal" />

    <!-- 文件选择器 -->
    <DriveFileSelector ref="fileSelectorRef" @on-file-selected="onFileSelected" :max-select="1000">
      <!-- 条目列表 -->
      <DriveEntryGrid
        :keyword="entryKeyword"
        @on-directory-change="onDirectoryChange"
        @on-entry-click=""
        @on-entry-dblclick=""
        @on-entry-drag="onEntryDrag"
        @on-entry-contextmenu="onEntryContextmenu"
        ref="entryGridRef"
      />
    </DriveFileSelector>

    <!-- 右键菜单 -->
    <DriveEntryRightMenu
      ref="rightMenuRef"
      @on-create-folder="onCreateFolder"
      @on-upload-file="onUploadFile"
      @on-preview="onPreview"
      @on-download="onDownload"
      @on-cut="onCut"
      @on-copy="onCopy"
      @on-paste="onPaste"
      @on-delete="onDelete"
      @on-rename="onRename"
      @on-properties="onProperties"
      @on-refresh="loadEntries"
    />

    <!-- 创建文件夹模态框 -->
    <DriveModalCreateDir ref="createEntryModalRef" :parent-id="currentParentId" @success="loadEntries" />

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
import DriveApi, { type GetEntryListDto, type GetEntryListVo } from "@/api/drive/DriveApi.ts";
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

const inQueueUploadCount = ref(0); //正在上传的文件数量
const fileInput = ref<HTMLInputElement | null>(null);
const fileUploadRef = ref<InstanceType<typeof DriveModalFileUpload> | null>(null);
const createEntryModalRef = ref<InstanceType<typeof DriveModalCreateDir> | null>(null);
const rightMenuRef = ref<InstanceType<typeof DriveEntryRightMenu> | null>(null);
const removeConfirmRef = ref<InstanceType<typeof DriveModalRemove> | null>(null);
const renameModalRef = ref<InstanceType<typeof DriveModalRename> | null>(null);
const fileSelectorRef = ref<InstanceType<typeof DriveFileSelector> | null>(null);
const moveConfirmModalRef = ref<InstanceType<typeof DriveModalMoveConfirm> | null>(null);
const currentSelectedEntry = ref<GetEntryListVo | null>(null);
const driveHolder = DriveHolder();
const entryGridRef = ref<InstanceType<typeof DriveEntryGrid> | null>(null);

//列表查询条件
const entryKeyword = ref<string | null>(null);

//列表数据
const entryData = ref<GetEntryListVo[]>([]);

//列表总条数
const entryTotal = ref(0);

//当前文件夹ID
const currentParentId = ref<string | null>(null);

const updateQueryKeyword = (keyword: string | null) => {
  entryKeyword.value = keyword;
};

/**
 * 加载条目列表
 */
const loadEntries = async () => {
  const ret = await entryGridRef.value?.listLoad();
  if (ret) {
    entryData.value = ret.data;
    entryTotal.value = ret.total;
  }
};

/**
 * 右键菜单打开
 * @param event 鼠标事件
 * @param entries 当前选中的条目列表
 */
const onEntryContextmenu = (entries: GetEntryListVo[], event: MouseEvent) => {
  rightMenuRef.value?.openMenu(event, entries);
};

const onDirectoryChange = (targetId: string | null) => {
  currentParentId.value = targetId;
};

/* const handleEntryDoubleClick = (id: string, kind: number) => {
  if (kind === 1) {
    // 双击文件夹，进入文件夹
    listForm.parentId = id;
    listForm.pageNum = 1;
    loadList();
  }
  if (kind === 0) {
    // 双击文件，可根据需要实现下载或预览
  }
}; */

/**
 * 文件选择器->文件选择
 */
const onFileSelected = (files: File[]) => {
  ElMessage.info(`正在处理 ${files.length} 个文件`);
  //添加到上传队列
  fileUploadRef.value?.toUploadQueue(files, currentParentId.value);
};

//右键菜单操作

/**
 * 右键菜单->创建文件夹
 */
const onCreateFolder = () => {
  createEntryModalRef.value?.openModal();
};

/**
 * 右键菜单->上传文件
 */
const onUploadFile = () => {
  fileSelectorRef.value?.openSelector();
};

/**
 * 右键菜单->预览
 * @param entry 单个条目
 */
const onPreview = (entry: GetEntryListVo) => {
  // 预览逻辑，可根据需要实现
  ElMessage.info("预览功能待实现");
};

/**
 * 右键菜单->下载
 * @param entries 条目列表
 */
const onDownload = async (entries: GetEntryListVo[]) => {
  //不支持文件夹与文件混选下载
  if (entries.some((item) => item.kind === 1)) {
    ElMessage.error("不支持文件与文件夹打包下载！请选择同类型条目打包下载！");
    return;
  }

  //获取签名
  try {
    const result = await DriveApi.getEntrySign({ ids: entries.map((item) => item.id as string) });
    if (Result.isSuccess(result)) {
      const params = result.data.params;
      window.open(`/drive/object/access/downloadEntry?sign=${params}`, "_blank");
    }
  } catch (error: any) {
    ElMessage.error(error.message || "下载失败");
  }
};

/**
 * 右键菜单->剪切
 * @param entries 条目列表
 */
const onCut = (entries: GetEntryListVo[]) => {
  // 剪切逻辑，可根据需要实现
  ElMessage.info("剪切功能待实现");
};

/**
 * 右键菜单->复制
 * @param entries 条目列表
 */
const onCopy = (entries: GetEntryListVo[]) => {
  driveHolder.setClipBoardEntry(entries);
};

/**
 * 右键菜单->粘贴
 * @param entries 条目列表
 */
const onPaste = async () => {
  const entries = driveHolder.getClipBoardEntry;
  if (entries.length === 0) {
    return;
  }
  try {
    //调用后端粘贴接口
    const result = await DriveApi.copyEntry({
      entryIds: entries.map((item) => item.id as string),
      parentId: currentParentId.value,
    });
    if (Result.isSuccess(result)) {
      ElMessage.success("粘贴成功");
      loadEntries();
    }
  } catch (error: any) {
    ElMessage.error(error.message || "粘贴失败");
  }
};

/**
 * 右键菜单->删除
 * @param entries 条目列表
 */
const onDelete = async (entries: GetEntryListVo[]) => {
  if (!removeConfirmRef.value) {
    return;
  }

  await removeConfirmRef.value.openConfirm(entries);
};

/**
 * 右键菜单->重命名
 * @param entry 单个条目
 */
const onRename = (entry: GetEntryListVo) => {
  if (!renameModalRef.value) {
    return;
  }
  renameModalRef.value.openModal(entry);
};

/**
 * 右键菜单->属性
 * @param entry 单个条目
 */
const onProperties = (entry: GetEntryListVo) => {
  // 属性逻辑，可根据需要实现
  ElMessage.info("属性功能待实现");
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

/**
 * 条目拖拽事件
 * @param target 目标条目
 * @param entries 被拖拽的条目列表
 */
const onEntryDrag = async (target: GetEntryListVo, entries: GetEntryListVo[]) => {
  if (target === null) {
    ElMessage.error("拖拽到上级目录");
    return;
  }

  ElMessage.info("拖拽事件 目标ID: " + target.id + " 条目ID: " + entries.map((item) => item.id).join(","));
  return;
  const entryIds: string[] = [];

  entries.forEach((item) => {
    entryIds.push(item.id as string);
  });

  //检测移动
  const result = await DriveApi.checkEntryMove({ targetId: target.id, entryIds: entryIds, mode: 0 });
  const canMove = result.data.canMove;

  //0:可以移动 1:名称冲突 2:不可移动
  if (canMove == 2) {
    ElMessage.error(result.data.message);
    return;
  }

  if (canMove == 1) {
    const conflictNames = result.data.conflictNames;
    const action = await moveConfirmModalRef.value.openModal(conflictNames);

    //取消
    if (action === -1) {
      return;
    }

    //跳过
    if (action === 1) {
      await DriveApi.moveEntry({ targetId: target.id, entryIds: entryIds, mode: 1 });
    }
  }

  //覆盖移动
  await DriveApi.moveEntry({ targetId: target.id, entryIds: entryIds, mode: 0 });
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
