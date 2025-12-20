<template>
  <div class="control-panel">
    <div class="panel-content">
      <!-- 左侧搜索区域 -->
      <div class="search-section">
        <el-input v-model="keyword" placeholder="在团队云盘中搜索..." clearable @clear="handleSearch" @keyup.enter="handleSearch" @input="handleSearchInput" class="search-input">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <span class="entry-count">共 {{ entryCount }} 项</span>
      </div>

      <!-- 右侧操作区域 -->
      <div class="action-section">
        <el-button type="success" @click="handleUploadQueue" class="action-btn">
          <el-icon><Upload /></el-icon>
          <span
            >查看文件上传队列<span v-show="uploadCount > 0">({{ uploadCount }})</span></span
          >
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { Search, Upload } from "@element-plus/icons-vue";

//定义props
const props = withDefaults(
  defineProps<{
    entryCount?: number; //当前文件夹中的条目数量
    uploadCount?: number; //上传队列中的文件数量
  }>(),
  {
    entryCount: 0,
    uploadCount: 0,
  }
);

//定义emits
const emit = defineEmits<{
  (e: "on-search", keyword: string): void; //搜索时触发
  (e: "open-upload-queue"): void; //打开上传队列
}>();

const keyword = ref("");
let searchTimer: number | null = null;

const handleSearch = () => {
  emit("on-search", keyword.value);
};

const handleSearchInput = () => {
  if (searchTimer) {
    clearTimeout(searchTimer);
  }

  searchTimer = setTimeout(() => {
    emit("on-search", keyword.value);
  }, 500);
};

const handleUploadQueue = () => {
  emit("open-upload-queue");
};
</script>

<style scoped>
.control-panel {
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
  padding: 10px 12px;
  margin-bottom: 10px;
  border-radius: 0;
}

.panel-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.search-section {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 300px;
}

.search-input {
  flex: 1;
  max-width: 450px;
}

.entry-count {
  color: var(--el-text-color-secondary);
  font-size: 13px;
  white-space: nowrap;
}

.search-input :deep(.el-input__wrapper) {
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
  padding: 0px 10px;
  box-shadow: none;
  transition: border-color 0.2s;
  border-radius: 0;
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: var(--el-border-color-hover);
}

.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: var(--el-color-primary);
}

.search-input :deep(.el-input__inner) {
  color: var(--el-text-color-primary);
  font-size: 13px;
  height: 28px;
  line-height: 28px;
}

.search-input :deep(.el-input__inner::placeholder) {
  color: var(--el-text-color-placeholder);
}

.search-input :deep(.el-input__prefix) {
  font-size: 14px;
}

.action-section {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  padding: 6px 14px;
  font-size: 13px;
  font-weight: normal;
  border-radius: 0;
  height: 30px;
}

.action-btn .el-icon {
  margin-right: 4px;
  font-size: 14px;
}

@media (max-width: 768px) {
  .panel-content {
    flex-direction: column;
  }

  .search-section {
    width: 100%;
    min-width: auto;
  }

  .search-input {
    max-width: 100%;
  }

  .action-section {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
