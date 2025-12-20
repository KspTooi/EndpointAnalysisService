<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="(val: boolean) => emit('update:visible', val)"
    title="文件上传"
    width="400px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    :show-close="!uploading"
  >
    <div class="upload-content">
      <div class="progress-container">
        <el-progress :percentage="progress" :status="uploadComplete ? 'success' : undefined" />
        <div class="status-bar">
          <span class="status-text">{{ progressText }}</span>
          <el-button v-if="uploading" type="danger" link @click="handleCancel">取消上传</el-button>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { ElMessage } from "element-plus";
import AttachApi, { type PreCheckAttachDto, type ApplyChunkVo } from "@/api/core/AttachApi.ts";
import DriveApi, { type AddEntryDto } from "@/api/drive/DriveApi.ts";
import { Result } from "@/commons/entity/Result.ts";

const CHUNK_SIZE = 5 * 1024 * 1024; // 5MB

const props = defineProps<{
  visible: boolean;
  parentId: string | null;
  kind: string;
  file: File | null;
}>();

const emit = defineEmits<{
  (e: "update:visible", visible: boolean): void;
  (e: "success"): void;
}>();

const uploading = ref(false);
const uploadComplete = ref(false);
const progress = ref(0);
const currentStatus = ref("");
const isCancelled = ref(false);

const progressText = computed(() => {
  if (uploadComplete.value) return "上传完成";
  return currentStatus.value || `上传中... ${progress.value}%`;
});

watch(
  () => props.visible,
  (val) => {
    if (val && props.file) {
      startUpload(props.file);
    }
  }
);

const handleCancel = () => {
  isCancelled.value = true;
  uploading.value = false;
  currentStatus.value = "已取消";
  ElMessage.info("上传已取消");
};

const computeSHA256 = async (file: File): Promise<string> => {
  const arrayBuffer = await file.arrayBuffer();
  const hashBuffer = await crypto.subtle.digest("SHA-256", arrayBuffer);
  const hashArray = Array.from(new Uint8Array(hashBuffer));
  return hashArray.map((b) => b.toString(16).padStart(2, "0")).join("");
};

const uploadChunk = async (preCheckId: string, chunkId: number, chunk: Blob): Promise<ApplyChunkVo | null> => {
  if (isCancelled.value) throw new Error("上传已取消");
  const file = new File([chunk], `chunk_${chunkId}`, { type: "application/octet-stream" });
  const ret = await AttachApi.applyChunk(preCheckId, chunkId.toString(), file);
  if (Result.isSuccess(ret)) return ret.data;
  return null;
};

const startUpload = async (file: File) => {
  uploading.value = true;
  uploadComplete.value = false;
  progress.value = 0;
  isCancelled.value = false;
  currentStatus.value = "正在计算哈希...";

  try {
    const sha256 = await computeSHA256(file);
    currentStatus.value = "正在预检...";

    const preCheckDto: PreCheckAttachDto = {
      name: file.name,
      kind: props.kind,
      totalSize: file.size.toString(),
      sha256: sha256,
    };

    const preCheckResult = await AttachApi.preCheckAttach(preCheckDto);
    if (Result.isError(preCheckResult)) throw new Error(preCheckResult.message);

    const { preCheckId, status, missingChunkIds } = preCheckResult.data;

    if (status === 3) {
      progress.value = 100;
      await createEntry(file, preCheckId);
      finishUpload();
      return;
    }

    const chunksToUpload = missingChunkIds.map((id: string) => parseInt(id));
    const totalChunks = chunksToUpload.length;

    if (totalChunks === 0) {
      progress.value = 100;
      await createEntry(file, preCheckId);
      finishUpload();
      return;
    }

    let uploadedCount = 0;
    let finalAttachId = preCheckId;

    for (const chunkId of chunksToUpload) {
      if (isCancelled.value) return;
      const start = chunkId * CHUNK_SIZE;
      const end = Math.min(start + CHUNK_SIZE, file.size);
      const chunk = file.slice(start, end);

      currentStatus.value = `正在上传区块 ${chunkId + 1}/${totalChunks}`;
      const result = await uploadChunk(preCheckId, chunkId, chunk);
      if (!result) throw new Error("上传区块失败");

      if (result.attachId) finalAttachId = result.attachId;
      uploadedCount++;
      progress.value = Math.round((uploadedCount / totalChunks) * 100);

      if (result.attachId && result.chunkApplied === result.chunkTotal) {
        await createEntry(file, result.attachId);
        finishUpload();
        return;
      }
    }

    await createEntry(file, finalAttachId);
    finishUpload();
  } catch (error: any) {
    if (isCancelled.value) return;
    uploading.value = false;
    ElMessage.error(error.message || "上传失败");
  }
};

const createEntry = async (file: File, attachId: string) => {
  const addEntryDto: AddEntryDto = {
    parentId: props.parentId,
    name: file.name,
    kind: 0,
    attachId: attachId,
  };
  const result = await DriveApi.addEntry(addEntryDto);
  if (result.code !== 0) throw new Error(result.message || "创建条目失败");
};

const finishUpload = () => {
  uploadComplete.value = true;
  uploading.value = false;
  currentStatus.value = "";
  ElMessage.success("上传完成");
  emit("success");
  setTimeout(() => emit("update:visible", false), 1000);
};
</script>

<style scoped>
.upload-content {
  padding: 10px 0;
}
.status-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}
.status-text {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
</style>
