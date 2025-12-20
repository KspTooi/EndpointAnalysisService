<template>
  <div ref="gridRef" class="list-grid" v-loading="loading" @contextmenu.prevent="handleGridRightClick" @mousedown="handleMouseDown">
    <!-- 上级目录 -->
    <DriveEntryParentItem :target-id="parentId" name="上级目录" v-show="parentId" @dblclick="handleReturnParentDir" />

    <!-- 条目列表 -->
    <DriveEntryItem
      v-for="item in data"
      :key="item.id"
      :id="item.id"
      :name="item.name"
      :kind="item.kind"
      :attach-id="item.attachId"
      :attach-size="item.attachSize"
      :attach-suffix="item.attachSuffix"
      :ref="(el) => setEntryRef(item.id, el)"
      :class="{ selected: selectedIds.has(item.id) }"
      @click="handleEntryClick"
      @dblclick="handleEntryDoubleClick"
      @contextmenu="(event: MouseEvent) => handleEntryRightClick(event, item)"
    />

    <!-- 框选矩形 -->
    <div
      v-if="isSelecting"
      class="selection-box"
      :style="{
        left: selectionBox.left + 'px',
        top: selectionBox.top + 'px',
        width: selectionBox.width + 'px',
        height: selectionBox.height + 'px',
      }"
    ></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from "vue";
import DriveEntryItem from "@/views/drive/components/DriveEntryItem.vue";
import DriveEntryParentItem from "@/views/drive/components/DriveEntryParentItem.vue";
import type { GetEntryListVo } from "@/api/drive/DriveApi.ts";

const props = defineProps<{
  data: GetEntryListVo[];
  loading: boolean;
  parentId: string | null;
}>();

const emit = defineEmits<{
  (e: "grid-right-click", event: MouseEvent): void;
  (e: "entry-click", id: string): void;
  (e: "entry-dblclick", id: string, kind: number): void;
  (e: "entry-right-click", event: MouseEvent, entries: GetEntryListVo[]): void;
  (e: "return-parent-dir", parentId: string | null): void;
}>();

const gridRef = ref<HTMLElement | null>(null);
const entryRefs = ref<Map<string, HTMLElement>>(new Map());
const selectedIds = ref<Set<string>>(new Set());
const isSelecting = ref(false);
const selectionBox = ref({
  left: 0,
  top: 0,
  width: 0,
  height: 0,
});

const startX = ref(0);
const startY = ref(0);
const scrollTop = ref(0);

const setEntryRef = (id: string, el: any) => {
  if (!el) {
    return;
  }
  entryRefs.value.set(id, el.$el);
};

const handleGridRightClick = (event: MouseEvent) => {
  if (selectedIds.value.size > 1) {
    const selectedEntries = props.data.filter((item) => selectedIds.value.has(item.id));
    emit("entry-right-click", event, selectedEntries);
    return;
  }
  emit("grid-right-click", event);
};

const handleEntryClick = (id: string) => {
  emit("entry-click", id);
};

const handleEntryDoubleClick = (id: string, kind: number) => {
  emit("entry-dblclick", id, kind);
};

const handleEntryRightClick = (event: MouseEvent, entry: GetEntryListVo) => {
  if (selectedIds.value.size > 1 && selectedIds.value.has(entry.id)) {
    const selectedEntries = props.data.filter((item) => selectedIds.value.has(item.id));
    emit("entry-right-click", event, selectedEntries);
    return;
  }
  emit("entry-right-click", event, [entry]);
};

const handleReturnParentDir = (parentId: string | null) => {
  emit("return-parent-dir", parentId);
};

const handleMouseDown = (event: MouseEvent) => {
  if (event.button !== 0) {
    return;
  }

  const target = event.target as HTMLElement;
  if (target.closest(".drive-entry-item") || target.closest(".drive-entry-parent-item")) {
    return;
  }

  if (!gridRef.value) {
    return;
  }

  const rect = gridRef.value.getBoundingClientRect();
  startX.value = event.clientX - rect.left + gridRef.value.scrollLeft;
  startY.value = event.clientY - rect.top + gridRef.value.scrollTop;
  scrollTop.value = gridRef.value.scrollTop;

  isSelecting.value = true;
  selectedIds.value.clear();

  selectionBox.value = {
    left: startX.value,
    top: startY.value,
    width: 0,
    height: 0,
  };

  document.addEventListener("mousemove", handleMouseMove);
  document.addEventListener("mouseup", handleMouseUp);
};

const handleMouseMove = (event: MouseEvent) => {
  if (!isSelecting.value || !gridRef.value) {
    return;
  }

  const rect = gridRef.value.getBoundingClientRect();
  const currentX = event.clientX - rect.left + gridRef.value.scrollLeft;
  const currentY = event.clientY - rect.top + gridRef.value.scrollTop;

  const left = Math.min(startX.value, currentX);
  const top = Math.min(startY.value, currentY);
  const width = Math.abs(currentX - startX.value);
  const height = Math.abs(currentY - startY.value);

  selectionBox.value = { left, top, width, height };

  updateSelectedEntries();
};

const handleMouseUp = () => {
  if (!isSelecting.value) {
    return;
  }

  isSelecting.value = false;
  document.removeEventListener("mousemove", handleMouseMove);
  document.removeEventListener("mouseup", handleMouseUp);
};

const updateSelectedEntries = () => {
  if (!gridRef.value) {
    return;
  }

  const newSelectedIds = new Set<string>();
  const boxRect = {
    left: selectionBox.value.left,
    top: selectionBox.value.top,
    right: selectionBox.value.left + selectionBox.value.width,
    bottom: selectionBox.value.top + selectionBox.value.height,
  };

  entryRefs.value.forEach((entryEl, id) => {
    const entryRect = entryEl.getBoundingClientRect();
    if (!gridRef.value) {
      return;
    }
    const gridRect = gridRef.value.getBoundingClientRect();

    const entryBox = {
      left: entryRect.left - gridRect.left + gridRef.value.scrollLeft,
      top: entryRect.top - gridRect.top + gridRef.value.scrollTop,
      right: entryRect.right - gridRect.left + gridRef.value.scrollLeft,
      bottom: entryRect.bottom - gridRect.top + gridRef.value.scrollTop,
    };

    if (boxRect.left < entryBox.right && boxRect.right > entryBox.left && boxRect.top < entryBox.bottom && boxRect.bottom > entryBox.top) {
      newSelectedIds.add(id);
    }
  });

  selectedIds.value = newSelectedIds;
};

onMounted(() => {});

onUnmounted(() => {
  document.removeEventListener("mousemove", handleMouseMove);
  document.removeEventListener("mouseup", handleMouseUp);
});
</script>

<style scoped>
.list-grid {
  position: relative;
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
  padding: 10px 0;
  width: calc(100% - 2px);
  height: calc(100vh - 185px);
  /* border: 1px solid var(--el-border-color); */
  overflow-y: auto;
}

.selection-box {
  position: absolute;
  border: 1px solid #409eff;
  background-color: rgba(64, 158, 255, 0.1);
  pointer-events: none;
  z-index: 1000;
}

.drive-entry-item.selected {
  background-color: rgba(64, 158, 255, 0.2) !important;
}
</style>
