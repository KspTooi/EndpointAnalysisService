<template>
  <div class="control-panel">
    <div class="panel-content">
      <!-- 左侧搜索区域 -->
      <div class="search-section">
        <el-input
          v-model="keyword"
          placeholder="在团队云盘中搜索..."
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
          @input="handleSearchInput"
          class="search-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <span class="entry-count">共 {{ entryCount }} 项</span>
      </div>

      <!-- 云盘信息区域 -->
      <div class="drive-info-section" @click="handleRefreshDriveInfo">
        <el-tooltip effect="dark" placement="bottom">
          <template #content>
            <div class="drive-info-tooltip">
              <div>总容量: {{ formatSize(driveInfo.totalCapacity) }}</div>
              <div>已使用: {{ formatSize(driveInfo.usedCapacity) }}</div>
              <div>对象数量: {{ driveInfo.objectCount }}</div>
              <div style="margin-top: 8px; color: #909399">点击刷新云盘信息</div>
            </div>
          </template>
          <div class="drive-info-display">
            <el-icon><Coin /></el-icon>
            <span class="drive-info-text"
              >{{ formatSize(driveInfo.usedCapacity) }} / {{ formatSize(driveInfo.totalCapacity) }}</span
            >
            <el-progress
              :percentage="usagePercentage"
              :stroke-width="4"
              :show-text="false"
              :color="progressColor"
              class="drive-progress"
            />
          </div>
        </el-tooltip>
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
import { ref, computed, onMounted } from "vue";
import { Search, Upload, Coin } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import DriveApi from "@/views/drive/api/DriveApi.ts";
import { Result } from "@/commons/entity/Result";
import type { GetDriveInfoVo } from "@/views/drive/api/DriveTypes.ts";

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

const driveInfo = ref<GetDriveInfoVo>({
  totalCapacity: "0",
  usedCapacity: "0",
  objectCount: "0",
});

const usagePercentage = computed(() => {
  const total = Number(driveInfo.value.totalCapacity);
  const used = Number(driveInfo.value.usedCapacity);

  if (!total || total === 0) {
    return 0;
  }

  return Math.round((used / total) * 100);
});

const progressColor = computed(() => {
  if (usagePercentage.value >= 90) {
    return "#f56c6c";
  }
  if (usagePercentage.value >= 70) {
    return "#e6a23c";
  }
  return "#67c23a";
});

const formatSize = (bytes: string | number | null | undefined): string => {
  const numBytes = Number(bytes);

  if (!numBytes || numBytes === 0 || isNaN(numBytes)) {
    return "0 B";
  }

  const k = 1024;
  const sizes = ["B", "KB", "MB", "GB", "TB"];
  const i = Math.floor(Math.log(numBytes) / Math.log(k));
  return Math.round((numBytes / Math.pow(k, i)) * 100) / 100 + " " + sizes[i];
};

const loadDriveInfo = async () => {
  const result = await DriveApi.getDriveInfo();
  if (Result.isSuccess(result)) {
    driveInfo.value = result.data;
  }
};

const handleRefreshDriveInfo = async () => {
  const result = await DriveApi.getDriveInfo();
  if (Result.isSuccess(result)) {
    driveInfo.value = result.data;
    ElMessage.success("云盘信息已刷新");
    return;
  }
  ElMessage.error("刷新云盘信息失败");
};

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

onMounted(() => {
  loadDriveInfo();
});
</script>

<style scoped>
.control-panel {
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
  padding: 10px 12px;
  margin-bottom: 10px;
  border-radius: 0;
  user-select: none;
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
  padding: 0 10px;
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

.drive-info-section {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.drive-info-display {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px;
  background: var(--el-fill-color-lighter);
  border: 1px solid var(--el-border-color);
  cursor: pointer;
  transition: all 0.2s;
}

.drive-info-display:hover {
  background: var(--el-fill-color-light);
  border-color: var(--el-border-color-hover);
}

.drive-info-display .el-icon {
  font-size: 14px;
  color: var(--el-color-primary);
}

.drive-info-text {
  font-size: 13px;
  color: var(--el-text-color-regular);
  white-space: nowrap;
}

.drive-progress {
  width: 60px;
}

.drive-info-tooltip {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
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

  .drive-info-section {
    width: 100%;
  }

  .drive-info-display {
    width: 100%;
  }

  .action-section {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
