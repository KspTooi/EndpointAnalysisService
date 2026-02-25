<template>
  <div class="control-panel">
    <!-- 上层：搜索、容量信息与操作按钮 -->
    <div class="panel-row top-row">
      <div class="search-section">
        <!-- 云盘空间选择器 -->
        <el-select
          v-model="selectedSpaceId"
          placeholder="选择云盘空间"
          class="space-select"
          :loading="spaceLoading"
          filterable
          :teleported="false"
          @change="onSpaceChange"
        >
          <template #prefix>
            <el-icon><Box /></el-icon>
          </template>
          <el-option v-for="space in spaceList" :key="space.id" :label="space.name" :value="space.id">
            <div class="space-option">
              <el-icon><Box /></el-icon>
              <span>{{ space.name }}</span>
            </div>
          </el-option>
        </el-select>

        <el-input
          v-model="keyword"
          placeholder="在团队云盘中搜索..."
          clearable
          @clear="onSearch"
          @keyup.enter="onSearch"
          @input="onSearchInput"
          class="search-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <span class="entry-count">共 {{ entryCount }} 项</span>
      </div>

      <div class="info-action-group">
        <div class="drive-info-section" @click="onRefreshDriveInfo">
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

        <div class="action-section">
          <el-button type="success" @click="onViewUploadQueue" class="action-btn">
            <el-icon><Upload /></el-icon>
            <span
              >查看文件上传队列<span v-show="uploadCount > 0">({{ uploadCount }})</span></span
            >
          </el-button>
        </div>
      </div>
    </div>

    <!-- 下层：目录路径 -->
    <div v-show="reversedPaths.length > 0">
      <div class="path-section">
        <DriveControlPanelPaths :paths="reversedPaths" @on-path-change="onPathClick" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { Search, Upload, Coin, Box } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { storeToRefs } from "pinia";
import DriveApi from "@/views/drive/api/DriveApi.ts";
import type Result from "@/commons/entity/Result.ts";
import type { GetDriveInfoVo, GetEntryListPathVo } from "@/views/drive/api/DriveTypes.ts";
import { DriveStore } from "@/views/drive/service/DriveStore.ts";
import DriveControlPanelPaths from "@/views/drive/components/DriveControlPanelPaths.vue";
import DriveSpaceService from "@/views/drive/service/DriveSpaceService.ts";

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
  (e: "refresh-drive-info", result: Result<GetDriveInfoVo>): void; //刷新云盘信息
  (e: "on-path-change", path: GetEntryListPathVo): void; //当前目录路径变更
}>();

const keyword = ref("");
let searchTimer: number | null = null;

const driveStore = DriveStore();
const { currentDirPaths } = storeToRefs(driveStore);

//云盘空间选择器
const { spaceList, spaceLoading } = DriveSpaceService.useDriveSpaceSelector();
const selectedSpaceId = ref<string>(driveStore.currentDriveSpace?.id ?? "");

/**
 * 云盘空间切换
 */
const onSpaceChange = (id: string) => {
  const space = spaceList.value.find((s) => s.id === id) ?? null;
  driveStore.setCurrentDriveSpace(space);
};

const reversedPaths = computed(() => {
  return [...currentDirPaths.value].reverse();
});

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
  emit("refresh-drive-info", result);
  if (result.code === 0) {
    driveInfo.value = result.data;
  }
};

const onRefreshDriveInfo = async () => {
  const result = await DriveApi.getDriveInfo();
  emit("refresh-drive-info", result);
  if (result.code === 0) {
    driveInfo.value = result.data;
    ElMessage.success("云盘信息已刷新");
    return;
  }
  ElMessage.error("刷新云盘信息失败");
};

const onSearch = () => {
  emit("on-search", keyword.value);
};

const onSearchInput = () => {
  if (searchTimer) {
    clearTimeout(searchTimer);
  }

  searchTimer = setTimeout(() => {
    emit("on-search", keyword.value);
  }, 500);
};

const onViewUploadQueue = () => {
  emit("open-upload-queue");
};

const onPathClick = (path: GetEntryListPathVo | null) => {
  emit("on-path-change", path as any);
};

onMounted(() => {
  loadDriveInfo();
});
</script>

<style scoped>
.control-panel {
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
  padding: 8px 12px;
  margin-bottom: 8px;
  border-radius: 0;
  user-select: none;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.panel-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  width: 100%;
}

.top-row {
  padding-bottom: 2px;
}

.bottom-row {
  border-top: 1px solid var(--el-border-color-lighter);
  padding-top: 4px;
}

.info-action-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-section {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.space-select :deep(.el-select__popper),
.space-select :deep(.el-select-dropdown),
.space-select :deep(.el-popper) {
  border-radius: 0 !important;
}

.space-select :deep(.el-select-dropdown__list) {
  padding: 0;
}

.space-select :deep(.el-select-dropdown__item) {
  border-radius: 0 !important;
}

.space-select {
  width: 220px;
  flex-shrink: 0;
  --el-border-radius-base: 0px;
  --el-input-border-radius: 0px;
}

.space-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.space-select :deep(.el-input__wrapper) {
  border-radius: 0 !important;
  box-shadow: none !important;
  border: 1px solid var(--el-border-color);
  background-color: var(--el-fill-color-lighter);
  transition: all 0.2s;
}

.space-select :deep(.el-input__wrapper:hover) {
  border-color: var(--el-border-color-hover);
  background-color: var(--el-fill-color-light);
}

.space-select :deep(.el-input__wrapper.is-focus) {
  border-color: var(--el-color-primary);
  background-color: var(--el-bg-color);
}

.space-select :deep(.el-input__inner) {
  font-weight: 500;
}

.space-select :deep(.el-input__prefix) {
  color: var(--el-color-primary);
}

.search-input {
  width: 280px;
}

.entry-count {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  white-space: nowrap;
}

.path-section {
  flex: 1;
  overflow: hidden;
}

.search-input :deep(.el-input__wrapper) {
  background: var(--el-fill-color-blank);
  border: 1px solid var(--el-border-color);
  padding: 0 8px;
  box-shadow: none;
  transition: all 0.2s;
  border-radius: 0;
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: var(--el-border-color-hover);
}

.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: var(--el-color-primary);
  background: var(--el-bg-color);
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
  font-size: 13px;
}

.drive-info-section {
  display: flex;
  align-items: center;
}

.drive-info-display {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 3px 10px;
  background: var(--el-fill-color-lighter);
  border: 1px solid var(--el-border-color-light);
  border-radius: 0;
  cursor: pointer;
  transition: all 0.2s;
}

.drive-info-display:hover {
  background: var(--el-fill-color-light);
  border-color: var(--el-border-color);
}

.drive-info-display .el-icon {
  font-size: 14px;
  color: var(--el-color-primary);
}

.drive-info-text {
  font-size: 12px;
  color: var(--el-text-color-regular);
  white-space: nowrap;
}

.drive-progress {
  width: 70px;
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
  padding: 0 12px;
  font-size: 12px;
  font-weight: normal;
  border-radius: 0;
  height: 28px;
}

.action-btn .el-icon {
  margin-right: 4px;
  font-size: 13px;
}

@media (max-width: 992px) {
  .search-input {
    width: 200px;
  }
}

@media (max-width: 768px) {
  .panel-row {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }

  .top-row {
    padding-bottom: 0;
  }

  .bottom-row {
    padding-top: 4px;
  }

  .info-action-group {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }

  .search-input {
    width: 100%;
  }

  .drive-info-display {
    width: 100%;
    justify-content: center;
  }

  .action-btn {
    width: 100%;
  }
}
</style>
