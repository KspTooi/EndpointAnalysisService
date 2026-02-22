<template>
  <div class="preview-container">
    <vue-office-docx :src="fileUrl" class="docx-container" @rendered="onRendered" @error="onError" />
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import VueOfficeDocx from "@vue-office/docx/lib/v3/vue-office-docx.mjs";
import "@vue-office/docx/lib/v3/index.css";
import Http from "@/commons/Http";
const route = useRoute();

// 计算下载链接
const fileUrl = computed(() => {
  const sign = route.query.sign as string;
  if (!sign) return "";
  // 直接使用您后端的下载接口，组件会自动下载并解析
  return Http.resolve(`/drive/object/access/downloadEntry?sign=${sign}&preview=1`);
});

const onRendered = () => {
  // 渲染完成回调
};

const onError = (e: any) => {
  console.error("Word渲染失败:", e);
  ElMessage.error("文件预览失败，可能文件已损坏或格式不支持");
};
</script>

<style scoped>
.preview-container {
  height: 100%;
  width: 100%;
  overflow: hidden;
  background-color: #f0f2f5;
}

.docx-container {
  height: 100vh; /* 确保容器高度填满 */
  width: 100%;
}
</style>
