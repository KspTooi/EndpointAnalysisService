<template>
  <div v-show="visible" ref="menuRef" class="context-menu" :style="{ left: x + 'px', top: y + 'px' }" @click.stop @contextmenu.prevent>
    <!-- 当没有选中条目时，只显示新建文件夹和上传文件 -->
    <template v-if="!currentEntry">
      <div class="menu-item" @click="onCreateFolder">
        <el-icon><FolderAdd /></el-icon>
        <span>新建文件夹</span>
      </div>
      <div class="menu-item" @click="onUploadFile">
        <el-icon><Upload /></el-icon>
        <span>上传文件</span>
      </div>
    </template>

    <!-- 当单选条目时，显示完整菜单 -->
    <template v-if="currentEntry && !isMultiSelect">
      <div v-if="isMultiSelect && currentEntry.kind === 0" class="menu-item" @click="onPreview">
        <el-icon><View /></el-icon>
        <span>预览</span>
      </div>
      <div v-if="currentEntry.kind === 0" class="menu-item" @click="onDownload">
        <el-icon><Download /></el-icon>
        <span>下载</span>
      </div>
      <div v-if="currentEntry.kind === 0" class="menu-divider"></div>
      <div class="menu-item" @click="onCut">
        <el-icon><Scissor /></el-icon>
        <span>剪切</span>
      </div>
      <div class="menu-item" @click="onCopy">
        <el-icon><DocumentCopy /></el-icon>
        <span>复制</span>
      </div>
      <div class="menu-divider"></div>
      <div class="menu-item" @click="onDelete">
        <el-icon><Delete /></el-icon>
        <span>删除</span>
      </div>
      <div class="menu-item" @click="onRename">
        <el-icon><Edit /></el-icon>
        <span>重命名</span>
      </div>
      <div class="menu-divider"></div>
      <div class="menu-item" @click="onProperties">
        <el-icon><InfoFilled /></el-icon>
        <span>属性</span>
      </div>
    </template>

    <!-- 当多选条目时，显示完整菜单 -->
    <template v-if="isMultiSelect">
      <div class="menu-item" @click="onDelete">
        <el-icon><Delete /></el-icon>
        <span>删除</span>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from "vue";
import { FolderAdd, View, Download, Scissor, DocumentCopy, Delete, Edit, InfoFilled, Upload } from "@element-plus/icons-vue";
import type { GetEntryListVo } from "@/api/drive/DriveApi";

const emit = defineEmits<{
  (e: "on-create-folder"): void; //创建文件夹
  (e: "on-upload-file"): void; //上传文件
  (e: "on-preview", entry: GetEntryListVo): void; //预览
  (e: "on-download", entries: GetEntryListVo[]): void; //下载
  (e: "on-cut", entries: GetEntryListVo[]): void; //剪切
  (e: "on-copy", entries: GetEntryListVo[]): void; //复制
  (e: "on-delete", entries: GetEntryListVo[]): void; //删除
  (e: "on-rename", entry: GetEntryListVo): void; //重命名
  (e: "on-properties", entry: GetEntryListVo): void; //属性
}>();

const visible = ref(false);
const x = ref(0);
const y = ref(0);

//选择的条目列表
const currentEntries = ref<GetEntryListVo[]>([]);
//当前选中的条目
const currentEntry = computed(() => {
  return currentEntries.value[0] || null;
});
//是否多选
const isMultiSelect = computed(() => {
  return currentEntries.value.length > 1;
});
const menuRef = ref<HTMLElement>();

/**
 * 打开右键菜单
 * @param event 鼠标事件
 * @param entries 当前条目，可以为空，表示没有选中条目
 */
const openMenu = (event: MouseEvent, entries: GetEntryListVo[] | null = null) => {
  x.value = event.clientX;
  y.value = event.clientY;
  currentEntries.value = entries || [];
  visible.value = true;
};

/**
 * 关闭右键菜单
 */
const closeMenu = () => {
  visible.value = false;
};

/**
 * 创建文件夹
 */
const onCreateFolder = () => {
  emit("on-create-folder");
  closeMenu();
};

/**
 * 上传文件
 */
const onUploadFile = () => {
  emit("on-upload-file");
  closeMenu();
};

/**
 * 预览
 */
const onPreview = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("on-preview", currentEntry.value);
  closeMenu();
};

//下载
const onDownload = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("on-download", currentEntries.value);
  closeMenu();
};

/**
 * 剪切
 */
const onCut = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("on-cut", currentEntries.value);
  closeMenu();
};

/**
 * 复制
 */
const onCopy = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("on-copy", currentEntries.value);
  closeMenu();
};

/**
 * 删除
 */
const onDelete = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("on-delete", currentEntries.value);
  closeMenu();
};

/**
 * 重命名
 */
const onRename = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("on-rename", currentEntry.value);
  closeMenu();
};

/**
 * 属性
 */
const onProperties = () => {
  if (!currentEntry.value) {
    return;
  }
  emit("on-properties", currentEntry.value);
  closeMenu();
};

/**
 * 点击外部关闭菜单
 */
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
