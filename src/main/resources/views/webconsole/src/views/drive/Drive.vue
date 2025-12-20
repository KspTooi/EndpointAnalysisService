<template>
  <div class="list-container">
    <!-- 控制面板 -->
    <DriveContrlPanel :entry-count="listTotal" :upload-count="inQueueUploadCount" @on-search="loadList" @open-upload-queue="openFileUploadModal" />

    <!-- 文件选择器 -->
    <DriveFileSelector ref="fileSelectorRef" @on-file-selected="onFileSelected" :max-select="1000">
      <!-- 条目列表 -->
      <DriveEntryGrid
        :data="listData"
        :loading="listLoading"
        :parent-id="listForm.parentId"
        @grid-right-click="handleGridRightClick"
        @entry-right-click="onEntryRightClick"
        @entry-click="handleEntryClick"
        @entry-dblclick="handleEntryDoubleClick"
        @return-parent-dir="listReturnParentDir"
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
    />

    <!-- 创建文件夹模态框 -->
    <DriveModalCreateDir ref="createEntryModalRef" :parent-id="listForm.parentId" @success="loadList" />

    <!-- 文件上传队列组件 -->
    <DriveModalFileUpload ref="fileUploadRef" kind="drive" @on-upload-success="loadList" @on-queue-update="onQueueUpdate" />

    <!-- 删除确认对话框 -->
    <DriveModalRemove ref="removeConfirmRef" @success="loadList" />
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
import { DriveHolder } from "@/store/DriveHolder.ts";

const inQueueUploadCount = ref(0); //正在上传的文件数量
const fileInput = ref<HTMLInputElement | null>(null);
const fileUploadRef = ref<InstanceType<typeof DriveModalFileUpload> | null>(null);
const createEntryModalRef = ref<InstanceType<typeof DriveModalCreateDir> | null>(null);
const rightMenuRef = ref<InstanceType<typeof DriveEntryRightMenu> | null>(null);
const removeConfirmRef = ref<InstanceType<typeof DriveModalRemove> | null>(null);
const fileSelectorRef = ref<InstanceType<typeof DriveFileSelector> | null>(null);
const currentSelectedEntry = ref<GetEntryListVo | null>(null);
const driveHolder = DriveHolder();

const listForm = reactive<GetEntryListDto>({
  parentId: null,
  keyword: null,
  pageNum: 1,
  pageSize: 50000,
});

const listData = ref<GetEntryListVo[]>([]);
const listTotal = ref(0);
const listLoading = ref(false);

const loadList = async (keyword: string | null = null) => {
  listLoading.value = true;
  try {
    listForm.keyword = keyword;
    const res = await DriveApi.getEntryList(listForm);
    if (res.code === 0) {
      listData.value = res.data;
      listTotal.value = res.total;
    }
    if (res.code !== 0) {
      ElMessage.error(res.message || "加载列表失败");
    }
  } catch (error: any) {
    ElMessage.error(error.message || "加载列表失败");
  } finally {
    listLoading.value = false;
  }
};

const handleEntryClick = (id: string) => {
  const entry = listData.value.find((item) => item.id === id);
  if (entry) {
    currentSelectedEntry.value = entry;
  }
};

const handleGridRightClick = (event: MouseEvent) => {
  rightMenuRef.value?.openMenu(event, null);
};

/**
 * 条目右键点击
 * @param event 鼠标事件
 * @param entries 当前选中的条目列表
 */
const onEntryRightClick = (event: MouseEvent, entries: GetEntryListVo[]) => {
  rightMenuRef.value?.openMenu(event, entries);
};

const handleEntryDoubleClick = (id: string, kind: number) => {
  if (kind === 1) {
    // 双击文件夹，进入文件夹
    listForm.parentId = id;
    listForm.pageNum = 1;
    loadList();
  }
  if (kind === 0) {
    // 双击文件，可根据需要实现下载或预览
  }
};

loadList();

/**
 * 文件选择器->文件选择
 */
const onFileSelected = (files: File[]) => {
  ElMessage.info(`正在处理 ${files.length} 个文件`);
  //添加到上传队列
  fileUploadRef.value?.toUploadQueue(files, listForm.parentId);
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
const onDownload = (entries: GetEntryListVo[]) => {
  // 下载逻辑，可根据需要实现
  ElMessage.info("下载功能待实现");
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
      entryIds: entries.map((item) => item.id),
      parentId: listForm.parentId,
    });
    if (Result.isSuccess(result)) {
      ElMessage.success("粘贴成功");
      loadList();
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
  // 重命名逻辑，可根据需要实现
  ElMessage.info("重命名功能待实现");
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
 * 返回上级目录
 * @param parentId 父级ID
 */
const listReturnParentDir = async (parentId: string | null) => {
  const result = await DriveApi.getEntryDetails({ id: parentId });

  if (Result.isSuccess(result)) {
    listForm.parentId = result.data.parentId;
    listForm.pageNum = 1;
    listForm.pageSize = 50000;
    await loadList();
    return;
  }

  ElMessage.error(result.message);
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
