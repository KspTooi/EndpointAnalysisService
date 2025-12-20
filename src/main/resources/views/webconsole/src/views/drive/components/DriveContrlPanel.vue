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
          <span>上传队列({{ uploadCount }})</span>
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
  background: #f5f7fa;
  border: 1px solid #dcdfe6;
  padding: 12px;
  margin-bottom: 12px;
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
  max-width: 500px;
}

.entry-count {
  color: #606266;
  font-size: 14px;
  white-space: nowrap;
}

.search-input :deep(.el-input__wrapper) {
  background: #ffffff;
  border: 1px solid #dcdfe6;
  padding: 1px 11px;
  box-shadow: none;
  transition: border-color 0.2s;
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: #c0c4cc;
}

.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
}

.search-input :deep(.el-input__inner) {
  color: #333;
  font-size: 14px;
}

.search-input :deep(.el-input__inner::placeholder) {
  color: #a8abb2;
}

.action-section {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
}

.action-btn .el-icon {
  margin-right: 4px;
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
