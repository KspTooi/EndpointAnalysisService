<template>
  <Teleport to="body">
    <div v-if="isVisible" class="confirm-overlay" @click.self="handleCancel" @contextmenu.prevent>
      <div class="confirm-dialog">
        <!-- 标题栏 -->
        <div class="dialog-header">
          <span class="dialog-title">删除确认</span>
          <button class="dialog-close" @click="handleCancel" :disabled="loading">
            <el-icon><Close /></el-icon>
          </button>
        </div>

        <!-- 内容区域 -->
        <div class="dialog-content">
          <!-- 图标和提示 -->
          <div class="content-header">
            <div class="warning-icon">
              <el-icon :size="48" color="#f56c6c">
                <Delete />
              </el-icon>
            </div>
            <div class="warning-text">
              <p class="warning-title">确定要永久删除{{ entries.length > 1 ? `这 ${entries.length} 个项目` : "此项目" }}吗？</p>
              <p class="warning-subtitle">删除后将无法恢复</p>
            </div>
          </div>

          <!-- 文件列表 -->
          <div class="file-list">
            <div v-for="item in entries" :key="item.id" class="file-item">
              <el-icon class="file-icon" :class="{ 'folder-icon': item.kind === 1, 'file-icon-type': item.kind === 0 }">
                <Folder v-if="item.kind === 1" />
                <Document v-else />
              </el-icon>
              <div class="file-info">
                <div class="file-name">{{ item.name }}</div>
                <div class="file-meta">
                  <span>{{ item.kind === 1 ? "文件夹" : getFileType(item.attachSuffix) }}</span>
                  <span v-if="item.kind === 0 && item.attachSize" class="file-size">{{ formatFileSize(item.attachSize) }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 错误提示 -->
          <div v-if="errorMessage" class="error-message">
            <el-icon color="#f56c6c"><WarningFilled /></el-icon>
            <span>{{ errorMessage }}</span>
          </div>
        </div>

        <!-- 按钮区域 -->
        <div class="dialog-footer">
          <el-button @click="handleCancel" :disabled="loading">取消</el-button>
          <el-button type="danger" @click="handleConfirm" :loading="loading" :disabled="loading">确定删除</el-button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { ElMessage } from "element-plus";
import { Close, Delete, Folder, Document, WarningFilled } from "@element-plus/icons-vue";
import DriveApi from "@/views/drive/api/DriveApi.ts";
import { Result } from "@/commons/entity/Result";
import type { EntryPo } from "@/views/drive/api/DriveTypes.ts";
import GenricHotkeyService from "@/commons/service/GenricHotkeyService";

const emit = defineEmits<{
  (e: "success"): void;
}>();

const isVisible = ref(false);
const loading = ref(false);
const entries = ref<EntryPo[]>([]);
const errorMessage = ref("");
let resolvePromise: ((value: boolean) => void) | null = null;

const openConfirm = (pos: EntryPo[]): Promise<boolean> => {
  entries.value = pos;
  isVisible.value = true;
  loading.value = false;
  errorMessage.value = "";

  return new Promise<boolean>((resolve) => {
    resolvePromise = resolve;
  });
};

const handleConfirm = async () => {
  if (loading.value) {
    return;
  }

  loading.value = true;
  errorMessage.value = "";

  try {
    const ids = entries.value.map((entry) => entry.id);
    const result = await DriveApi.deleteEntry({ ids });

    if (Result.isSuccess(result)) {
      ElMessage.success(`成功删除 ${entries.value.length} 个项目`);
      isVisible.value = false;
      loading.value = false;
      if (resolvePromise) {
        resolvePromise(true);
        resolvePromise = null;
      }
      emit("success");
      return;
    }

    if (Result.isError(result)) {
      errorMessage.value = result.message || "删除失败";
      ElMessage.error(errorMessage.value);
    }
  } catch (error: any) {
    errorMessage.value = error.message || "删除失败";
    ElMessage.error(errorMessage.value);
  }

  loading.value = false;
};

const handleCancel = () => {
  if (loading.value) {
    return;
  }

  isVisible.value = false;
  if (!resolvePromise) {
    return;
  }
  resolvePromise(false);
  resolvePromise = null;
};

const formatFileSize = (bytes: string): string => {
  const bytesNum = parseInt(bytes);

  if (!bytesNum || bytesNum === 0) {
    return "-";
  }
  if (bytesNum < 1024) {
    return bytesNum + " B";
  }
  if (bytesNum < 1024 * 1024) {
    return (bytesNum / 1024).toFixed(2) + " KB";
  }
  if (bytesNum < 1024 * 1024 * 1024) {
    return (bytesNum / (1024 * 1024)).toFixed(2) + " MB";
  }
  return (bytesNum / (1024 * 1024 * 1024)).toFixed(2) + " GB";
};

const getFileType = (suffix: string | null): string => {
  if (!suffix) {
    return "文件";
  }
  return suffix.toUpperCase() + " 文件";
};

defineExpose({
  openConfirm,
});

//快捷键功能打包
GenricHotkeyService.useHotkeyFunction(
  {
    enter: handleConfirm,
    esc: handleCancel,
  },
  isVisible,
  true
);
</script>

<style scoped>
.confirm-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  user-select: none;
}

.confirm-dialog {
  background: var(--el-bg-color);
  border-radius: 0;
  box-shadow: var(--el-box-shadow-dark);
  min-width: 420px;
  max-width: 520px;
  border: 1px solid var(--el-border-color);
  overflow: hidden;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  background: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.dialog-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.dialog-close {
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 2px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-regular);
  border-radius: 0;
  transition: all 0.2s;
}

.dialog-close:hover {
  background: var(--el-fill-color-light);
  color: var(--el-text-color-primary);
}

.dialog-content {
  padding: 16px;
}

.content-header {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  align-items: flex-start;
}

.warning-icon {
  flex-shrink: 0;
}

.warning-text {
  flex: 1;
}

.warning-title {
  font-size: 14px;
  font-weight: 500;
  margin: 0 0 6px 0;
  color: var(--el-text-color-primary);
}

.warning-subtitle {
  font-size: 12px;
  margin: 0;
  color: var(--el-text-color-secondary);
}

.file-list {
  background: var(--el-fill-color-lighter);
  border: 1px solid var(--el-border-color-light);
  border-radius: 0;
  max-height: 280px;
  overflow-y: auto;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 10px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  transition: background-color 0.2s;
}

.file-item:last-child {
  border-bottom: none;
}

.file-item:hover {
  background: var(--el-fill-color-light);
}

.file-icon {
  flex-shrink: 0;
  font-size: 18px;
}

.folder-icon {
  color: #409eff;
}

.file-icon-type {
  color: var(--el-text-color-regular);
}

.file-info {
  flex: 1;
  min-width: 0;
}

.file-name {
  font-size: 12px;
  color: var(--el-text-color-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 3px;
}

.file-meta {
  font-size: 11px;
  color: var(--el-text-color-secondary);
  display: flex;
  gap: 8px;
}

.file-size::before {
  content: "•";
  margin: 0 3px;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  margin-top: 12px;
  background: rgba(245, 108, 108, 0.1);
  border: 1px solid rgba(245, 108, 108, 0.3);
  border-radius: 0;
  font-size: 12px;
  color: var(--el-text-color-primary);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 10px 16px;
  background: var(--el-fill-color-lighter);
  border-top: 1px solid var(--el-border-color-lighter);
}

:deep(.el-button) {
  border-radius: 0;
  padding: 8px 16px;
  font-size: 13px;
}
</style>
