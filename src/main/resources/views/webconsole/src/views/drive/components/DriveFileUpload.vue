<template>
  <el-dialog v-model="modalVisible" title="文件上传队列" width="600px" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="!uploading" class="modal-centered">
    <div class="upload-content">
      <div class="queue-header">
        <span class="queue-info">队列中共 {{ uploadQueue.length }} 个文件，已完成 {{ completedCount }} 个</span>
        <el-button v-if="uploading" type="danger" size="small" @click="handleCancelAll">取消全部</el-button>
      </div>

      <div class="queue-list">
        <div
          v-for="(item, index) in uploadQueue"
          :key="index"
          class="queue-item"
          :class="{ active: index === currentIndex, completed: item.status === 'completed', failed: item.status === 'failed' }"
        >
          <div class="item-info">
            <span class="item-name">{{ item.file.name }}</span>
            <span class="item-size">{{ formatFileSize(item.file.size) }}</span>
          </div>
          <div class="item-progress">
            <el-progress v-if="item.status === 'uploading'" :percentage="item.progress" :stroke-width="6" />
            <el-progress v-if="item.status === 'completed'" :percentage="100" status="success" :stroke-width="6" />
            <el-progress v-if="item.status === 'failed'" :percentage="item.progress" status="exception" :stroke-width="6" />
            <span v-if="item.status === 'pending'" class="pending-text">等待中...</span>
          </div>
          <div class="item-status">
            <span class="status-text">{{ item.statusText }}</span>
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { ElMessage } from "element-plus";
import AttachApi, { type PreCheckAttachDto, type ApplyChunkVo } from "@/api/core/AttachApi.ts";
import DriveApi, { type AddEntryDto } from "@/api/drive/DriveApi.ts";
import { Result } from "@/commons/entity/Result.ts";

export interface UploadQueueItem {
  file: File;
  parentId: string | null;
  status: "pending" | "uploading" | "completed" | "failed";
  progress: number;
  statusText: string;
}

const CHUNK_SIZE = 5 * 1024 * 1024; // 5MB

const props = defineProps<{
  kind: string;
}>();

const emit = defineEmits<{
  (e: "queue-update", queue: UploadQueueItem[]): void;
  (e: "success"): void;
}>();

const modalVisible = ref(false);
const uploadQueue = ref<UploadQueueItem[]>([]);
const uploading = ref(false);
const currentIndex = ref(-1);
const isCancelled = ref(false);

const completedCount = computed(() => {
  return uploadQueue.value.filter((item) => item.status === "completed").length;
});

const toUploadQueue = (item: UploadQueueItem) => {
  uploadQueue.value.push({
    ...item,
    status: "pending",
    progress: 0,
    statusText: "等待中...",
  });
  emit("queue-update", uploadQueue.value);

  if (!uploading.value) {
    processQueue();
  }
};

const handleCancelAll = () => {
  isCancelled.value = true;
  uploading.value = false;
  uploadQueue.value.forEach((item) => {
    if (item.status === "uploading" || item.status === "pending") {
      item.status = "failed";
      item.statusText = "已取消";
    }
  });
  ElMessage.info("已取消全部上传");
};

const processQueue = async () => {
  if (uploading.value) {
    return;
  }

  const pendingIndex = uploadQueue.value.findIndex((item) => item.status === "pending");
  if (pendingIndex === -1) {
    uploading.value = false;
    currentIndex.value = -1;
    emit("success");
    return;
  }

  uploading.value = true;
  currentIndex.value = pendingIndex;
  const item = uploadQueue.value[pendingIndex];

  await uploadFile(item);

  if (!isCancelled.value) {
    processQueue();
  }
};

const uploadFile = async (item: UploadQueueItem) => {
  item.status = "uploading";
  item.progress = 0;
  item.statusText = "正在计算哈希...";

  try {
    const sha256 = await computeSHA256(item.file);
    item.statusText = "正在预检...";

    const preCheckDto: PreCheckAttachDto = {
      name: item.file.name,
      kind: props.kind,
      totalSize: item.file.size.toString(),
      sha256: sha256,
    };

    const preCheckResult = await AttachApi.preCheckAttach(preCheckDto);
    if (Result.isError(preCheckResult)) {
      throw new Error(preCheckResult.message);
    }

    const { preCheckId, status, missingChunkIds } = preCheckResult.data;

    if (status === 3) {
      item.progress = 100;
      await createEntry(item.file, preCheckId, item.parentId);
      item.status = "completed";
      item.statusText = "上传完成";
      return;
    }

    const chunksToUpload = missingChunkIds.map((id: string) => parseInt(id));
    const totalChunks = chunksToUpload.length;

    if (totalChunks === 0) {
      item.progress = 100;
      await createEntry(item.file, preCheckId, item.parentId);
      item.status = "completed";
      item.statusText = "上传完成";
      return;
    }

    let uploadedCount = 0;
    let finalAttachId = preCheckId;

    for (const chunkId of chunksToUpload) {
      if (isCancelled.value) {
        return;
      }

      const start = chunkId * CHUNK_SIZE;
      const end = Math.min(start + CHUNK_SIZE, item.file.size);
      const chunk = item.file.slice(start, end);

      item.statusText = `正在上传区块 ${chunkId + 1}/${totalChunks}`;
      const result = await uploadChunk(preCheckId, chunkId, chunk);
      if (!result) {
        throw new Error("上传区块失败");
      }

      if (result.attachId) {
        finalAttachId = result.attachId;
      }

      uploadedCount++;
      item.progress = Math.round((uploadedCount / totalChunks) * 100);

      if (result.attachId && result.chunkApplied === result.chunkTotal) {
        await createEntry(item.file, result.attachId, item.parentId);
        item.status = "completed";
        item.statusText = "上传完成";
        return;
      }
    }

    await createEntry(item.file, finalAttachId, item.parentId);
    item.status = "completed";
    item.statusText = "上传完成";
  } catch (error: any) {
    if (isCancelled.value) {
      item.status = "failed";
      item.statusText = "已取消";
      return;
    }

    item.status = "failed";
    item.statusText = error.message || "上传失败";
    ElMessage.error(`${item.file.name} 上传失败: ${error.message}`);
  }
};

const computeSHA256 = async (file: File): Promise<string> => {
  const arrayBuffer = await file.arrayBuffer();
  const hashBuffer = await crypto.subtle.digest("SHA-256", arrayBuffer);
  const hashArray = Array.from(new Uint8Array(hashBuffer));
  return hashArray.map((b) => b.toString(16).padStart(2, "0")).join("");
};

const uploadChunk = async (preCheckId: string, chunkId: number, chunk: Blob): Promise<ApplyChunkVo | null> => {
  if (isCancelled.value) {
    throw new Error("上传已取消");
  }

  const file = new File([chunk], `chunk_${chunkId}`, { type: "application/octet-stream" });
  const ret = await AttachApi.applyChunk(preCheckId, chunkId.toString(), file);
  if (Result.isSuccess(ret)) {
    return ret.data;
  }

  return null;
};

const createEntry = async (file: File, attachId: string, parentId: string | null) => {
  const addEntryDto: AddEntryDto = {
    parentId: parentId,
    name: file.name,
    kind: 0,
    attachId: attachId,
  };
  const result = await DriveApi.addEntry(addEntryDto);
  if (result.code !== 0) {
    throw new Error(result.message || "创建条目失败");
  }
};

const formatFileSize = (bytes: number): string => {
  if (bytes === 0) {
    return "0 B";
  }

  const k = 1024;
  const sizes = ["B", "KB", "MB", "GB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + " " + sizes[i];
};

defineExpose({
  toUploadQueue,
  openModal: () => {
    modalVisible.value = true;
  },
});
</script>

<style scoped>
.upload-content {
  padding: 10px 0;
}

.queue-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.queue-info {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.queue-list {
  max-height: 400px;
  overflow-y: auto;
}

.queue-item {
  padding: 12px;
  margin-bottom: 12px;
  border: 1px solid #ebeef5;
  background: #f5f7fa;
  transition: all 0.3s;
}

.queue-item.active {
  border-color: #409eff;
  background: #ecf5ff;
}

.queue-item.completed {
  background: #f0f9ff;
  border-color: #b3e19d;
}

.queue-item.failed {
  background: #fef0f0;
  border-color: #fbc4c4;
}

.item-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.item-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 12px;
}

.item-size {
  font-size: 12px;
  color: #909399;
}

.item-progress {
  margin-bottom: 4px;
}

.pending-text {
  font-size: 12px;
  color: #909399;
}

.item-status {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.status-text {
  font-size: 12px;
  color: #606266;
}
</style>
