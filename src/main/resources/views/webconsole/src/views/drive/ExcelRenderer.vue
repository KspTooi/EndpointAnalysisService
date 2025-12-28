<template>
  <div class="preview-container" v-loading="loading">
    <vue-office-excel v-if="isXlsx" :src="fileUrl" class="excel-container" @rendered="onRendered" @error="onError" />

    <div v-else-if="xlsHtml" class="xls-container" v-html="xlsHtml"></div>

    <div v-else-if="!loading && !fileUrl" class="empty-tip">无法获取文件链接</div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import axios from "axios";
import * as XLSX from "xlsx"; // 引入 SheetJS

// 引入 vue-office 组件 (使用 .mjs 路径解决 Vite 报错)
import VueOfficeExcel from "@vue-office/excel/lib/v3/vue-office-excel.mjs";
import "@vue-office/excel/lib/v3/index.css";

const route = useRoute();
const loading = ref(false);
const xlsHtml = ref("");

// 获取当前文件名（从URL参数中）
const fileName = computed(() => (route.query.name as string) || "");

// 判断是否为 xlsx 格式
const isXlsx = computed(() => {
  return fileName.value.toLowerCase().endsWith(".xlsx");
});

// 计算下载链接
const fileUrl = computed(() => {
  const sign = route.query.sign as string;
  if (!sign) return "";
  return `/drive/object/access/downloadEntry?sign=${sign}&preview=1`;
});

// 渲染完成回调 (VueOffice)
const onRendered = () => {
  loading.value = false;
};

// 渲染错误回调 (VueOffice)
const onError = (e: any) => {
  loading.value = false;
  console.error("XLSX渲染失败:", e);
  ElMessage.error("预览失败，文件可能已损坏");
};

// 专门处理 .xls 格式的方法
const loadXlsFile = async () => {
  if (!fileUrl.value) return;

  loading.value = true;
  try {
    // 1. 下载二进制文件
    const response = await axios.get(fileUrl.value, {
      responseType: "arraybuffer",
    });

    // 2. 使用 SheetJS 解析
    const data = new Uint8Array(response.data);
    const workbook = XLSX.read(data, { type: "array" });

    // 3. 取第一个 Sheet 转换为 HTML
    const firstSheetName = workbook.SheetNames[0];
    const worksheet = workbook.Sheets[firstSheetName];
    // 使用简单的 HTML 表格转换
    const html = XLSX.utils.sheet_to_html(worksheet);

    xlsHtml.value = html;
  } catch (error) {
    console.error("XLS解析失败:", error);
    ElMessage.error("旧版 Excel (.xls) 解析失败，请下载后查看");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  // 如果是 .xls，手动触发加载逻辑
  if (!isXlsx.value && fileName.value.toLowerCase().endsWith(".xls")) {
    loadXlsFile();
  }
});

// 监听 URL 变化（防止同一个路由组件切换文件不刷新）
watch(
  () => fileUrl.value,
  () => {
    if (!isXlsx.value && fileName.value.toLowerCase().endsWith(".xls")) {
      loadXlsFile();
    }
  }
);
</script>

<style scoped>
.preview-container {
  height: 100%;
  width: 100%;
  overflow: auto; /* 允许滚动 */
  background-color: #fff;
}

.excel-container {
  height: 100vh;
  width: 100%;
}

/* 简单的 .xls 表格样式美化 */
.xls-container {
  padding: 20px;
  width: 100%;
}

:deep(table) {
  border-collapse: collapse;
  width: 100%;
  font-size: 14px;
}

:deep(td),
:deep(th) {
  border: 1px solid #dcdfe6;
  padding: 8px;
  min-width: 50px;
}

:deep(tr:first-child) {
  background-color: #f5f7fa;
  font-weight: bold;
}
</style>
