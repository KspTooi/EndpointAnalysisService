<template>
  <div class="list-container">
    <!-- 控制面板 -->
    <DriveContrlPanel :entry-count="listTotal" :upload-count="1" @on-search="loadList" @open-upload-queue="openFileUploadModal" />

    <!-- 条目列表 -->
    <DriveEntryGrid
      :data="listData"
      :loading="listLoading"
      :parent-id="listForm.parentId"
      @grid-right-click="handleGridRightClick"
      @entry-click="handleEntryClick"
      @entry-dblclick="handleEntryDoubleClick"
      @entry-right-click="handleEntryRightClick"
      @return-parent-dir="listReturnParentDir"
    />

    <!-- 右键菜单 -->
    <DriveEntryRightMenu
      ref="rightMenuRef"
      @create-folder="openCreateEntryModal"
      @upload-file="handleUploadFile"
      @preview="handlePreview"
      @download="handleDownload"
      @cut="handleCut"
      @copy="handleCopy"
      @delete="handleDelete"
      @rename="handleRename"
      @properties="handleProperties"
    />

    <!-- 创建文件夹模态框 -->
    <DriveCreateEntryModal ref="createEntryModalRef" :parent-id="listForm.parentId" @success="loadList" />

    <!-- 文件上传队列模态框 -->
    <DriveFileUpload ref="fileUploadRef" kind="drive" @success="loadList" />

    <input type="file" ref="fileInput" style="display: none" @change="onFileSelected" />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import DriveApi, { type GetEntryListDto, type GetEntryListVo } from "@/api/drive/DriveApi.ts";
import DriveCreateEntryModal from "@/views/drive/components/DriveCreateEntryModal.vue";
import DriveEntryGrid from "@/views/drive/components/DriveEntryGrid.vue";
import DriveEntryRightMenu from "@/views/drive/components/DriveEntryRightMenu.vue";
import DriveFileUpload from "@/views/drive/components/DriveFileUpload.vue";
import { Result } from "@/commons/entity/Result";
import DriveContrlPanel from "@/views/drive/components/DriveContrlPanel.vue";

const fileInput = ref<HTMLInputElement | null>(null);
const uploadFile = ref<File | null>(null);
const fileUploadRef = ref<InstanceType<typeof DriveFileUpload> | null>(null);
const createEntryModalRef = ref<InstanceType<typeof DriveCreateEntryModal> | null>(null);
const rightMenuRef = ref<InstanceType<typeof DriveEntryRightMenu> | null>(null);

const listForm = reactive<GetEntryListDto>({
  parentId: null,
  keyword: null,
  pageNum: 1,
  pageSize: 50000,
});

const listData = ref<GetEntryListVo[]>([]);
const listTotal = ref(0);
const listLoading = ref(false);

const currentSelectedEntry = ref<GetEntryListVo | null>(null);

const handleEntryClick = (id: string) => {
  const entry = listData.value.find((item) => item.id === id);
  if (entry) {
    currentSelectedEntry.value = entry;
  }
};

const handleGridRightClick = (event: MouseEvent) => {
  rightMenuRef.value?.openMenu(event, null);
};

const handleEntryRightClick = (event: MouseEvent, entry: GetEntryListVo) => {
  currentSelectedEntry.value = entry;
  rightMenuRef.value?.openMenu(event, entry);
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

const handleUploadFile = () => {
  fileInput.value?.click();
};

const onFileSelected = (event: Event) => {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files.length > 0) {
    uploadFile.value = input.files[0];
  }
  input.value = "";
};

const handlePreview = (entry: GetEntryListVo) => {
  // 预览逻辑，可根据需要实现
  ElMessage.info("预览功能待实现");
};

const handleDownload = (entry: GetEntryListVo) => {
  // 下载逻辑，可根据需要实现
  ElMessage.info("下载功能待实现");
};

const handleCut = (entry: GetEntryListVo) => {
  // 剪切逻辑，可根据需要实现
  ElMessage.info("剪切功能待实现");
};

const handleCopy = (entry: GetEntryListVo) => {
  // 复制逻辑，可根据需要实现
  ElMessage.info("复制功能待实现");
};

const handleDelete = (entry: GetEntryListVo) => {
  // 删除逻辑，可根据需要实现
  ElMessage.info("删除功能待实现");
};

const handleRename = (entry: GetEntryListVo) => {
  // 重命名逻辑，可根据需要实现
  ElMessage.info("重命名功能待实现");
};

const handleProperties = (entry: GetEntryListVo) => {
  // 属性逻辑，可根据需要实现
  ElMessage.info("属性功能待实现");
};

loadList();

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
 * 打开创建文件夹弹窗
 */
const openCreateEntryModal = () => {
  createEntryModalRef.value?.openModal();
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
