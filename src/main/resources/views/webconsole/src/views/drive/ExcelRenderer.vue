<template>
  <div class="preview-container" v-loading="loading">
    <vue-office-excel v-if="isXlsx" :src="fileUrl" :options="excelOptions" @rendered="onRendered" @error="onError" />

    <div
      @wheel.capture="handleWheel"
      v-else-if="xlsHtml"
      class="xls-container"
      v-html="xlsHtml"
      :style="{
        transform: `scale(${scale})`,
        transformOrigin: '0 0',
      }"
    ></div>

    <div v-else-if="!loading && !fileUrl" class="empty-tip">无法获取文件链接</div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import axios from "axios";
import * as XLSX from "xlsx";

import VueOfficeExcel from "@vue-office/excel/lib/v3/vue-office-excel.mjs";
import "@vue-office/excel/lib/v3/index.css";
// 1. 引入缩放服务
import DriveRendererService from "./service/DriveRendererService";

const route = useRoute();
const loading = ref(false);
const xlsHtml = ref("");

const fileName = computed(() => (route.query.name as string) || "");

const isXlsx = computed(() => {
  return fileName.value.toLowerCase().endsWith(".xlsx");
});

const fileUrl = computed(() => {
  const sign = route.query.sign as string;
  if (!sign) return "";
  return `/drive/object/access/downloadEntry?sign=${sign}&preview=1`;
});

const onRendered = () => {
  loading.value = false;
};

const onError = (e: any) => {
  loading.value = false;
  console.error("XLSX渲染失败:", e);
  ElMessage.error("预览失败，文件可能已损坏");
};

const excelOptions = {
  transformData: (workbookData: any) => {
    if (!workbookData) {
      return workbookData;
    }

    workbookData.forEach((sheet: any) => {
      if (!sheet.data) {
        return;
      }

      sheet.data.forEach((row: any) => {
        if (!row) {
          return;
        }

        row.forEach((cell: any) => {
          if (!cell || !cell.style) {
            return;
          }

          if (cell.style.fontSize) {
            cell.style.fontSize = Math.round(cell.style.fontSize * 1.2);
          }
        });
      });
    });

    return workbookData;
  },
};

const loadXlsFile = async () => {
  if (!fileUrl.value) return;

  loading.value = true;
  try {
    const response = await axios.get(fileUrl.value, {
      responseType: "arraybuffer",
    });

    const data = new Uint8Array(response.data);
    const workbook = XLSX.read(data, { type: "array" });

    const firstSheetName = workbook.SheetNames[0];
    const worksheet = workbook.Sheets[firstSheetName];
    const html = XLSX.utils.sheet_to_html(worksheet);

    xlsHtml.value = html;
  } catch (error) {
    console.error("XLS解析失败:", error);
    ElMessage.error("旧版 Excel (.xls) 解析失败，请下载后查看");
  } finally {
    loading.value = false;
  }
};

//注入缩放打包
const { scale, handleWheel } = DriveRendererService.useRendererScale({
  maxScale: 5.0,
  minScale: 0.1,
  step: 0.05,
});

onMounted(() => {
  if (!isXlsx.value && fileName.value.toLowerCase().endsWith(".xls")) {
    loadXlsFile();
  }
});

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
  height: calc(100vh - 80px);
  width: 100%;
  overflow: auto; /* 必须允许滚动，因为放大后内容会超出 */
  background-color: #fff;
}

.excel-container {
  /* 确保组件有基础尺寸 */
  min-height: 100vh;
  width: 100%;
}

.xls-container {
  padding: 20px;
  width: 100%;
  /* 确保缩放过渡平滑 */
  transition: transform 0.1s linear;
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
