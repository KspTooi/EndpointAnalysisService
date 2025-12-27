<template>
  <div ref="gridRef" class="list-grid" v-loading="listLoading" @contextmenu.prevent="onGridRightClick" @mousedown="onMouseDown">
    <!-- 上级目录 -->
    <DriveEntryItem
      v-if="hasParentDir"
      :type="1"
      :currentDir="currentDir"
      @on-click="onEntryClick"
      @on-dblclick="enterDirectory"
      @on-contextmenu="onContextmenu"
      @on-drag-drop="onDrop"
    />

    <!-- 条目列表 -->
    <DriveEntryItem
      v-for="item in listData"
      :key="item.id"
      :entry="item"
      :currentDir="currentDir"
      :ref="(el) => setEntryRef(item.id, el)"
      :class="{ selected: selectedIds.has(item.id) }"
      @on-click="onEntryClick"
      @on-dblclick="enterDirectory"
      @on-contextmenu="onContextmenu"
      @on-drag-start="onDragStart"
      @on-drag-over="onDragOver"
      @on-drag-drop="onDrop"
    />

    <!-- 框选矩形 -->
    <div
      v-if="hasSelecting"
      class="selection-box"
      :style="{
        left: selectBox.left + 'px',
        top: selectBox.top + 'px',
        width: selectBox.width + 'px',
        height: selectBox.height + 'px',
      }"
    ></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, reactive, watch, type Ref } from "vue";
import DriveEntryItem from "@/views/drive/components/DriveEntryItem.vue";
import DriveEntryGridService from "@/views/drive/service/DriveEntryGridService.ts";
import type { CurrentDirPo, EntryPo, GetEntryListDto } from "@/views/drive/api/DriveTypes.ts";

const props = defineProps<{
  //搜索关键词
  keyword: string | null;
}>();

export interface EntryGridEmitter {
  //目录切换
  (e: "on-directory-change", currentDir: CurrentDirPo): void;

  //条目列表加载完成
  (e: "on-entries-loaded", items: EntryPo[], total: number): void;

  //条目单击
  (e: "on-entry-click", entry: EntryPo): void;

  //条目双击
  (e: "on-entry-dblclick", entry: EntryPo): void;

  //拖拽条目
  (e: "on-entry-drag", target: EntryPo, entries: EntryPo[], currentDir: CurrentDirPo): void;

  //右键菜单打开
  (e: "on-entry-contextmenu", entries: EntryPo[], event: MouseEvent): void;
}

const emit = defineEmits<EntryGridEmitter>();

const gridRef = ref<HTMLElement | null>(null);
const entryRefs = ref<Map<string, HTMLElement>>(new Map());

//条目列表打包
const { hasParentDir, currentDir, listQuery, listData, listTotal, listLoading, listLoad } =
  DriveEntryGridService.useEntryList(emit);

//鼠标框选打包
const { hasSelecting, selectBox, selectedIds, onMouseDown, clearSelection } = DriveEntryGridService.useEntrySelection(
  gridRef,
  entryRefs
);

//鼠标拖拽打包
const { onDragStart, onDragOver, onDrop } = DriveEntryGridService.useEntryDrag(listData, selectedIds, emit);

//文件夹导航打包
const { enterDirectory } = DriveEntryGridService.useDirectoryNavigation(listQuery, selectedIds, listLoad);

const setEntryRef = (id: string, el: any) => {
  if (!el) {
    entryRefs.value.delete(id);
    return;
  }
  if (el.$el) {
    entryRefs.value.set(id, el.$el);
  }
};

/**
 * 条目被单击
 * @param entry 条目对象
 */
const onEntryClick = (entry: EntryPo) => {
  emit("on-entry-click", entry);
};

/**
 * 右键菜单事件
 * @param entry 条目对象
 * @param event 鼠标事件
 */
const onContextmenu = (entry: EntryPo, event: MouseEvent) => {
  //如果选了多个且当前右键的也在其中，保持多选状态
  if (selectedIds.value.size > 1 && entry.id && selectedIds.value.has(entry.id)) {
    const selectedEntries = listData.value.filter((item) => selectedIds.value.has(item.id));
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

onMounted(() => {
  listLoad();
});

//监听keyword变更
watch(
  () => props.keyword,
  (newVal: string | null) => {
    listQuery.keyword = newVal;
    listLoad();
  }
);

defineExpose({
  listLoad,
  getCurrentDirId: () => {
    return currentDir.value.id;
  },
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
