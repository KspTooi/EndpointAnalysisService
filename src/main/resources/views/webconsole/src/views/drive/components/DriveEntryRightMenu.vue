<template>
  <div v-show="visible" ref="menuRef" class="context-menu" :style="{ left: x + 'px', top: y + 'px' }" @click.stop @contextmenu.prevent>
    <!-- 当没有选中条目时，只显示新建文件夹和上传文件 -->
    <template v-if="!currentEntry">
      <div class="menu-item" @click="handleCreateFolder">
        <el-icon><FolderAdd /></el-icon>
        <span>新建文件夹</span>
      </div>
      <div class="menu-item" @click="handleUploadFile">
        <el-icon><Upload /></el-icon>
        <span>上传文件</span>
      </div>
    </template>

    <!-- 当选中条目时，显示完整菜单 -->
    <template v-else>
      <div v-if="currentEntry.kind === 0" class="menu-item" @click="handlePreview">
        <el-icon><View /></el-icon>
        <span>预览</span>
      </div>
      <div v-if="currentEntry.kind === 0" class="menu-item" @click="handleDownload">
        <el-icon><Download /></el-icon>
        <span>下载</span>
      </div>
      <div v-if="currentEntry.kind === 0" class="menu-divider"></div>
      <div class="menu-item" @click="handleCut">
        <el-icon><Scissor /></el-icon>
        <span>剪切</span>
      </div>
      <div class="menu-item" @click="handleCopy">
        <el-icon><DocumentCopy /></el-icon>
        <span>复制</span>
      </div>
      <div class="menu-divider"></div>
      <div class="menu-item" @click="handleDelete">
        <el-icon><Delete /></el-icon>
        <span>删除</span>
      </div>
      <div class="menu-item" @click="handleRename">
        <el-icon><Edit /></el-icon>
        <span>重命名</span>
      </div>
      <div class="menu-divider"></div>
      <div class="menu-item" @click="handleProperties">
        <el-icon><InfoFilled /></el-icon>
        <span>属性</span>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from "vue";
import { FolderAdd, View, Download, Scissor, DocumentCopy, Delete, Edit, InfoFilled, Upload } from "@element-plus/icons-vue";
import type { GetEntryListVo } from "@/api/drive/DriveApi";

const emit = defineEmits<{
  (e: "createFolder"): void;
  (e: "uploadFile"): void;
  (e: "preview", entry: GetEntryListVo): void;
  (e: "download", entry: GetEntryListVo): void;
  (e: "cut", entry: GetEntryListVo): void;
  (e: "copy", entry: GetEntryListVo): void;
  (e: "delete", entry: GetEntryListVo): void;
  (e: "rename", entry: GetEntryListVo): void;
  (e: "properties", entry: GetEntryListVo): void;
}>();

const visible = ref(false);
const x = ref(0);
const y = ref(0);
const currentEntry = ref<GetEntryListVo | null>(null);
const menuRef = ref<HTMLElement>();

const openMenu = (event: MouseEvent, entry: GetEntryListVo | null = null) => {
  x.value = event.clientX;
  y.value = event.clientY;
  currentEntry.value = entry;
  visible.value = true;
};

const closeMenu = () => {
  visible.value = false;
};

const handleCreateFolder = () => {
  emit("createFolder");
  closeMenu();
};

const handleUploadFile = () => {
  emit("uploadFile");
  closeMenu();
};

const handlePreview = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("preview", currentEntry.value);
  closeMenu();
};

const handleDownload = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("download", currentEntry.value);
  closeMenu();
};

const handleCut = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("cut", currentEntry.value);
  closeMenu();
};

const handleCopy = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("copy", currentEntry.value);
  closeMenu();
};

const handleDelete = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("delete", currentEntry.value);
  closeMenu();
};

const handleRename = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("rename", currentEntry.value);
  closeMenu();
};

const handleProperties = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("properties", currentEntry.value);
  closeMenu();
};

const handleClickOutside = (event: MouseEvent) => {
  if (menuRef.value && !menuRef.value.contains(event.target as Node)) {
    closeMenu();
  }
};

onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
});

defineExpose({
  openMenu,
});
</script>

<style scoped>
.context-menu {
  position: fixed;
  background: #ffffff;
  border: 1px solid #d4d4d4;
  border-radius: 2px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 2000;
  min-width: 160px;
  padding: 2px 0;
  font-size: 13px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 20px 6px 28px;
  cursor: pointer;
  color: #333333;
  transition: background-color 0.1s;
  user-select: none;
  white-space: nowrap;
}

.menu-item:hover {
  background: #e8f4ff;
  color: #000000;
}

.menu-item .el-icon {
  font-size: 14px;
  width: 16px;
  text-align: center;
}

.menu-divider {
  height: 1px;
  background: #e5e5e5;
  margin: 2px 0;
}
</style>
