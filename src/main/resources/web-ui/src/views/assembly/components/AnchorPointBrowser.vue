<template>
  <div class="w-full">
    <el-input v-model="anchorPointName" placeholder="请输入或浏览选择导航锚点" clearable>
      <template #append>
        <el-button :disabled="!scmId" @click="openBrowser">浏览</el-button>
      </template>
    </el-input>
  </div>

  <el-dialog v-model="visible" :title="title" width="640px" :close-on-click-modal="false" append-to-body>
    <div class="flex gap-2 mb-3 w-full">
      <el-input v-model="keyword" placeholder="搜索名称 / 路径" clearable />
    </div>
    <el-table
      v-loading="loading"
      :data="filteredList"
      style="cursor: pointer"
      border
      stripe
      highlight-current-row
      height="400px"
      @row-click="onRowClick"
    >
      <el-table-column prop="name" label="名称" min-width="160" show-overflow-tooltip />
      <el-table-column prop="relativePath" label="相对路径" min-width="240" show-overflow-tooltip />
    </el-table>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import type { GetAnchorPointsVo } from "@/views/assembly/api/ScmApi";
import ScmApi from "@/views/assembly/api/ScmApi";
import { ElMessageBox } from "element-plus";

const anchorPointName = defineModel<string>({ required: true, default: "" });

const props = defineProps<{
  scmId: string;
  kind: number;
}>();

const visible = ref(false);
const loading = ref(false);
const keyword = ref("");
const anchorPointList = ref<GetAnchorPointsVo[]>([]);

const title = computed(() => {
  const kindLabel = props.kind === 0 ? "输入" : "输出";
  return `导航锚点浏览器 - ${kindLabel}`;
});

const filteredList = computed(() => {
  const kw = keyword.value.trim().toLowerCase();
  if (!kw) {
    return anchorPointList.value;
  }
  return anchorPointList.value.filter(
    (a) => a.name.toLowerCase().includes(kw) || a.relativePath.toLowerCase().includes(kw)
  );
});

const loadAnchorPoints = async (): Promise<void> => {
  loading.value = true;
  anchorPointList.value = [];
  try {
    anchorPointList.value = await ScmApi.getAnchorPoints({ scmId: props.scmId, kind: props.kind });
  } catch (error: any) {
    ElMessageBox.alert(error.message, "获取锚点失败", { type: "error", confirmButtonText: "确定" });
    visible.value = false;
  } finally {
    loading.value = false;
  }
};

const openBrowser = async (): Promise<void> => {
  keyword.value = "";
  visible.value = true;
  await loadAnchorPoints();
};

watch(
  () => [props.scmId, props.kind],
  () => {
    anchorPointList.value = [];
  }
);

const onRowClick = (row: GetAnchorPointsVo): void => {
  anchorPointName.value = row.relativePath;
  visible.value = false;
};
</script>

<style scoped></style>
