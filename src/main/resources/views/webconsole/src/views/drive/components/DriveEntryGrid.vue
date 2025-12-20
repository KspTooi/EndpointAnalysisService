<template>
  <div class="list-grid" v-loading="loading" @contextmenu.prevent="handleGridRightClick">
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
      @click="handleEntryClick"
      @dblclick="handleEntryDoubleClick"
      @contextmenu="(event: MouseEvent) => handleEntryRightClick(event, item)"
    />
  </div>
</template>

<script setup lang="ts">
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
  (e: "entry-right-click", event: MouseEvent, entry: GetEntryListVo): void;
  (e: "return-parent-dir", parentId: string | null): void;
}>();

const handleGridRightClick = (event: MouseEvent) => {
  emit("grid-right-click", event);
};

const handleEntryClick = (id: string) => {
  emit("entry-click", id);
};

const handleEntryDoubleClick = (id: string, kind: number) => {
  emit("entry-dblclick", id, kind);
};

const handleEntryRightClick = (event: MouseEvent, entry: GetEntryListVo) => {
  emit("entry-right-click", event, entry);
};

const handleReturnParentDir = (parentId: string | null) => {
  emit("return-parent-dir", parentId);
};
</script>

<style scoped>
.list-grid {
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
  padding: 10px 0;
  width: calc(100% - 2px);
  height: calc(100vh - 185px);
  /* border: 1px solid var(--el-border-color); */
  overflow-y: auto;
}
</style>
