<template>
  <div ref="gridRef" class="list-grid" v-loading="entryLoading" @contextmenu.prevent="onGridRightClick" @mousedown="handleMouseDown">
    <!-- 上级目录 -->
    <!-- <DriveEntryParentItem
      target-id="0"
      name="上级目录"
      v-show="previousParentId.length > 0"
      @dblclick="onReturnParentDir"
      @dragover="handleParentDragOver"
      @drop="handleParentDrop"
    /> -->

    <!-- 上级目录 -->
    <DriveEntryItem
      v-if="previousParentId.length > 0"
      :type="1"
      @on-click="onEntryClick"
      @on-dblclick="onEntryDoubleClick"
      @on-contextmenu="onRightMenuOpen"
      @on-drag-start="handleEntryDragStart"
      @on-drag-over="handleEntryDragOver"
      @on-drag-drop="handleEntryDrop"
    />

    <!-- 条目列表 -->
    <DriveEntryItem
      v-for="item in entryData"
      :key="item.id as string"
      :entry="item"
      :ref="(el) => setEntryRef(item.id as string, el)"
      :class="{ selected: selectedIds.has(item.id as string) }"
      @on-click="onEntryClick"
      @on-dblclick="onEntryDoubleClick"
      @on-contextmenu="onRightMenuOpen"
      @on-drag-start="handleEntryDragStart"
      @on-drag-over="handleEntryDragOver"
      @on-drag-drop="handleEntryDrop"
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
import { ref, onMounted, onUnmounted, reactive, watch } from "vue";
import DriveEntryItem from "@/views/drive/components/DriveEntryItem.vue";
import DriveEntryParentItem from "@/views/drive/components/DriveEntryParentItem.vue";
import type { GetEntryListDto, GetEntryListVo } from "@/api/drive/DriveApi.ts";
import DriveApi from "@/api/drive/DriveApi.ts";
import type RestPageableView from "@/commons/entity/RestPageableView";
import { ElMessage } from "element-plus";
import MouseInteractionService from "@/views/drive/service/DriveMouseInteractionService.ts";

const props = defineProps<{
  //搜索关键词
  keyword: string | null;
}>();

const emit = defineEmits<{
  //条目加载完成
  (e: "on-entries-loaded", data: GetEntryListVo[], total: number): void;

  //目录切换
  (e: "on-directory-change", targetId: string | null): void;

  //条目单击
  (e: "on-entry-click", entry: GetEntryListVo): void;

  //条目双击
  (e: "on-entry-dblclick", entry: GetEntryListVo): void;

  //拖拽条目
  (e: "on-entry-drag", target: GetEntryListVo, entries: GetEntryListVo[]): void;

  //右键菜单打开
  (e: "on-entry-contextmenu", entries: GetEntryListVo[], event: MouseEvent): void;
}>();

const entryData = ref<GetEntryListVo[]>([]);
const entryTotal = ref(0);
const entryLoading = ref(false);

//上级文件夹列表
const previousParentId = ref<string[]>([]);

const entryForm = reactive<GetEntryListDto>({
  parentId: null,
  keyword: null,
  pageNum: 1,
  pageSize: 50000,
});

/**
 * 加载条目列表
 * @returns 条目列表分页结果
 */
const loadEntries = async (): Promise<RestPageableView<GetEntryListVo>> => {
  entryLoading.value = true;
  try {
    const res = await DriveApi.getEntryList(entryForm);
    if (res.code === 0) {
      entryData.value = res.data;
      entryTotal.value = res.total;
    }
    if (res.code !== 0) {
      ElMessage.error(res.message || "加载列表失败");
    }
    emit("on-entries-loaded", entryData.value, entryTotal.value);
    return res;
  } catch (error: any) {
    entryLoading.value = false;
    ElMessage.error(error.message || "加载列表失败");
    throw error;
  } finally {
    entryLoading.value = false;
  }
};

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
const draggedEntryId = ref<string | null>(null);

const setEntryRef = (id: string, el: any) => {
  if (!el) {
    return;
  }
  entryRefs.value.set(id, el.$el);
};

/**
 * 条目被单击
 * @param entry 条目对象
 */
const onEntryClick = (entry: GetEntryListVo) => {
  emit("on-entry-click", entry);
};

/**
 * 条目被双击
 * @param entry 条目对象
 */
const onEntryDoubleClick = (entry: GetEntryListVo) => {
  //如果entry为null则返回顶级目录
  if (entry == null) {
    entryForm.parentId = null;
    emit("on-directory-change", null);
    loadEntries();
    return;
  }

  //如果双击的是文件夹 则进入目录
  if (entry.kind === 1) {
    previousParentId.value.push(entry.id as string);
    entryForm.parentId = entry.id;
    loadEntries();
    return;
  }

  emit("on-entry-dblclick", entry);
};

/**
 * 回退到上级目录
 * @param targetId 目标ID
 */
const onReturnParentDir = () => {
  if (previousParentId.value.length > 0) {
    previousParentId.value.pop();
    entryForm.parentId = previousParentId.value[previousParentId.value.length - 1];
    loadEntries();
  }
};

/**
 * 右键菜单打开
 * @param entry 条目对象
 * @param event 鼠标事件
 */
const onRightMenuOpen = (entry: GetEntryListVo, event: MouseEvent) => {
  //如果选了多个且当前右键的也在其中，保持多选状态
  if (selectedIds.value.size > 1 && entry.id && selectedIds.value.has(entry.id as string)) {
    const selectedEntries = entryData.value.filter((item) => selectedIds.value.has(item.id as string));
    emit("on-entry-contextmenu", selectedEntries, event);
    return;
  }

  //单选
  emit("on-entry-contextmenu", [entry], event);
};

/**
 * 右键点击容器中没有元素的空白区
 * @param event 鼠标事件
 */
const onGridRightClick = (event: MouseEvent) => {
  emit("on-entry-contextmenu", [], event);
};

/**
 * 鼠标按下
 * @param event 鼠标事件
 */
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

const handleEntryDragStart = (entry: GetEntryListVo, event: DragEvent) => {
  if (!entry.id) {
    return;
  }
  draggedEntryId.value = entry.id;
  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = "move";
  }
};

const handleEntryDragOver = (entry: GetEntryListVo, event: DragEvent) => {
  if (event.dataTransfer) {
    event.dataTransfer.dropEffect = "move";
  }
};

/*
 * 条目拖拽结束
 * @param targetEntry 目标条目
 * @param event 鼠标事件
 */
const handleEntryDrop = (targetEntry: GetEntryListVo, event: DragEvent) => {
  if (!draggedEntryId.value || !targetEntry.id) {
    return;
  }

  const draggedEntry = entryData.value.find((item) => item.id === draggedEntryId.value);
  if (!draggedEntry) {
    return;
  }

  let entriesToDrag: GetEntryListVo[] = [];
  if (selectedIds.value.size > 1 && selectedIds.value.has(draggedEntryId.value)) {
    entriesToDrag = entryData.value.filter((item) => selectedIds.value.has(item.id as string));
  } else {
    entriesToDrag = [draggedEntry];
  }

  if (entriesToDrag.some((item) => item.id === targetEntry.id)) {
    return;
  }

  emit("on-entry-drag", targetEntry, entriesToDrag);
  draggedEntryId.value = null;
};

/*
 * 父级拖拽覆盖
 * @param targetId 目标ID
 * @param event 鼠标事件
 */
const handleParentDragOver = (targetId: string | null, event: DragEvent) => {
  if (event.dataTransfer) {
    event.dataTransfer.dropEffect = "move";
  }
};

/*
 * 父级拖拽结束
 * @param targetId 目标ID
 * @param event 鼠标事件
 */
const handleParentDrop = (targetId: string | null, event: DragEvent) => {
  if (!draggedEntryId.value) {
    return;
  }

  const draggedEntry = entryData.value.find((item) => item.id === draggedEntryId.value);
  if (!draggedEntry) {
    return;
  }

  let entriesToDrag: GetEntryListVo[] = [];
  if (selectedIds.value.size > 1 && selectedIds.value.has(draggedEntryId.value)) {
    entriesToDrag = entryData.value.filter((item) => selectedIds.value.has(item.id as string));
  } else {
    entriesToDrag = [draggedEntry];
  }

  if (targetId && entriesToDrag.some((item) => item.id === targetId)) {
    return;
  }

  const targetEntry: GetEntryListVo = {
    id: targetId || "",
    name: "上级目录",
    kind: 1,
    attachId: null,
    attachSize: "0",
    attachSuffix: null,
    createTime: "",
    parentId: null,
  };

  emit("on-entry-drag", null, entriesToDrag);
  draggedEntryId.value = null;
};

onMounted(() => {
  loadEntries();
});

onUnmounted(() => {
  document.removeEventListener("mousemove", handleMouseMove);
  document.removeEventListener("mouseup", handleMouseUp);
});

//监听keyword变更
watch(
  () => props.keyword,
  (newVal: string | null) => {
    entryForm.keyword = newVal;
    loadEntries();
  }
);

defineExpose({
  loadEntries,
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
