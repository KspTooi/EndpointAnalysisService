<template>
  <div
    ref="dropZoneRef"
    class="file-selector-dropzone"
    :class="{ 'drag-over': isDragOver }"
    @dragenter.prevent="handleDragEnter"
    @dragover.prevent="handleDragOver"
    @dragleave.prevent="handleDragLeave"
    @drop.prevent="handleDrop"
  >
    <slot></slot>
  </div>
  <input ref="fileInputRef" type="file" :multiple="props.maxSelect > 1" style="display: none" @change="handleFileInputChange" />
</template>

<script setup lang="ts">
import { ref } from "vue";

const props = withDefaults(
  defineProps<{
    maxSelect?: number; //最大文件选择数量
  }>(),
  {
    maxSelect: 1,
  }
);

const emit = defineEmits<{
  (e: "on-file-selected", files: File[]): void;
}>();

const fileInputRef = ref<HTMLInputElement | null>(null);
const dropZoneRef = ref<HTMLElement | null>(null);
const isDragOver = ref(false);
let dragCounter = 0;

/**
 * 打开文件选择器
 */
const openSelector = () => {
  fileInputRef.value?.click();
};

const handleFileInputChange = (event: Event) => {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files.length > 0) {
    const files = Array.from(input.files);
    if (props.maxSelect > 0 && files.length > props.maxSelect) {
      files.splice(props.maxSelect);
    }
    emit("on-file-selected", files);
  }
  input.value = "";
};

const handleDragEnter = (event: DragEvent) => {
  dragCounter++;
  if (event.dataTransfer?.types.includes("Files")) {
    isDragOver.value = true;
  }
};

const handleDragOver = (event: DragEvent) => {
  if (event.dataTransfer?.types.includes("Files")) {
    event.dataTransfer.dropEffect = "copy";
  }
};

const handleDragLeave = (event: DragEvent) => {
  dragCounter--;
  if (dragCounter === 0) {
    isDragOver.value = false;
  }
};

const handleDrop = (event: DragEvent) => {
  dragCounter = 0;
  isDragOver.value = false;

  if (!event.dataTransfer) {
    return;
  }

  const files = Array.from(event.dataTransfer.files);
  if (files.length === 0) {
    return;
  }

  if (props.maxSelect > 0 && files.length > props.maxSelect) {
    const selectedFiles = files.slice(0, props.maxSelect);
    emit("on-file-selected", selectedFiles);
    return;
  }

  emit("on-file-selected", files);
};

defineExpose({
  openSelector,
});
</script>

<style scoped>
.file-selector-dropzone {
  position: relative;
}

.file-selector-dropzone.drag-over {
  outline: 2px dashed #409eff;
  outline-offset: -2px;
  background-color: rgba(64, 158, 255, 0.05);
}
</style>
