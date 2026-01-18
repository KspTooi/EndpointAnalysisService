<template>
  <el-dialog v-model="modalVisible" title="移动冲突确认" width="500px" :close-on-click-modal="false" @contextmenu.prevent>
    <div class="modal-content">
      <div class="conflict-tip">
        <el-icon class="warning-icon"><Warning /></el-icon>
        <span>检测到以下文件/文件夹名称冲突：</span>
      </div>
      <div class="conflict-list">
        <div v-for="(name, index) in conflictNames" :key="index" class="conflict-item">
          {{ name }}
        </div>
      </div>
      <div class="action-tip">请选择处理方式：</div>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button @click="handleSkip">跳过</el-button>
        <el-button type="primary" @click="handleOverwrite">覆盖</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from "vue";
import { Warning } from "@element-plus/icons-vue";
import GenricHotkeyService from "@/service/GenricHotkeyService";

const modalVisible = ref(false);
const conflictNames = ref<string[]>([]);
let resolvePromise: ((value: number) => void) | null = null;

const openModal = (names: string[]): Promise<number> => {
  if (!names || names.length === 0) {
    return Promise.resolve(-1);
  }

  conflictNames.value = names;
  modalVisible.value = true;

  return new Promise<number>((resolve) => {
    resolvePromise = resolve;
  });
};

const handleOverwrite = () => {
  const action = 0;
  modalVisible.value = false;
  if (resolvePromise) {
    resolvePromise(action);
    resolvePromise = null;
  }
};

const handleSkip = () => {
  const action = 1;
  modalVisible.value = false;
  if (resolvePromise) {
    resolvePromise(action);
    resolvePromise = null;
  }
};

const handleCancel = () => {
  const action = -1;
  modalVisible.value = false;
  if (resolvePromise) {
    resolvePromise(action);
    resolvePromise = null;
  }
};

defineExpose({
  openModal,
});

watch(modalVisible, (val) => {
  if (val) {
    nextTick(() => {
      const overlay = document.querySelector(".el-overlay");
      if (overlay) {
        overlay.addEventListener("contextmenu", (e) => {
          e.preventDefault();
          e.stopPropagation();
        });
      }
    });
    return;
  }

  if (!val && resolvePromise) {
    const action = -1;
    resolvePromise(action);
    resolvePromise = null;
  }
});

//快捷键功能打包
GenricHotkeyService.useHotkeyFunction(
  {
    enter: handleOverwrite,
  },
  modalVisible,
  true
);
</script>

<style scoped>
:deep(.el-overlay) {
  user-select: none;
}

:deep(.el-overlay) * {
  user-select: none;
}

.modal-content {
  padding: 20px 0;
}

.conflict-tip {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  color: #e6a23c;
  font-size: 14px;
}

.warning-icon {
  margin-right: 8px;
  font-size: 18px;
}

.conflict-list {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 12px;
  margin-bottom: 16px;
  background-color: #f5f7fa;
}

.conflict-item {
  padding: 8px 0;
  color: #606266;
  font-size: 14px;
  border-bottom: 1px solid #ebeef5;
}

.conflict-item:last-child {
  border-bottom: none;
}

.action-tip {
  color: #909399;
  font-size: 13px;
  margin-bottom: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
